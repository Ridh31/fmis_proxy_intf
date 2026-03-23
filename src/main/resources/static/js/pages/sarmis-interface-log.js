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
    showLoading(9);

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
            beforeSend: function() { showLoading(9); },
            complete: function() { hideLoading(); },
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
                        `<span class="view-link" data-index="${i}" data-type="payload" data-status="${item.status}">View</span>`,
                        `<span class="view-link" data-index="${i}" data-type="response" data-status="${item.status}">Preview</span>`,
                        `<span class="download-json" data-index="${i}">📄</span>`
                ]);
            },
            error: function(xhr, status, error) {
                showError(9);
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
            { title: "Response" },
            { title: "Export" }
        ]
    });
}

/**
 * Opens the modal and routes to the appropriate renderer based on data shape and type.
 *
 * Rendering priority:
 * 1. Server/proxy connection errors (IOError, ClientProxy, ServerProxy)
 * 2. API errors with null data (auth failure, not found, etc.)
 * 3. Payload — purchase orders sent to SARMIS
 * 4. Response — purchase order callback validation errors from SARMIS
 * 5. Response — successful batch PO confirmation from SARMIS
 * 6. Institution closing list
 * 7. Asset kind list
 * 8. Long term asset report
 * 9. Depreciation asset report
 * 10. Fallback — raw JSON display
 *
 * @param {Object|string} item - Parsed JSON data to display.
 * @param {string} type - Content type: 'payload' or 'response'.
 * @param {boolean|string} [status=true] - Request status; used to determine error styling.
 */
function openModal(item, type, status = true) {
    const modal = document.getElementById("modal");
    const body = document.getElementById("modalBody");
    const isFailed = (status === false || status === "false");

    modal.style.display = "flex";

    if (!item || (typeof item === "object" && Object.keys(item).length === 0)) {
        body.innerHTML = `
            <div style="padding:1rem; color:#9CA3AF; font-style:italic; font-size:13px;
                background:#FEFEFE; border:1px dashed #E5E7EB; border-radius:8px;">
                No data available.
            </div>`;
        return;
    }

    // Server/proxy connection errors
    if (item.type && (item.type.includes("IOError") || item.type.includes("Proxy"))) {
        renderErrorModal(item, body);
        return;
    }

    // Auth/validation errors with no data
    if (item.data === null && item.error && item.error !== "0") {
        renderApiErrorModal(item, body);
        return;
    }

    // Payload — purchase orders
    if (type === "payload" && item?.purchase_orders) {
        renderPayloadModal(item, body);
        return;
    }

    // Response — purchase order callback
    if (type === "response" && item?.purchase_orders) {
        renderCallbackModal(item, body, isFailed);
        return;
    }

    // Response — successful batch PO (SARMIS returns confirmed PO + receipt IDs)
    if (type === "response" && item?.data?.purchase_orders && (item?.error === "0" || item?.error === "000")) {
        renderPoSuccessModal(item, body);
        return;
    }

    // Institution closing list
    if (item?.data && Array.isArray(item.data) && item.data[0]?.institutions) {
        renderInstitutionClosingList(item, body);
        return;
    }

    // Asset kind list
    if (item?.data && Array.isArray(item.data) && item.data[0]?.asset_kind_code !== undefined) {
        renderAssetKindList(item, body);
        return;
    }

    // Long term asset report
    if (item?.data?.items && item.data?.closing_inventory) {
        renderLongTermAssetReport(item, body);
        return;
    }

    // Depreciation asset report
    if (item?.data?.items && item.data?.institution && !item.data?.closing_inventory) {
        renderDepreciationReport(item, body);
        return;
    }

    // Fallback
    renderRawModal(item, body, isFailed);
}

/**
 * Renders server/proxy connection error
 */
