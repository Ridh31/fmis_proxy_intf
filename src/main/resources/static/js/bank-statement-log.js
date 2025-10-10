const username = document.querySelector("[data-username]")?.dataset.username;
const password = document.querySelector("[data-password]")?.dataset.password;
const partnerToken = document.querySelector("[data-partner-token]").dataset.partnerToken;
const basicAuth = btoa(`${username}:${password}`);
const baseUrl = window.location.origin;
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;
const url = `${baseUrl}${apiPrefix}/list-bank-statement`;
const filterBtn = document.querySelector("#filter-button");

let fullData = [];
const jsonDataMap = {};
const logTable = $("#logTable");
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
    cell.colSpan = 12;
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
    cell.colSpan = 12;
    cell.style.textAlign = "center";
    cell.style.color = "red";
    cell.style.fontWeight = "bold";
    cell.style.padding = "0.75rem";
    cell.innerText = message || "Error fetching data.";
    row.appendChild(cell);
    tbody.appendChild(row);
}

/**
 * Asynchronously loads partner options into the select input.
 *
 * Fetches the list of partners from the public API endpoint using basic authentication,
 * then populates the <select> dropdown (`#partner`) with each partner's identifier and name.
 * Handles API errors gracefully and logs them to the console.
 */
async function loadPartners() {
    const partnerSelect = document.getElementById("partner");

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
        showToast("error", "Error loading partners.");
    }
}

/**
 * Loads or refreshes the DataTable.
 * - Calls renderTable() on first load.
 * - Uses ajax.reload() for filter changes.
 * - Maintains custom loading state with showLoading() / hideLoading().
 */
async function fetchData() {
    showLoading();

    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().ajax.reload();
        return;
    }

    renderTable();
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
 * Initializes the DataTable for bank statement logs with server-side processing.
 * - Destroys any existing table instance.
 * - Fetches only the current page from the server using filters.
 * - Shows a custom spinner during AJAX requests.
 * - Maps server response to columns and action buttons.
 */
