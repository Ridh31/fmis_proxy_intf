const username = document.querySelector("[data-username]")?.dataset.username;
const password = document.querySelector("[data-password]")?.dataset.password;
const partnerToken = document.querySelector("[data-partner-token]").dataset.partnerToken;
const basicAuth = btoa(`${username}:${password}`);
const baseUrl = window.location.origin;
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;
const url = `${baseUrl}${apiPrefix}/list-sarmis-interface`;
const filterBtn = document.querySelector("#filter-button");

let fullData = [];
let logTable = $("#logTable");
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
 * Show error when fetching data
 *
 * @param message - The data to display.
 */
function showError(message) {
    const tbody = document.querySelector("#logTable tbody");
    tbody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 8;
    cell.style.textAlign = "center";
    cell.style.color = "red";
    cell.style.fontWeight = "bold";
    cell.style.padding = "0.75rem";
    cell.innerText = message || "Error fetching data.";
    row.appendChild(cell);
    tbody.appendChild(row);
}

/**
 * Fetches paginated data based on filter inputs and updates the table display.
 */
async function fetchData() {
    showLoading();

    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().ajax.reload();
    } else {
        renderTable();
    }
}

/**
 * Formats a date string into DD-MM-YYYY format.
 * @param {string} input - The input date string.
 * @returns {string} Formatted date as 'DD-MM-YYYY'.
 */
function formatDate(input) {
    if (!input) return null;
    const [day, month, year] = input.split('/');
    if (!day || !month || !year) return null;
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

    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().destroy();
    }

    logTable.DataTable({
        serverSide: true,
        processing: true,
        scrollX: true,
        pageLength: 10,
        lengthMenu: [10, 25, 50, 100],
        ajax: {
            url: url,
            type: "GET",
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            },
            data: function(d) {
                const endpoint = $("#categorySelect").val();
                if (endpoint) d.endpoint = endpoint;

                const interfaceCode = $("#interfaceCode").val();
                if (interfaceCode) d.interfaceCode = interfaceCode;

                const purchaseOrderId = $("#purchaseOrderId").val();
                if (purchaseOrderId) d.purchaseOrderId = purchaseOrderId;

                const actionDate = $("#actionDate").val();
                if (actionDate) d.actionDate = formatDate(actionDate);

                const status = $("#statusSelect").val();
                if (status) d.status = status;

                d.size = d.length;
                d.page = d.start / d.length;
            },
            beforeSend: showLoading,
            complete: hideLoading,
            dataSrc: function(json) {
                fullData = json?.data?.content || [];
                json.recordsTotal = json?.data?.totalElements || 0;
                json.recordsFiltered = json?.data?.totalElements || 0;

                return fullData.map((item, i) => [
                    i + 1 + ((json?.data?.pageable?.pageNumber || 0) * (json?.data?.pageable?.pageSize || fullData.length)),
                    item.interfaceCode,
                    item.method,
                    item.endpoint,
                    `<span class="${item.status === true ? "success" : "error"}">${item.status === true ? "Processed" : "Failed"}</span>`,
                    item.createdDate ? formatDateTime(item.createdDate) : "N/A",
                        `<span class="view-link" data-index="${i}" data-type="payload" data-status="${item.status}">📄 View</span>`,
                        `<span class="view-link" data-index="${i}" data-type="response" data-status="${item.status}">📑 Preview</span>`
                ]);
            },
            error: function(xhr, status, error) {
                showError("Error fetching data.");
                showToast("error", "Error fetching data.");
            }
        },
        columns: [
            { title: "#" },
            { title: "Interface Code" },
            { title: "Method" },
            { title: "Endpoint" },
            { title: "Status" },
            { title: "Action Date" },
            { title: "Payload" },
            { title: "Response" }
        ]
    });
}

/**
 * Opens the modal and displays the data in a formatted and styled way.
 *
 * @param {Object|string} item - The data to display (JSON object or string).
 * @param {string} type - The type of content (e.g., 'payload', 'response').
 * @param {string} [status="true"] - The status value; determines background color.
 */
function openModal(item, type, status = true) {
    const modal = document.getElementById("modal");
    const body = document.getElementById("modalBody");
    const title = document.getElementById("modalTitle");

    // Ensure status is boolean
    const isFailed = (status === false || status === "false");

    let content = "";

    try {
        if (!item || (typeof item === "object" && Object.keys(item).length === 0)) {
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
            let jsonStr;
            if (typeof item === "string") {
                // simple string, not JSON
                jsonStr = item;
            } else {
                // JSON object
                jsonStr = JSON.stringify(item, null, 2);

                // Optional: highlight some keys
                const highlightRegex = /("(purchase_order_id|receipt_id|interface_code)"\s*:\s*)"([^"]+)"/g;
                jsonStr = jsonStr.replace(highlightRegex, (_, keyPrefix, keyName, value) => {
                    let color = "", textColor = "";
                    switch (keyName) {
                        case "purchase_order_id": color = "yellow"; textColor = "#000"; break;
                        case "receipt_id": color = "#CCE5FF"; textColor = "#003366"; break;
                        case "interface_code": color = "#D3F9D8"; textColor = "#2C6E49"; break;
                    }
                    return `${keyPrefix}"<span style="background-color: ${color}; font-weight: bold; color: ${textColor};">${value}</span>"`;
                });
            }

            const bgColor = isFailed ? "#FFF5F5" : "#F9F9F9";
            const borderColor = isFailed ? "#F5C6CB" : "#E4E4E4";

            content = `
                <pre style="
                    white-space: pre-wrap;
                    font-size: 0.85rem;
                    font-family: 'JetBrains Mono', monospace;
                    color: #333;
                    background-color: ${bgColor};
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

// Filter data on filter button click
filterBtn.addEventListener("click", () => {
    fetchData();
});

// Delegate click for dynamically created .view-link elements
logTable.on("click", ".view-link", function () {
    const index = $(this).data("index");
    const type = $(this).data("type");
    const status = $(this).data("status");
    const item = fullData[index];

    let parsed;

    try {
        const raw = item[type];

        if (!raw || raw.trim() === "") {
            parsed = null;
        } else {
            try {
                parsed = JSON.parse(raw);
            } catch {
                parsed = raw; // fallback to string
            }
        }
    } catch {
        parsed = null;
    }

    openModal(parsed, type, status);
});

// Initial data fetch on page load
fetchData();