function renderErrorModal(item, body) {
    body.innerHTML = `
        <div style="padding:12px 16px; border-radius:8px; background:#FEF2F2; border:1px solid #FECACA; margin-bottom:12px;">
            <div style="font-size:11px; font-weight:700; color:#9CA3AF; text-transform:uppercase; letter-spacing:0.05em; margin-bottom:6px;">Connection Error</div>
            <div style="font-size:12px; font-weight:600; color:#DC2626; margin-bottom:4px;">${item.type || "Unknown Error"}</div>
            <div style="font-size:12px; color:#374151; margin-bottom:8px;">${item.message || ""}</div>
            ${item.detail ? `<div style="font-size:10px; color:#9CA3AF; font-family:monospace; background:#F9FAFB; padding:4px 8px; border-radius:4px;">Ref: ${item.detail}</div>` : ""}
        </div>`;
}

/**
 * Renders API error (auth, not found, etc.)
 */
function renderApiErrorModal(item, body) {
    const isNotFound = item.message?.includes("not.found");
    const isAuth = item.error === "401";
    const bg = isAuth ? "#FEF2F2" : "#FFFBEB";
    const border = isAuth ? "#FECACA" : "#FDE68A";
    const color = isAuth ? "#991B1B" : "#92400E";
    const label = isAuth ? "Authentication Error" : isNotFound ? "No Data Found" : "Error";

    body.innerHTML = `
        <div style="padding:12px 16px; border-radius:8px; background:${bg}; border:1px solid ${border};">
            <div style="font-size:11px; font-weight:700; color:#9CA3AF; text-transform:uppercase; letter-spacing:0.05em; margin-bottom:6px;">${label}</div>
            <div style="font-size:12px; color:${color};">${item.message || "Unknown error"}</div>
            ${item.error !== "0" ? `<div style="margin-top:4px; font-size:10px; font-family:monospace; color:#9CA3AF;">Error code: ${item.error}</div>` : ""}
        </div>`;
}

/**
 * Renders successful batch PO response as a clean summary table.
 */
function renderPoSuccessModal(item, body) {
    const interfaceCode = item.data?.interface_code || "N/A";
    const orders = item.data?.purchase_orders || [];

    let html = `
        <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
            <span style="font-size:11px; font-weight:700; color:#6B7280; text-transform:uppercase; letter-spacing:0.05em;">Interface Code</span>
            <span style="font-size:11px; background:#EEF2FF; color:#3730A3; padding:2px 8px; border-radius:4px; font-family:monospace;">${interfaceCode}</span>
            <span style="margin-left:auto; font-size:11px; background:#F0FDF4; color:#166534; padding:2px 8px; border-radius:20px;">✓ ${orders.length} PO${orders.length !== 1 ? 's' : ''} accepted</span>
        </div>
        <div style="border:1px solid #E5E7EB; border-radius:8px; overflow:hidden;">
            <div style="overflow-x:auto;">
                <table style="width:100%; border-collapse:collapse; font-size:11px;">
                    <thead>
                        <tr>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px;">#</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Purchase Order ID</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Receipt ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${orders.map((po, i) => `
                        <tr>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${i + 1}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:11px;">${po.purchase_order_id || "—"}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:11px;">${po.receipt_id || "—"}</td>
                        </tr>`).join("")}
                    </tbody>
                </table>
            </div>
        </div>`;

    body.innerHTML = html;
}

/**
 * Renders institution closing list grouped by type
 */
