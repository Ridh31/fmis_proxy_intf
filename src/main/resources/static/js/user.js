// Retrieve credentials and tokens from DOM dataset attributes or default to empty string
const username = document.querySelector("[data-username]")?.dataset.username || "";
const password = document.querySelector("[data-password]")?.dataset.password || "";
const partnerToken = document.querySelector("[data-partner-token]")?.dataset.partnerToken || "";
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;

// Encode Basic Auth credentials in base64
const basicAuth = btoa(`${username}:${password}`);

// Base URL for API calls
const baseUrl = window.location.origin;
const url = `${baseUrl}${apiPrefix}/auth/list-user`;

// Elements references
const filterBtn = document.querySelector("#filter-button");
let fullData = [];
let logTable = $("#logTable");

/**
 * Display a loading indicator in the table while fetching data.
 * Clears existing table body and shows a single row with "Loading..." message.
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
 * Remove the "Loading..." row from the table body after data has loaded.
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
 * Fetch or reload Partner Management DataTable
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
 * Render Partner Management table with server-side processing
 */
function renderTable() {
    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().destroy();
    }

    const table = logTable.DataTable({
        serverSide: true,
        processing: true,
        pageLength: 10,
        scrollX: true,
        scrollCollapse: true,
        lengthMenu: [10, 25, 50, 100, 200],
        fixedHeader: true,
        ajax: {
            url: url,
            type: "GET",
            data: function (d) {
                const page = Math.floor(d.start / d.length);
                return {
                    username: document.getElementById("username")?.value || undefined,
                    page: page,
                    size: d.length
                };
            },
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            },
            beforeSend: showLoading,
            complete: hideLoading,
            dataSrc: function (json) {
                json.recordsTotal = json?.data?.totalElements || 0;
                json.recordsFiltered = json?.data?.totalElements || 0;
                return json?.data?.content || [];
            },
            error: function () {
                showToast("error", "Error fetching data.")
            }
        },
        columns: [
            { data: null, title: "#", render: (data, type, row, meta) => meta.row + 1 },
            {
                data: null,
                title: "Action",
                render: () => `
                    <span class="edit-link">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="var(--color-blue-dark)" class="size-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M18.364 18.364A9 9 0 0 0 5.636 5.636m12.728 12.728A9 9 0 0 1 5.636 5.636m12.728 12.728L5.636 5.636" />
                        </svg>
                    </span>`
            },
            { data: "username", title: "Username" },
            { data: "role.name", title: "Role" },
            { data: "partner.name", title: "Branch" },
            { data: "email", title: "Email", render: (data) => data || `<span class="text-muted">N/A</span>` }
        ]
    });
}

// DOM Event Listeners
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("openModalBtn")?.addEventListener("click", () => {
        showToast("info", "Under development.");
    });
});

// Filter button triggers data fetch with current filter inputs
filterBtn.addEventListener("click", () => {
    fetchData();
});

// Initial data fetch on page load
fetchData();