function renderTable() {

    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().destroy();
    }

    logTable.DataTable({
        serverSide: true,
        processing: true,
        pageLength: 10,
        scrollX: true,
        scrollCollapse: true,
        lengthMenu: [10, 25, 50, 100, 200],
        fixedColumns: { leftColumns: 2 },
        ajax: {
            url: url,
            type: "GET",
            data: function (d) {
                // Map DataTables params to backend params
                const page = Math.floor(d.start / d.length);
                const params = {
                    page: page,
                    size: d.length,
                    bankId: document.getElementById("partner").value || undefined,
                    bankAccountNumber: document.getElementById("bankAccount").value || undefined,
                    statementId: document.getElementById("statementId").value || undefined,
                    statementDate: document.getElementById("statementDate").value
                        ? formatDate(document.getElementById("statementDate").value)
                        : undefined,
                    importedDate: document.getElementById("importedDate").value
                        ? formatDate(document.getElementById("importedDate").value)
                        : undefined,
                    status: document.getElementById("status").value || undefined
                };
                return params;
            },
            beforeSend: function () {
                showLoading();
            },
            complete: function () {
                hideLoading();
            },
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            },
            dataSrc: function (json) {
                json.recordsTotal = json?.data?.totalElements || 0;
                json.recordsFiltered = json?.data?.totalElements || 0;

                return json?.data?.content || [];
            },
            error: function(xhr, status, error) {
                showError("Error fetching data.");
                showToast("error", "Error fetching data.");
            }
        },
        columns: [
            { data: null, title: "#", render: (data, type, row, meta) => meta.row + 1 },
            { data: "bankAccountNumber", title: "Bank Account" },
            { data: "statementId", title: "Statement ID" },
            { data: "statementDate", title: "Statement Date" },
            { data: "status", title: "Status", render: (data) => `<span class="${data === "Processed" ? "success" : "error"}">${data}</span>` },
            { data: "method", title: "Method" },
            { data: "endpoint", title: "Endpoint" },
            { data: "branch", title: "Branch", defaultContent: "N/A" },
            { data: "importedBy", title: "Imported By", defaultContent: "N/A" },
            { data: "createdDate", title: "Imported Date", defaultContent: "N/A" },
            { data: null, title: "Record", render: (data, type, row, meta) => `<span class="view-link">View</span>` },
            { data: null, title: "Export", render: (data, type, row, meta) => `<span class="download-json">📄</span>` }
        ]
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

    let html = "";

    const modal = document.getElementById("modal");
    const body = document.getElementById("modalBody");
    const message = typeof item.message === "string" ? item.message : "";
    const hasCMB = message.includes("CMB_");
    const hasEntryStar = message.includes("Entry: *");
    const hasEntries = item.Data && Array.isArray(item.Data.CMB_BANKSTM_STG) && item.Data.CMB_BANKSTM_STG.length > 0;

    // Fallback error without CMB or Entry: *
    if (item.status === "Failed" && !hasCMB && !hasEntryStar) {
        html += `<div style="
            background-color: #F8D7DA;
            color: #842029;
            border: 1px solid #F5C2C7;
            border-radius: 8px;
            font-size: 0.85rem;
            padding: 1rem;
            margin-bottom: 1rem;">
            <strong>Error:</strong> ${message || "Unknown error"}
        </div>`;

        // Show entries like success style if available
        if (hasEntries) {
            item.Data.CMB_BANKSTM_STG.forEach((entry, i) => {
                html += `<div style="
                    padding:1rem;
                    margin-bottom:1rem;
                    border-radius:8px;
                    font-size:0.85rem;
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    border: 1px solid #E4E4E4;
                    background-color:#F9F9F9;
                ">`;

                // Badge for Entry
                html += `<div style="display:flex; justify-content:flex-start; margin-bottom:4px;">
                    <span style="
                        background-color: #4C5F85;
                        color: white;
                        padding: 4px 7px;
                        border-radius: 5px;
                        font-size: 0.75rem;
                        font-weight: 600;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    ">Entry ${i + 1}</span>
                </div>`;

                Object.entries(entry).forEach(([key, value]) => {
                    html += `<p style="margin:4px 0;">
                        <span style="font-weight:600;">${key}:</span>
                        <span style="color:#8C7F77;"> ${value}</span>
                    </p>`;
                });

                html += `</div>`;
            });
        }

        body.innerHTML = html;
        modal.style.display = "flex";
        return;
    }

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

    if (item.Data && Array.isArray(item.Data.CMB_BANKSTM_STG)) {
        const isGlobalError = message && message.includes("Entry: *");
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
                        background-color: ${isHighlighted ? '#EF4444' : '#3B82F6'};
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
 * Generates a JSON filename based on available fields.
 * Falls back to timestamped default if required fields are missing.
 */
function generateFilename(item) {
    const clean = (str) =>
        typeof str === "string" ? str.replace(/\s+/g, "").toUpperCase() : null;

    const systemCode = clean(item.systemCode);
    const bankAccount = item.bankAccountNumber || null;
    const date = item.statementDate?.replace(/[-/]/g, "") || null;

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
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: false
    }).replace(/[^\d]/g, "");

    return `CMB_BANKSTM_${timestamp}.json`;
}

/**
 * Downloads the JSON file for a given index from jsonDataMap.
 * Performs cleanup before generating and downloading the file.
 */
function downloadJSON(item) {
    if (!item) return;

    // Use the row’s Data or data property
    const originalData = item.Data || item.data || {};
    const data = JSON.parse(JSON.stringify(originalData));

    // Cleanup fields
    delete data.createdDateFormatted;

    if (Array.isArray(data?.Data?.CMB_BANKSTM_STG)) {
        data.Data.CMB_BANKSTM_STG.forEach((entry) => {
            delete entry.CMB_BANK_CODE;
        });
    }

    const jsonString = JSON.stringify(data, null, 4);
    const blob = new Blob([jsonString], { type: "application/json" });

    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);

    // Use pre-defined filename or generate one
    link.download = item.filename || generateFilename(item);

    link.click();
    URL.revokeObjectURL(link.href);
}


// Filter data
filterBtn.addEventListener("click", () => {
    fetchData();
});

// View modal
logTable.on("click", ".view-link", function () {
    const row = $(this).closest("tr");
    const data = logTable.DataTable().row(row).data();
    openModal(data);
});

// Download JSON
logTable.on("click", ".download-json", function () {
    const row = $(this).closest("tr");
    const data = logTable.DataTable().row(row).data();
    downloadJSON(data);
});

// Initial fetch when page loads
loadPartners();
fetchData();