function renderInstitutionClosingList(item, body) {
    const groups = item.data || [];
    const totalInstitutions = groups.reduce((sum, g) => sum + (g.institutions?.length || 0), 0);

    let html = `
        <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
            <span style="font-size:11px; font-weight:700; color:#6B7280; text-transform:uppercase; letter-spacing:0.05em;">Institution Closing List</span>
            <span style="margin-left:auto; font-size:11px; background:#F3F4F6; color:#6B7280; padding:2px 8px; border-radius:20px;">${totalInstitutions} institutions</span>
        </div>`;

    groups.forEach((group, gi) => {
        const colors = ['#3B82F6', '#8B5CF6', '#0891B2'];
        const color = colors[gi % colors.length];
        const institutions = group.institutions || [];

        html += `
        <div style="border:1px solid #E5E7EB; border-radius:8px; overflow:hidden; margin-bottom:10px;">
            <div style="padding:8px 12px; background:#F0F2F5; border-bottom:1px solid #E5E7EB; display:flex; align-items:center; justify-content:space-between;">
                <span style="font-size:11px; font-weight:700; color:${color}; text-transform:uppercase; letter-spacing:0.04em;">${group.type_name || "N/A"}</span>
                <span style="font-size:11px; color:#6B7280;">${institutions.length} institutions</span>
            </div>
            <div style="overflow-x:auto;">
                <table style="width:100%; border-collapse:collapse; font-size:11px;">
                    <thead>
                        <tr>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Code</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px;">Institution Name</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Closing Years</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${institutions.map(inst => `
                        <tr>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:10px; white-space:nowrap;">${inst.institution_code || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${inst.institution_name_kh || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; font-size:11px;">
                                ${inst.closing_years && inst.closing_years.length > 0
            ? inst.closing_years.map(y => `<span style="display:inline-block; background:#EEF2FF; color:#3730A3; padding:1px 5px; border-radius:3px; font-size:10px; margin:1px;">${y}</span>`).join("")
            : `<span style="color:#D1D5DB; font-style:italic;">—</span>`
        }
                            </td>
                        </tr>`).join("")}
                    </tbody>
                </table>
            </div>
        </div>`;
    });

    body.innerHTML = html;
}

/**
 * Renders asset kind list as a clean table
 */
