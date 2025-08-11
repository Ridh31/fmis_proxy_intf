const username = document.querySelector("[data-username]")?.dataset.username;
const password = document.querySelector("[data-password]")?.dataset.password;
const partnerToken = document.querySelector("[data-partner-token]").dataset.partnerToken;
const basicAuth = btoa(`${username}:${password}`);
const baseUrl = window.location.origin;
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;
const url = `${baseUrl}${apiPrefix}/list-sarmis-interface`;
const filterBtn = document.querySelector(".filter-button");

let fullData = [];
let modalContent = $(".modal-content");

$(() => {
    modalContent.draggable({
        handle: ".modal-header",
        cursor: "move"
    });

    flatpickr("#actionDate", {
        dateFormat: "d/m/Y",
        allowInput: true
    });
});

/**
 * Displays a loading message in the table body while data is being fetched.
 */
function showLoading() {
    const tbody = document.querySelector("#logTable tbody");
    tbody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 8;
    cell.style.textAlign = "center";
    cell.style.margin = "0.75rem";
    cell.style.fontWeight = "bold";
    cell.innerText = "Loading...";
    row.appendChild(cell);
    tbody.appendChild(row);
}

/**
 * Removes the loading message row from the table body if present.
 */
function hideLoading() {
    const tbody = document.querySelector("#logTable tbody");
    const rows = tbody.querySelectorAll("tr");

    rows.forEach(row => {
        if (row.textContent.trim() === "Loading...") {
            tbody.removeChild(row);
        }
    });
}

/**
 * Fetches paginated data based on filter inputs and updates the table display.
 */
