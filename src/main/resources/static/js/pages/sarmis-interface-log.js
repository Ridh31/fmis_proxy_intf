const url = `${baseUrl}${apiPrefix}/list-sarmis-interface`;
let fullData = [];

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
 * Fetches paginated data based on filter inputs and updates the table display.
 */
async function fetchData() {
    showLoading(8);

    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().ajax.reload();
    } else {
        renderTable();
    }
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
            beforeSend: showLoading(8),
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
                showError(8);
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