function renderAssetKindList(item, body) {
    const assets = item.data || [];

    let html = `
        <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
            <span style="font-size:11px; font-weight:700; color:#6B7280; text-transform:uppercase; letter-spacing:0.05em;">Asset Kind List</span>
            <span style="margin-left:auto; font-size:11px; background:#F3F4F6; color:#6B7280; padding:2px 8px; border-radius:20px;">${assets.length} items</span>
        </div>
        <div style="border:1px solid #E5E7EB; border-radius:8px; overflow:hidden;">
            <div style="overflow-x:auto;">
                <table style="width:100%; border-collapse:collapse; font-size:11px;">
                    <thead>
                        <tr>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px;">#</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px;">Asset Name</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Asset Code</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Financial Code</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${assets.map((a, i) => `
                        <tr>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${i + 1}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${a.asset_kind_name || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:10px; white-space:nowrap;">${a.asset_kind_code || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:10px; white-space:nowrap;">${a.financial_code || ""}</td>
                        </tr>`).join("")}
                    </tbody>
                </table>
            </div>
        </div>`;

    body.innerHTML = html;
}

/**
 * Renders long term asset report
 */
function renderLongTermAssetReport(item, body) {
    const items = item.data?.items || [];
    const institution = item.data?.institution || {};
    const year = item.data?.closing_inventory?.year || "N/A";

    let html = `
        <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
            <div>
                <div style="font-size:12px; font-weight:600; color:#111827;">${institution.institution_name_kh || "N/A"}</div>
                <div style="font-size:11px; color:#6B7280;">${institution.institution_code || ""} ${institution.institution_address ? "· " + institution.institution_address : ""}</div>
            </div>
            <span style="margin-left:auto; font-size:11px; background:#EEF2FF; color:#3730A3; padding:2px 8px; border-radius:20px; white-space:nowrap;">Year: ${year}</span>
        </div>
        <div style="border:1px solid #E5E7EB; border-radius:8px; overflow:hidden;">
            <div style="overflow-x:auto;">
                <table style="width:100%; border-collapse:collapse; font-size:11px;">
                    <thead>
                        <tr>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Asset Code</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Financial Code</th>
                            <th style="padding:5px 10px; text-align:right; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Qty</th>
                            <th style="padding:5px 10px; text-align:right; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Initial Price</th>
                            <th style="padding:5px 10px; text-align:right; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Book Value</th>
                            <th style="padding:5px 10px; text-align:right; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Accumulated Dep.</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Registered</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">In Use Since</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${items.map(it => `
                        <tr>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:10px; white-space:nowrap;">${it.asset_code || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:10px; white-space:nowrap;">${it.financial_code || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; text-align:right; font-size:11px;">${it.quantity ?? ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; text-align:right; white-space:nowrap; font-size:11px;">${Number(it.initial_price || 0).toLocaleString()}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; text-align:right; white-space:nowrap; font-size:11px;">${Number(it.book_value || 0).toLocaleString()}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; text-align:right; white-space:nowrap; font-size:11px;">${Number(it.accumulated_depreciation || 0).toLocaleString()}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; white-space:nowrap; font-size:11px;">${it.registered_date || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; white-space:nowrap; font-size:11px;">${it.using_date || ""}</td>
                        </tr>`).join("")}
                    </tbody>
                </table>
            </div>
        </div>`;

    body.innerHTML = html;
}

/**
 * Renders depreciation asset report grouped by chapter_code
 */
function renderDepreciationReport(item, body) {
    const items = item.data?.items || [];
    const institution = item.data?.institution || {};

    // Group by chapter_code
    const grouped = {};
    items.forEach(it => {
        const ch = it.chapter_code || "Other";
        if (!grouped[ch]) grouped[ch] = [];
        grouped[ch].push(it);
    });

    const chapterNames = { "20": "Intangible Assets", "21": "Tangible Assets" };

    let html = `
        <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
            <div>
                <div style="font-size:12px; font-weight:600; color:#111827;">${institution.institution_name_kh || "N/A"}</div>
                <div style="font-size:11px; color:#6B7280;">${institution.institution_code || ""} ${institution.institution_address ? "· " + institution.institution_address : ""}</div>
            </div>
        </div>`;

    const chapterColors = { "20": "#8B5CF6", "21": "#3B82F6" };

    Object.entries(grouped).forEach(([chapterCode, chItems]) => {
        const color = chapterColors[chapterCode] || "#6B7280";
        const label = chapterNames[chapterCode] || `Chapter ${chapterCode}`;
        const subtotal = chItems.reduce((sum, it) => sum + (it.accumulated_depreciation_price || 0), 0);

        html += `
        <div style="border:1px solid #E5E7EB; border-radius:8px; overflow:hidden; margin-bottom:10px;">
            <div style="padding:8px 12px; background:#F0F2F5; border-bottom:1px solid #E5E7EB; display:flex; align-items:center; justify-content:space-between;">
                <span style="font-size:11px; font-weight:700; color:${color}; text-transform:uppercase; letter-spacing:0.04em;">Chapter ${chapterCode} — ${label}</span>
                <span style="font-size:11px; color:#6B7280;">Subtotal: <strong style="color:#111827;">${subtotal.toLocaleString()}</strong></span>
            </div>
            <div style="overflow-x:auto;">
                <table style="width:100%; border-collapse:collapse; font-size:11px;">
                    <thead>
                        <tr>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px;">Account Name</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Financial Code</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Sub Code</th>
                            <th style="padding:5px 10px; text-align:right; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px; white-space:nowrap;">Accumulated Dep.</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; font-size:11px;">Currency</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${chItems.map(it => `
                        <tr>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${it.account_chart_name || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:10px;">${it.financial_code || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-family:monospace; font-size:10px;">${it.sub_financial_code || ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; text-align:right; white-space:nowrap; font-size:11px;">${Number(it.accumulated_depreciation_price || 0).toLocaleString()}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#9CA3AF; font-style:italic; font-size:11px;">${it.currency || "—"}</td>
                        </tr>`).join("")}
                        <tr style="background:#F9FAFB;">
                            <td colspan="3" style="padding:5px 10px; font-size:11px; font-weight:600; color:#374151; border-top:1px solid #E5E7EB;">Subtotal</td>
                            <td style="padding:5px 10px; font-size:11px; font-weight:600; color:#111827; text-align:right; border-top:1px solid #E5E7EB; white-space:nowrap;">${subtotal.toLocaleString()}</td>
                            <td style="border-top:1px solid #E5E7EB;"></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>`;
    });

    body.innerHTML = html;
}

/**
 * Renders purchase order payload in a structured table per PO.
 */
function renderPayloadModal(item, body) {
    const interfaceCode = item.interface_code || "N/A";
    const orders = item.purchase_orders || [];

    let html = `
        <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
            <span style="font-size:11px; font-weight:700; color:#6B7280; text-transform:uppercase; letter-spacing:0.05em;">Interface Code</span>
            <span style="font-size:11px; background:#EEF2FF; color:#3730A3; padding:2px 8px; border-radius:4px; font-family:monospace;">${interfaceCode}</span>
            <span style="margin-left:auto; font-size:11px; background:#F3F4F6; color:#6B7280; padding:2px 8px; border-radius:20px;">${orders.length} purchase order${orders.length !== 1 ? 's' : ''}</span>
        </div>`;

    orders.forEach((po, idx) => {
        const poId = po.purchase_order_id || "N/A";
        const receiptId = po.receipt_id || "N/A";
        const receiptDate = po.receipt_date || "N/A";
        const businessUnit = po.business_unit || "N/A";
        const items = po.items || [];
        const currency = items[0]?.currency || "N/A";

        html += `
        <div style="border:1px solid #E5E7EB; border-radius:8px; overflow:hidden; margin-bottom:10px;">
            <div style="padding:7px 12px; background:#F0F2F5; border-bottom:1px solid #E5E7EB; display:flex; align-items:center; gap:8px;">
                <span style="font-size:11px; font-weight:700; color:#3B82F6; text-transform:uppercase; letter-spacing:0.04em;">PO-${poId}</span>
                <span style="font-size:11px; color:#6B7280; margin-left:auto;">Receipt: ${receiptDate} &nbsp;|&nbsp; Receipt ID: ${receiptId}</span>
            </div>
            <div style="display:grid; grid-template-columns:1fr 1fr 1fr; border-bottom:1px solid #F3F4F6;">
                <div style="padding:5px 12px; font-size:11px; border-right:1px solid #F3F4F6;">
                    <span style="color:#6B7280; display:block;">Business Unit</span>
                    <span style="color:#111827; font-weight:600;">${businessUnit}</span>
                </div>
                <div style="padding:5px 12px; font-size:11px; border-right:1px solid #F3F4F6;">
                    <span style="color:#6B7280; display:block;">Items</span>
                    <span style="color:#111827; font-weight:600;">${items.length} item${items.length !== 1 ? 's' : ''}</span>
                </div>
                <div style="padding:5px 12px; font-size:11px;">
                    <span style="color:#6B7280; display:block;">Currency</span>
                    <span style="color:#111827; font-weight:600;">${currency}</span>
                </div>
            </div>
            <div style="overflow-x:auto;">
                <table style="width:100%; border-collapse:collapse; font-size:11px;">
                    <thead>
                        <tr>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">#</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Item Code</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Description</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Qty</th>
                            <th style="padding:5px 10px; text-align:right; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Amount (KHR)</th>
                            <th style="padding:5px 10px; text-align:left; font-weight:600; color:#6B7280; background:#F9FAFB; border-bottom:1px solid #F3F4F6; white-space:nowrap; font-size:11px;">Vendor</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${items.map((it, i) => `
                        <tr>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${i + 1}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; white-space:nowrap; font-size:11px;">${it.item_code ?? ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${it.item_description ?? ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${it.quantity_received ?? ""}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; text-align:right; white-space:nowrap; font-size:11px;">${Number(it.khr_amount || 0).toLocaleString()}</td>
                            <td style="padding:5px 10px; border-bottom:1px solid #F9FAFB; color:#374151; font-size:11px;">${it.vendor_name ?? ""}</td>
                        </tr>`).join("")}
                    </tbody>
                </table>
            </div>
        </div>`;
    });

    body.innerHTML = html;
}

/**
 * Renders callback validation errors per PO and line item.
 */
function renderCallbackModal(item, body, isFailed) {
    const interfaceCode = item.interface_code || "N/A";
    const orders = item.purchase_orders || [];
    const rootErrors = item.validation_errors;

    const posWithErrors = orders.filter(po =>
        (po.validation_errors && po.validation_errors.length > 0) ||
        (po.items && po.items.some(it => it.validation_errors && it.validation_errors.length > 0))
    );

    let html = `
        <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
            <span style="font-size:11px; font-weight:700; color:#6B7280; text-transform:uppercase; letter-spacing:0.05em;">Interface Code</span>
            <span style="font-size:11px; background:#EEF2FF; color:#3730A3; padding:2px 8px; border-radius:4px; font-family:monospace;">${interfaceCode}</span>
            ${posWithErrors.length > 0
        ? `<span style="margin-left:auto; font-size:11px; background:#FEF2F2; color:#991B1B; padding:2px 8px; border-radius:20px;">${posWithErrors.length} PO${posWithErrors.length !== 1 ? 's' : ''} with errors</span>`
        : `<span style="margin-left:auto; font-size:11px; background:#F0FDF4; color:#166534; padding:2px 8px; border-radius:20px;">No errors</span>`
    }
        </div>`;

    // Root level errors
    if (rootErrors && Array.isArray(rootErrors) && rootErrors.length > 0) {
        html += `<div style="padding:10px 14px; border-radius:8px; font-size:12px; line-height:1.5; margin-bottom:10px;
            background:#FEF2F2; border:1px solid #FECACA; color:#991B1B;">
            <strong>Interface-Level Errors:</strong>
            ${rootErrors.map(e => `<div style="margin-top:4px;">• ${e.message || "Unknown"} <span style="font-family:monospace; font-size:10px; background:#FEE2E2; padding:1px 5px; border-radius:3px;">${e.error || ""}</span></div>`).join("")}
        </div>`;
    }

    orders.forEach(po => {
        const poId = po.purchase_order_id || "N/A";
        const poErrors = po.validation_errors || [];
        const items = po.items || [];
        const hasErrors = poErrors.length > 0 || items.some(it => it.validation_errors?.length > 0);

        html += `
        <div style="border:1px solid ${hasErrors ? '#FECACA' : '#E5E7EB'}; border-radius:8px; overflow:hidden; margin-bottom:10px;">
            <div style="padding:7px 12px; background:${hasErrors ? '#FEF2F2' : '#F0F2F5'}; border-bottom:1px solid ${hasErrors ? '#FECACA' : '#E5E7EB'};">
                <span style="font-size:11px; font-weight:700; color:${hasErrors ? '#DC2626' : '#3B82F6'}; text-transform:uppercase; letter-spacing:0.04em;">PO-${poId}</span>
            </div>`;

        // PO level errors
        if (poErrors.length > 0) {
            html += `<div style="padding:8px 12px; border-bottom:1px solid #FEF2F2;">
                <div style="font-size:10px; font-weight:700; color:#9CA3AF; text-transform:uppercase; letter-spacing:0.05em; margin-bottom:5px;">PO Errors</div>
                ${poErrors.map(e => `
                    <div style="display:flex; align-items:center; gap:8px; padding:3px 0; font-size:11px;">
                        <span style="color:#374151;">${e.message || "Unknown"}</span>
                        <span style="color:#DC2626; background:#FEF2F2; padding:1px 6px; border-radius:4px; font-family:monospace; font-size:10px; border:1px solid #FECACA;">${e.error || ""}</span>
                    </div>`).join("")}
            </div>`;
        }

        // Item level errors
        const itemsWithErrors = items.filter(it => it.validation_errors?.length > 0);
        if (itemsWithErrors.length > 0) {
            const sorted = [...itemsWithErrors].sort((a, b) => (a.index ?? 0) - (b.index ?? 0));
            html += `<div style="padding:8px 12px;">
                <div style="font-size:10px; font-weight:700; color:#9CA3AF; text-transform:uppercase; letter-spacing:0.05em; margin-bottom:6px;">Line Item Errors</div>
                ${sorted.map(it => {
                const displayIndex = it.index !== undefined ? it.index + 1 : "?";
                // Deduplicate errors
                const errorMap = {};
                (it.validation_errors || []).forEach(e => {
                    const key = `${e.message}|${e.error}`;
                    errorMap[key] = (errorMap[key] || 0) + 1;
                });
                return `
                    <div style="margin-bottom:6px; padding:6px 10px; background:#FFFBEB; border:1px solid #FDE68A; border-radius:6px;">
                        <div style="font-size:10px; font-weight:700; color:#92400E; margin-bottom:3px;">Item ${displayIndex}</div>
                        ${Object.entries(errorMap).map(([key, count]) => {
                    const [msg, code] = key.split("|");
                    return `<div style="display:flex; align-items:center; gap:8px; padding:2px 0; font-size:11px;">
                                <span style="color:#374151;">${msg}${count > 1 ? ` <span style="color:#92400E;">×${count}</span>` : ""}</span>
                                <span style="color:#DC2626; background:#FEF2F2; padding:1px 6px; border-radius:4px; font-family:monospace; font-size:10px; border:1px solid #FECACA;">${code}</span>
                            </div>`;
                }).join("")}
                    </div>`;
            }).join("")}
            </div>`;
        }

        if (!hasErrors) {
            html += `<div style="padding:8px 12px; font-size:11px; color:#9CA3AF; font-style:italic;">No errors</div>`;
        }

        html += `</div>`;
    });

    body.innerHTML = html;
}

/**
 * Fallback — renders raw JSON with existing highlight logic.
 */
function renderRawModal(item, body, isFailed) {
    try {
        let jsonStr = typeof item === "string" ? item : JSON.stringify(item, null, 2);

        const highlightRegex = /("(purchase_order_id|receipt_id|interface_code)"\s*:\s*)"([^"]+)"/g;
        jsonStr = jsonStr.replace(highlightRegex, (_, keyPrefix, keyName, value) => {
            let color = "", textColor = "";
            switch (keyName) {
                case "purchase_order_id": color = "yellow"; textColor = "#000"; break;
                case "receipt_id": color = "#CCE5FF"; textColor = "#003366"; break;
                case "interface_code": color = "#D3F9D8"; textColor = "#2C6E49"; break;
            }
            return `${keyPrefix}"<span style="background-color:${color}; font-weight:bold; color:${textColor};">${value}</span>"`;
        });

        body.innerHTML = `
            <pre style="white-space:pre-wrap; font-size:0.85rem; font-family:'JetBrains Mono',monospace;
                color:#333; background-color:${isFailed ? '#FFF5F5' : '#F9F9F9'};
                border:1px solid ${isFailed ? '#F5C6CB' : '#E4E4E4'};
                padding:1rem; border-radius:8px; max-height:60vh; overflow-y:auto;">${jsonStr}</pre>`;
    } catch (e) {
        body.innerHTML = `<pre style="color:red;">Failed to render content.</pre>`;
    }
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

    // Use endpoint to set a better title
    const endpointMap = {
        "fmis-purchase-orders": "Batch Purchase Orders",
        "fmis-purchase-orders-callback": "Purchase Orders Callback",
        "long-term-asset-report": "Long Term Asset Report",
        "depreciation-asset-report": "Depreciation Asset Report",
        "institution-closing-list": "Institution Closing List",
        "asset-kind-list": "Asset Kind List"
    };

    const endpointKey = Object.keys(endpointMap).find(k => item.endpoint?.includes(k));
    const friendlyName = endpointKey ? endpointMap[endpointKey] : type;
    document.getElementById("modalTitle").textContent = `${friendlyName} — ${type.charAt(0).toUpperCase() + type.slice(1)}`;

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

logTable.on("click", ".download-json", function () {
    const index = $(this).data("index");
    const item = fullData[index];
    if (!item) return;

    const date = item.createdDate?.slice(0, 10) || "export";
    const code = item.interfaceCode || "sarmis";

    if (item.payload) {
        try {
            const data = JSON.parse(item.payload);
            downloadJson(data, `${code}-${date}-payload.json`);
        } catch {
            downloadJson(item.payload, `${code}-${date}-payload.json`);
        }
    }

    if (item.response) {
        try {
            const data = JSON.parse(item.response);
            downloadJson(data, `${code}-${date}-response.json`);
        } catch {
            downloadJson(item.response, `${code}-${date}-response.json`);
        }
    }
});

/**
 * Triggers a JSON file download with the given data and filename.
 *
 * @param {Object|string} data - The data to serialize and download.
 * @param {string} filename - The name of the downloaded file.
 */
function downloadJson(data, filename) {
    const blob = new Blob([JSON.stringify(data, null, 4)], { type: "application/json" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    link.click();
    URL.revokeObjectURL(link.href);
}

// Initial data fetch on page load
fetchData();