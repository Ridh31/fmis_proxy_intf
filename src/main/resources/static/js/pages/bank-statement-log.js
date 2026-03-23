const url = `${baseUrl}${apiPrefix}/list-bank-statement`;

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
 * - Maintains custom loading state with showLoading(colSpan) / hideLoading().
 */
async function fetchData() {
    showLoading(12);

    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().ajax.reload();
        return;
    }

    renderTable();
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
        lengthMenu: [10, 25, 50, 100, 200, 1000],
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
                showLoading(12);
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
                showError(12);
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
 * Fetches ALL filtered data (no pagination) and exports to Excel.
 * Excludes the raw JSON "Data" field.
 */
async function exportToExcel() {
    showToast("info", "Preparing Excel export...");

    const params = new URLSearchParams({
        page: 0,
        size: 10000,
        ...(document.getElementById("partner").value && { bankId: document.getElementById("partner").value }),
        ...(document.getElementById("bankAccount").value && { bankAccountNumber: document.getElementById("bankAccount").value }),
        ...(document.getElementById("statementId").value && { statementId: document.getElementById("statementId").value }),
        ...(document.getElementById("statementDate").value && { statementDate: formatDate(document.getElementById("statementDate").value) }),
        ...(document.getElementById("importedDate").value && { importedDate: formatDate(document.getElementById("importedDate").value) }),
        ...(document.getElementById("status").value && { status: document.getElementById("status").value }),
    });

    try {
        const response = await fetch(`${url}?${params}`, {
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            }
        });

        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const json = await response.json();
        const rows = json?.data?.content || [];

        if (rows.length === 0) {
            showToast("warning", "No data to export.");
            return;
        }

        const exportData = rows.map((row, i) => ({
            "#": i + 1,
            "Bank Account": row.bankAccountNumber ?? "",
            "Statement ID": row.statementId ?? "",
            "Statement Date": row.statementDate ?? "",
            "Status": row.status ?? "",
            "Method": row.method ?? "",
            "Endpoint": row.endpoint ?? "",
            "Branch": row.branch ?? "",
            "Imported By": row.importedBy ?? "",
            "Imported Date": row.createdDate ?? ""
        }));

        // Auto column width
        const colWidths = Object.keys(exportData[0]).map(key => ({
            wch: Math.max(key.length, ...exportData.map(r => String(r[key] ?? "").length)) + 2
        }));

        const worksheet = XLSX.utils.json_to_sheet(exportData);
        worksheet["!freeze"] = { xSplit: 0, ySplit: 1 };
        worksheet["!cols"] = colWidths;

        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Bank Statement Log");

        const filename = `Bank Statement (${new Date().toISOString().slice(0, 10)}).xlsx`;
        XLSX.writeFile(workbook, filename);
        showToast("success", "Excel exported successfully.");

    } catch (error) {
        console.error("Export error:", error);
        showToast("error", "Failed to export Excel.");
    }
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
        (
            item.message.includes("There are no statement records.") ||
            item.message.includes("Missing or invalid Data was provided.")
        );

    if (isDataEmpty) {
        const modal = document.getElementById("modal");
        const body = document.getElementById("modalBody");
        body.innerHTML = `
        <div style="padding:10px 14px; border-radius:8px; font-size:12px; line-height:1.5;
            background:#FEF2F2; border:1px solid #FECACA; color:#991B1B;">
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

    const kvRow = (key, value, isError = false, isHighlighted = false) => `
        <div style="display:grid; grid-template-columns:190px 1fr; padding:5px 14px;
            font-size:12px; border-bottom:1px solid #F3F4F6;
            ${isError ? 'background:#FEF2F2; border-left:3px solid #EF4444; padding-left:11px;' : ''}">
            <span style="font-weight:600; color:${isHighlighted ? '#991B1B' : '#111827'};">${key}</span>
            <span style="color:${isError ? '#DC2626' : isHighlighted ? '#B91C1C' : '#374151'}; ${isError ? 'font-weight:500;' : ''}">${value}</span>
        </div>`;

    const entryHeader = (index, entry = {}, isHighlighted = false) => {
        const colors = ['#3B82F6', '#8B5CF6', '#0891B2', '#059669'];
        const color = isHighlighted ? '#DC2626' : colors[index % colors.length];
        const bankCode = entry.CMB_BANK_CODE || "";
        const bankAccount = entry.CMB_BANK_ACCOUNT_N || "";
        const meta = bankCode && bankAccount ? `${bankCode} | ${bankAccount}` : bankCode || bankAccount || "";
        return `
        <div style="padding:8px 14px; display:flex; align-items:center; justify-content:space-between;
            background:${isHighlighted ? '#FEF2F2' : '#F0F2F5'};
            border-bottom:1px solid ${isHighlighted ? '#FECACA' : '#E5E7EB'};">
            <span style="font-size:11px; font-weight:700; letter-spacing:0.06em;
                text-transform:uppercase; color:${color};">
                Entry ${index + 1}
            </span>
            ${meta ? `<span style="font-size:11px; color:#6B7280; font-family:monospace;">${meta}</span>` : ''}
        </div>`;
    };

    const detailsBox = (msg, type = "warning") => {
        const styles = {
            warning: { bg: '#FFFBEB', border: '#FDE68A', color: '#92400E' },
            success: { bg: '#F0FDF4', border: '#BBF7D0', color: '#166534' },
            error:   { bg: '#FEF2F2', border: '#FECACA', color: '#991B1B' }
        };
        const s = styles[type];
        return `<div style="padding:10px 14px; border-radius:8px; font-size:12px; line-height:1.5;
        margin: 12px 0;
        background:${s.bg}; border:1px solid ${s.border}; color:${s.color};">
        <strong>Details:</strong> ${msg}
    </div>`;
    };

    const entryCard = (content, isHighlighted = false) => `
        <div style="border-radius:10px; overflow:hidden; margin-bottom:12px;
            border:1px solid ${isHighlighted ? '#FECACA' : '#E5E7EB'};">
            ${content}
        </div>`;

    // Fallback error without CMB or Entry: *
    if (item.status === "Failed" && !hasCMB && !hasEntryStar) {
        html += detailsBox(message || "Unknown error", "error");

        if (hasEntries) {
            item.Data.CMB_BANKSTM_STG.forEach((entry, i) => {
                let cardContent = entryHeader(i, entry);
                cardContent += `<div>`;
                Object.entries(entry)
                    .filter(([key]) => key !== "CMB_BANK_CODE")
                    .forEach(([key, value]) => {
                        cardContent += kvRow(key, value);
                    });
                cardContent += `</div>`;
                html += entryCard(cardContent);
            });
        }

        body.innerHTML = html;
        modal.style.display = "flex";
        return;
    }

    // Get highlighted entry index
    let highlightedIndex = -1;
    const entryMatch = message.match(/Entry:\s*(\d+)/);
    if (entryMatch) highlightedIndex = parseInt(entryMatch[1], 10) - 1;

    // Extract error fields
    const fieldsToHighlight = new Set();
    const fieldMatch = message.match(/CMB_[A-Z_]+/g);
    if (fieldMatch) fieldMatch.forEach(f => fieldsToHighlight.add(f));

    if (item.Data && Array.isArray(item.Data.CMB_BANKSTM_STG)) {
        const isGlobalError = message.includes("Entry: *");
        const isStatus = item.status === "Processed";
        const shownDetailBox = new Set();

        const totalEntries = item.Data.CMB_BANKSTM_STG.length;
        html += `
            <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
                <span style="margin-left:auto; font-size:11px; background:#F3F4F6; color:#6B7280; padding:2px 8px; border-radius:20px;">${totalEntries} entr${totalEntries !== 1 ? 'ies' : 'y'}</span>
            </div>`;

        // Global message at top
        if (isGlobalError && !isStatus) {
            html += detailsBox(message, "warning");
        } else if (isStatus) {
            html += detailsBox(message, "success");
        }

        item.Data.CMB_BANKSTM_STG.forEach((entry, i) => {
            const isErrorEntry = i === highlightedIndex;
            const isHighlighted = isGlobalError || isErrorEntry;

            // Specific error box above relevant entry
            if (isErrorEntry && !shownDetailBox.has(i)) {
                html += detailsBox(message, "warning");
                shownDetailBox.add(i);
            }

            let cardContent = entryHeader(i, entry, isHighlighted);
            cardContent += `<div>`;
            Object.entries(entry)
                .filter(([key]) => key !== "CMB_BANK_CODE")
                .forEach(([key, value]) => {
                    const isFieldError = isErrorEntry && fieldsToHighlight.has(key);
                    cardContent += kvRow(key, value, isFieldError, isHighlighted);
                });
            cardContent += `</div>`;
            html += entryCard(cardContent, isHighlighted);
        });

    } else {
        html += `<pre style="white-space:pre-wrap; font-size:12px; color:#374151;">${JSON.stringify(item, null, 2)}</pre>`;
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

document.getElementById("exportExcel").addEventListener("click", exportToExcel);

// Initial fetch when page loads
loadPartners();
fetchData();