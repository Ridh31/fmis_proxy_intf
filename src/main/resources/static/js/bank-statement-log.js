const username = document.querySelector("[data-username]")?.dataset.username;
const password = document.querySelector("[data-password]")?.dataset.password;
const partnerToken = document.querySelector("[data-partner-token]").dataset.partnerToken;
const basicAuth = btoa(`${username}:${password}`);
const baseUrl = window.location.origin;
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;
const url = `${baseUrl}${apiPrefix}/list-bank-statement`;
const filterBtn = document.querySelector(".filter-button");

let fullData = [];
const jsonDataMap = {};
let modalContent = $(".modal-content");

$(() => {
    modalContent.draggable({
        handle: ".modal-header",
        cursor: "move"
    });

    flatpickr("#statementDate", {
        dateFormat: "d/m/Y",
        allowInput: true
    });

    flatpickr("#importedDate", {
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
    cell.colSpan = 11;
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
 * Asynchronously loads partner options into the select input.
 *
 * Fetches the list of partners from the public API endpoint using basic authentication,
 * then populates the <select> dropdown (`#partnerSelect`) with each partner's identifier and name.
 * Handles API errors gracefully and logs them to the console.
 */
async function loadPartners() {
    const partnerSelect = document.getElementById("partnerSelect");

    try {
        // Make a request to fetch the list of partners using basic auth
        const response = await fetch(`${baseUrl}${apiPrefix}/list-bank-partner`, {
            headers: {
                "Authorization": `Basic ${basicAuth}`
            }
        });

        // Throw an error if the response is not successful
        if (!response.ok) {
            throw new Error(`Failed to fetch partners: ${response.status}`);
        }

        // Parse the JSON response
        const data = await response.json();
        const partners = data?.data?.content || [];

        // Populate the select dropdown with partner options
        partners.forEach(partner => {
            const option = document.createElement("option");
            option.value = partner.id;
            option.textContent = `𓉘${partner.identifier}𓉝 – ${partner.name}`;
            option.className = "info";
            partnerSelect.appendChild(option);
        });
    } catch (error) {
        console.error("Error loading partners: ", error);
    }
}

/**
 * Fetches paginated bank statement data from the API based on filter inputs,
 * aggregates all pages, and updates the table display.
 * Handles loading state, authorization headers, and errors gracefully.
 */
async function fetchData() {
    const tbody = document.querySelector("#logTable tbody");
    tbody.innerHTML = "";
    showLoading();

    const bankId = document.getElementById("partnerSelect").value;
    const account = document.getElementById("bankAccount").value;
    const statementId = document.getElementById("statementId").value;
    const statementDate = document.getElementById("statementDate").value;
    const importedDate = document.getElementById("importedDate").value;
    const status = document.getElementById("statusSelect").value;

    const params = new URLSearchParams();
    params.append("size", 100);
    if (bankId) params.append("bankId", bankId);
    if (account) params.append("bankAccountNumber", account);
    if (statementId) params.append("statementId", statementId);
    if (statementDate) params.append("statementDate", formatDate(statementDate));
    if (importedDate) params.append("importedDate", formatDate(importedDate));
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

            const responseList = data?.data?.content || [];
            allData = allData.concat(responseList);

            totalPages = data?.data?.totalPages || 1;
            currentPage++;
        }

        fullData = allData;
        renderTable();

    } catch (err) {
        console.error("Fetch failed:", err);
        tbody.innerHTML = `<tr><td colspan="10" style="text-align: center; color: red;">Error fetching data.</td></tr>`;
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
 * Renders the DataTable for bank statement logs.
 * Destroys any existing instance, then initializes a new one
 * using `fullData`, sets columns, pagination, and action handlers.
 */
function renderTable() {
    let bankStatementDataTable = $('#logTable');

    if ($.fn.DataTable.isDataTable("#logTable")) {
        bankStatementDataTable.DataTable().destroy();
    }

    bankStatementDataTable.DataTable({
        data: fullData.map((item, i) => {
            const filename = generateFilename(item);
            jsonDataMap[i] = {
                data: item,
                filename: filename
            };
            return [
                i + 1,
                item.bankAccountNumber,
                item.statementId,
                item.statementDate,
                `<span class="${item.status === "Processed" ? "success" : "error"}">${item.status}</span>`,
                item.method,
                item.endpoint,
                item.importedBy || "N/A",
                item.createdDate || "N/A",
                `<span class="view-link" data-index="${i}" onclick="handleViewClick(this)">View</span>`,
                `<span class="download-json" data-index="${i}" onclick="downloadJSON(${i})">📄</span>`
            ];
        }),
        columns: [
            { title: "#" },
            { title: "Bank Account" },
            { title: "Statement ID" },
            { title: "Statement Date" },
            { title: "Status" },
            { title: "Method" },
            { title: "Endpoint" },
            { title: "Imported By" },
            { title: "Imported Date" },
            { title: "Action" },
            { title: "Export" }
        ],
        pageLength: 10,
        lengthMenu: [10, 25, 50, 100, 200],
        destroy: true
    });
}

/**
 * Opens the modal and displays the statement data JSON prettily
 * @param {object} item - The statement data object
 */
function openModal(item) {
    const isDataEmpty =
        item.Data &&
        typeof item.Data === "object" &&
        Object.keys(item.Data).length === 0 &&
        item.status === "Failed" &&
        typeof item.message === "string" &&
        item.message.includes("There are no statement records.");

    if (isDataEmpty) {
        const modal = document.getElementById("modal");
        const body = document.getElementById("modalBody");

        body.innerHTML = `
        <div style="
            background-color: #F8D7DA;
            color: #842029;
            border: 1px solid #F5C2C7;
            border-radius: 8px;
            font-size: 0.85rem;
            padding: 1rem;
            margin-bottom: 1rem;
        ">
            <strong>Details:</strong> ${item.message}
        </div>`;

        modal.style.display = "flex";
        return;
    }

    const modal = document.getElementById("modal");
    const body = document.getElementById("modalBody");

    const message = typeof item.message === "string" ? item.message : "";

    // Get the specific Entry number
    let highlightedIndex = -1;
    const entryMatch = message.match(/Entry:\s*(\d+)/);
    if (entryMatch) {
        highlightedIndex = parseInt(entryMatch[1], 10) - 1;
    }

    // Extract error fields
    const fieldsToHighlight = new Set();
    const fieldMatch = message.match(/CMB_[A-Z_]+/g);
    if (fieldMatch) {
        fieldMatch.forEach(f => fieldsToHighlight.add(f));
    }

    let html = "";

    if (item.Data && Array.isArray(item.Data.CMB_BANKSTM_STG)) {
        const isGlobalError = message && message.includes('Entry: *');
        const showGlobalDetailsBox = isGlobalError;
        const showSpecificDetailsBox = message && Number.isInteger(highlightedIndex);
        const shownDetailBox = new Set();
        const isStatus = item.status === "Processed";

        // 1. Show global error message ONCE at the top
        if (showGlobalDetailsBox && !isStatus) {
            html += `<div style="background-color: #FFF3CD; color: #856404; border: 1px solid #FFEEBA; border-radius: 8px; font-size: 0.85rem; padding: 0.75rem; margin-bottom: 1rem;">
                    <strong>Details: </strong>${message}
                </div>`;
        } else if (isStatus) {
            html += `<div style="background-color: #D4EDDA; color: #155724; border: 1px solid #C3E6CB; border-radius: 8px; font-size: 0.85rem; padding: 0.75rem; margin-bottom: 1rem;">
                    <strong>Details: </strong>${message}
                </div>`;
        }

        // 2. Loop through each entry
        item.Data.CMB_BANKSTM_STG.forEach((entry, i) => {
            const isErrorEntry = i === highlightedIndex;
            const showThisDetailsBox = showSpecificDetailsBox && isErrorEntry && !shownDetailBox.has(i);
            const isHighlighted = isGlobalError || isErrorEntry;

            html += `<div style="margin-bottom: 1rem;">`;

            // 2a. Show specific error above the relevant entry
            if (showThisDetailsBox) {
                html += `<div style="background-color: #FFF3CD; color: #856404; border: 1px solid #FFEEBA; border-radius: 8px; font-size: 0.85rem; padding: 0.75rem; margin-bottom: 1rem;">
                        <strong>Details: </strong>${message}
                    </div>`;
                shownDetailBox.add(i);
            }

            // Container
            html += `<div style="
                    padding:1rem;
                    margin-bottom:1rem;
                    border-radius:8px;
                    font-size:0.85rem;
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    border: 1px solid ${isHighlighted ? '#F5C6CB' : '#E4E4E4'};
                    background-color:${isHighlighted ? '#FCEBEA' : '#F9F9F9'};
                    color: ${isHighlighted ? '#842029' : 'inherit'};
                ">`;

            // Badge
            html += `
                <div style="display: flex; justify-content: flex-start;">
                    <span style="
                        background-color: ${isHighlighted ? '#E6A8A1' : '#B0B0B0'};
                        color: white;
                        padding: 4px 7px;
                        border-radius: 5px;
                        font-size: 0.75rem;
                        font-weight: 600;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    ">
                        Entry ${i + 1}
                    </span>
                </div>`;

            Object.entries(entry).forEach(([key, value]) => {
                const isFieldError = isErrorEntry && fieldsToHighlight.has(key);
                html += `
                    <p style="margin: 4px 0; ${isFieldError ? 'border-left: 4px solid #D9534F; padding-left: 8px; background-color: #FEF7F7;' : ''}">
                        <span style="font-weight: 600;">${key}:</span>
                        <span style="color: ${isHighlighted ? '#B97A78' : '#8C7F77'};"> ${value}</span>
                    </p>`;
            });

            html += `</div>`;
            html += `</div>`;
        });

    } else {
        html += `<pre style="white-space: pre-wrap; font-size: 0.85rem;">${JSON.stringify(item, null, 2)}</pre>`;
    }

    body.innerHTML = html;
    modal.style.display = "flex";
}

/**
 * Closes the modal.
 */
function closeModal() {
    document.getElementById("modal").style.display = "none";
}

/**
 * Handles the "View" action click by retrieving the data item and opening its details in a modal.
 */
function handleViewClick(el) {
    const index = el.getAttribute("data-index");
    const item = fullData[index];
    openModal(item);
}

/**
 * Generates a JSON filename based on available fields:
 * Falls back to combinations or timestamp if fields are missing.
 */
function generateFilename(item) {
    const clean = (str) => str?.replace(/\s+/g, '').toUpperCase();
    const systemCode = clean(item.systemCode);
    const bankAccount = item.bankAccountNumber;
    const date = item.statementDate?.replace(/[-/]/g, '');

    if (systemCode && bankAccount && date) {
        return `${systemCode}-${bankAccount}-${date}.json`;
    }

    if (systemCode && date) {
        return `${systemCode}-${date}.json`;
    }

    if (systemCode && bankAccount) {
        return `${systemCode}-${bankAccount}.json`;
    }

    if (systemCode) {
        return `${systemCode}.json`;
    }

    // Fallback: CMB_BANKSTM_yyyymmddHHMMSS.json
    const now = new Date();
    const timestamp = now.toLocaleString("en-GB", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit", second: "2-digit",
        hour12: false
    }).replace(/[^\d]/g, '');

    return `CMB_BANKSTM_${timestamp}.json`;
}

/**
 * Downloads the JSON file for a given index from jsonDataMap.
 * Falls back to an empty string if no data is available.
 */
function downloadJSON(index) {
    const jsonEntry = jsonDataMap[index];

    // Use .Data, .data, or fallback to empty string
    let dataToDownload = "";
    if (jsonEntry) {
        dataToDownload = jsonEntry.Data || jsonEntry.data || "";
    }

    // Convert object to pretty JSON, or leave as string
    const dataStr = typeof dataToDownload === "object"
        ? JSON.stringify(dataToDownload, null, 4)
        : dataToDownload;

    const blob = new Blob([dataStr], { type: "application/json" });

    // Set filename or use default timestamp
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = (jsonEntry && jsonEntry.filename) || `bank-statement-${Date.now()}.json`;
    link.click();

    URL.revokeObjectURL(link.href);
}

// Filter data
filterBtn.addEventListener("click", () => {
    fetchData();
});

/**
 * Handle logout:
 * - Clear cookies
 * - Redirect to login page
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

// Initial fetch when page loads
loadPartners();
fetchData();