async function fetchData() {
    const tbody = document.querySelector("#logTable tbody");
    tbody.innerHTML = "";
    showLoading();

    const categorySelect = document.getElementById("categorySelect").value;
    const interfaceCode = document.getElementById("interfaceCode").value;
    const purchaseOrderId = document.getElementById("purchaseOrderId").value;
    const actionDate = document.getElementById("actionDate").value;
    const status = document.getElementById("statusSelect").value;

    const params = new URLSearchParams();
    params.append("size", 100);
    if (categorySelect) params.append("endpoint", categorySelect);
    if (interfaceCode) params.append("interfaceCode", interfaceCode);
    if (purchaseOrderId) params.append("purchaseOrderId", purchaseOrderId);
    if (actionDate) params.append("actionDate", formatDate(actionDate));
    if (status) params.append("status", status);

    try {
        let allData = [];
        let currentPage = 0;
        let totalPages = 1;

        while (currentPage < totalPages) {
            params.set("page", currentPage);

            const response = await fetch(`${url}?${params.toString()}`, {
                headers: {
                    "Authorization": `Basic ${basicAuth}`,
                    "X-Partner-Token": partnerToken
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            if (!data?.data?.content) {
                throw new Error("Invalid data structure received.");
            }

            const responseList = data.data.content;
            allData = allData.concat(responseList);

            totalPages = data.data.totalPages || 1;
            currentPage++;
        }

        fullData = allData;
        renderTable();

    } catch (err) {
        console.error("Fetch failed:", err);
        tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; color: red;">Error fetching data: ${err.message}</td></tr>`;
    } finally {
        hideLoading();
    }
}

/**
 * Formats a date string into DD-MM-YYYY format.
 * @param {string} input - The input date string.
 * @returns {string} Formatted date as 'DD-MM-YYYY'.
 */
function formatDate(input) {
    const [day, month, year] = input.split('/');
    return `${day}-${month}-${year}`;
}

/**
 * Converts an ISO date string
 * into a human-readable format: "DD-MM-YYYY hh:mm AM/PM".
 *
 * @param {string} input - The ISO date string to format.
 * @returns {string} Formatted date string or "N/A"/"Invalid date" if input is null/invalid.
 */
function formatDateTime(input) {
    if (!input) return "N/A";

    const date = new Date(input);

    if (isNaN(date)) return "Invalid date";

    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();

    let hours = date.getHours();
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const ampm = hours >= 12 ? "PM" : "AM";

    hours = hours % 12;
    hours = hours ? hours : 12;

    return `${day}-${month}-${year} ${String(hours).padStart(2, "0")}:${minutes} ${ampm}`;
}

/**
 * Renders the DataTable for logs.
 */
function renderTable() {
    let bankStatementDataTable = $('#logTable');

    if ($.fn.DataTable.isDataTable("#logTable")) {
        bankStatementDataTable.DataTable().destroy();
    }

    bankStatementDataTable.DataTable({
        data: fullData.map((item, i) => [
            i + 1,
            item.interfaceCode,
            item.method,
            item.endpoint,
            `<span class="${item.status === true ? "success" : "error"}">${item.status === true ? "Processed" : "Failed"}</span>`,
            item.createdDate ? formatDateTime(item.createdDate) : "N/A",
            `<span class="view-link" data-index="${i}" data-type="payload" data-status="${item.status}" onclick="handleViewClick(this)">📄 View</span>`,
            `<span class="view-link" data-index="${i}" data-type="response" data-status="${item.status}" onclick="handleViewClick(this)">📑 Preview</span>`
        ]),
        columns: [
            { title: "#" },
            { title: "Interface Code" },
            { title: "Method" },
            { title: "Endpoint" },
            { title: "Status" },
            { title: "Action Date" },
            { title: "Payload" },
            { title: "Response" }
        ],
        pageLength: 10,
        lengthMenu: [10, 25, 50, 100],
        scrollX: true,
        destroy: true
    });
}

/**
 * Opens the modal and displays the data in a formatted and styled way.
 *
 * @param {Object|string} item - The data to display (JSON object or string).
 * @param {string} type - The type of content (e.g., 'payload', 'response').
 * @param {string} [status="true"] - The status value; determines background color.
 */
function openModal(item, type, status = "true") {
    const modal = document.getElementById("modal");
    const body = document.getElementById("modalBody");
    const title = document.getElementById("modalTitle");

    let content = "";

    try {
        if (!item || (typeof item === "object" && Object.keys(item).length === 0)) {
            // Show fallback for missing or empty data
            content = `
                <div style="
                    padding: 1rem;
                    color: #888;
                    font-style: italic;
                    background-color: #FEFEFE;
                    border: 1px dashed #CCC;
                    border-radius: 8px;">
                    No data
                </div>
            `;
        } else {
            // Format JSON
            let jsonStr = JSON.stringify(item, null, 2);

            // Highlight key values
            const highlightRegex = /("(purchase_order_id|receipt_id|interface_code)"\s*:\s*)"([^"]+)"/g;
            jsonStr = jsonStr.replace(
                highlightRegex,
                (_, keyPrefix, keyName, value) => {
                    let color = "", textColor = "";

                    switch (keyName) {
                        case "purchase_order_id":
                            color = "yellow";
                            textColor = "#000";
                            break;
                        case "receipt_id":
                            color = "#CCE5FF";
                            textColor = "#003366";
                            break;
                        case "interface_code":
                            color = "#D3F9D8";
                            textColor = "#2C6E49";
                            break;
                    }

                    return `${keyPrefix}"<span style="background-color: ${color}; font-weight: bold; color: ${textColor};">${value}</span>"`;
                }
            );

            // Choose background and border colors based on status
            const backgroundColor = status === "false" ? "#FFF5F5" : "#F9F9F9";
            const borderColor = status === "false" ? "#F5C6CB" : "#E4E4E4";

            // Final content
            content = `
                <pre style="
                    white-space: pre-wrap;
                    font-size: 0.85rem;
                    font-family: 'Courier New', Courier, monospace;
                    color: #333;
                    background-color: ${backgroundColor};
                    border: 1px solid ${borderColor};
                    padding: 1rem;
                    border-radius: 8px;
                    max-height: 60vh;
                    overflow-y: auto;
                ">${jsonStr}</pre>
            `;
        }
    } catch (e) {
        content = `<pre style="color: red;">Failed to render content.</pre>`;
    }

    // Capitalize first letter of type
    title.textContent = type.charAt(0).toUpperCase() + type.slice(1);

    body.innerHTML = content;
    modal.style.display = "flex";
}

/**
 * Closes the modal.
 */
function closeModal() {
    document.getElementById("modal").style.display = "none";
}

/**
 * Handles the "View" action click by opening modal with item details.
 */
function handleViewClick(el) {
    const index = el.getAttribute("data-index");
    const type = el.getAttribute("data-type");
    const status = el.getAttribute("data-status");
    const item = fullData[index];

    let parsed;

    try {
        const raw = item[type];

        if (!raw || raw.trim() === "") {
            parsed = null;
        } else {
            parsed = JSON.parse(raw);
        }
    } catch {
        parsed = null;
    }

    openModal(parsed, type, status);
}

// Filter data on filter button click
filterBtn.addEventListener("click", () => {
    fetchData();
});

/**
 * Handle logout by clearing cookies and redirecting to the login page.
 */
document.querySelector(".btn-logout")?.addEventListener("click", () => {
    const deleteCookie = name => {
        document.cookie = `${name}=; Max-Age=0; path=/; SameSite=Lax;`;
    };

    deleteCookie("isAdmin");
    deleteCookie("adminUsername");
    deleteCookie("adminPassword");

    window.location.href = `${apiPrefix}/admin/login`;
});

// Initial data fetch on page load
fetchData();