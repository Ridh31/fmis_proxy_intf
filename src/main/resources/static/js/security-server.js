// Retrieve credentials and tokens from DOM dataset attributes or default to empty string
const username = document.querySelector("[data-username]")?.dataset.username || "";
const password = document.querySelector("[data-password]")?.dataset.password || "";
const partnerToken = document.querySelector("[data-partner-token]")?.dataset.partnerToken || "";
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;

// Encode Basic Auth credentials in base64
const basicAuth = btoa(`${username}:${password}`);

// Base URL for API calls
const baseUrl = window.location.origin;
const url = `${baseUrl}${apiPrefix}/security-server/list-server`;

// Elements references
const filterBtn = document.querySelector("#filter-button");
let fullData = [];
let logTable = $("#logTable")

// Initialize jQuery UI draggable for modal and flatpickr datepicker
$(() => {
    flatpickr("#createdDate", {
        dateFormat: "d/m/Y",
        allowInput: true
    });
});

/**
 * Display a loading indicator in the table while fetching data.
 * Clears existing table body and shows a single row with "Loading..." message.
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
 * Show error when fetching data
 *
 * @param message - The data to display.
 */
function showError(message) {
    const tbody = document.querySelector("#logTable tbody");
    tbody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 11;
    cell.style.textAlign = "center";
    cell.style.color = "red";
    cell.style.fontWeight = "bold";
    cell.style.padding = "0.75rem";
    cell.innerText = message || "Error fetching data.";
    row.appendChild(cell);
    tbody.appendChild(row);
}

/**
 * Load or refresh the Security Server DataTable.
 * Uses server-side processing for faster rendering on large datasets.
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
 * Convert date from dd/mm/yyyy → dd-mm-yyyy
 */
function formatDate(input) {
    const [day, month, year] = input.split('/');
    return `${day}-${month}-${year}`;
}

/**
 * Initialize Security Server DataTable with server-side processing
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
                    page: page,
                    size: d.length,
                    name: document.getElementById("name").value || undefined,
                    configKey: document.getElementById("configKey").value || undefined,
                    description: document.getElementById("description").value || undefined,
                    createdDate: document.getElementById("createdDate").value
                        ? formatDate(document.getElementById("createdDate").value)
                        : undefined
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
                showError("Error fetching data");
            }
        },
        columns: [
            { data: null, title: "#", render: (data, type, row, meta) => meta.row + 1 },
            { data: "name", title: "Name" },
            { data: "configKey", title: "Config Key" },
            { data: "baseURL", title: "Base URL" },
            { data: "endpoint", title: "Endpoint" },
            { data: "subsystem", title: "Subsystem" },
            { data: "username", title: "Username" },
            { data: "password", title: "Password" },
            { data: "contentType", title: "Content Type" },
            { data: "description", title: "Description" },
            {
                data: null,
                title: "Action",
                render: (data, type, row, meta) => `
                    <span class="edit-link">
                         <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="#1A73E8" class="size-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"/>
                         </svg>
                    </span>`
            }
        ]
    });

    // Attach edit handler
    logTable.off("click", ".edit-link").on("click", ".edit-link", function () {
        const rowData = table.row($(this).closest("tr")).data();
        if (!rowData) {
            console.error("No row data found for edit");
            return;
        }
        openEditModal(rowData);
    });
}

// Variable to track the currently edited item ID
let currentEditId = null;

/**
 * Opens the edit modal and fills it with data of the selected item.
 * @param {object} item - Data item to edit
 */
function openEditModal(item) {
    currentEditId = item.id;

    document.getElementById("modalName").value = item.name || null;
    document.getElementById("modalConfigKey").value = item.configKey || null;
    document.getElementById("modalBaseURL").value = item.baseURL || null;
    document.getElementById("modalEndpoint").value = item.endpoint || null;
    document.getElementById("modalSubsystem").value = item.subsystem || null;
    document.getElementById("modalUsername").value = item.username || null;
    document.getElementById("modalPassword").value = item.password || null;
    document.getElementById("modalContentType").value = item.contentType || null;
    document.getElementById("modalDescription").value = item.description || null;

    document.getElementById("modal").style.display = "flex";  // Show modal
}

/**
 * Closes the edit modal and resets the current edit ID.
 */
function closeModal() {
    document.getElementById("modal").style.display = "none";
    currentEditId = null;
    clearErrors();
}

// Attach event listeners to close buttons of modal
document.getElementById("modalClose").addEventListener("click", closeModal);
document.getElementById("cancelModal").addEventListener("click", closeModal);

/**
 * Clears all error messages and removes error styles from modal input fields.
 */
function clearErrors() {
    const inputs = document.querySelectorAll("#modal input");
    inputs.forEach(input => {
        input.classList.remove("error");
        const nextElem = input.nextElementSibling;
        if (nextElem && nextElem.classList.contains("error-message")) {
            nextElem.remove();
        }
    });
}

/**
 * Displays an error message below the input and applies error styling.
 * Creates the error element if it doesn’t exist yet.
 * @param {HTMLElement} input - The input element to show the error for.
 * @param {string} message - The error message to display.
 */
function showValidationError(input, message) {
    input.classList.add("error");
    let errorElem = input.nextElementSibling;
    if (!errorElem || !errorElem.classList.contains("error-message")) {
        errorElem = document.createElement("div");
        errorElem.className = "error-message";
        input.parentNode.insertBefore(errorElem, input.nextSibling);
    }
    errorElem.textContent = message;
}

/**
 * Validates required fields in the modal form.
 * Clears previous errors, then validates 'name' and 'configKey'.
 * Shows errors if fields are empty.
 * @param {Object} data - Form data to validate.
 * @returns {boolean} - True if valid, false otherwise.
 */
function validateModalFields(data) {
    let valid = true;
    clearErrors();

    if (!data.name.trim()) {
        const input = document.getElementById("modalName");
        showValidationError(input, "Name is required. Please provide a valid name.");
        valid = false;
    }

    if (!data.configKey.trim()) {
        const input = document.getElementById("modalConfigKey");
        showValidationError(input, "Config key is required. Please provide a valid config key.");
        valid = false;
    }

    return valid;
}

/**
 * Capitalizes the first letter of a string.
 * @param {string} str - The string to capitalize.
 * @returns {string} - Capitalized string.
 */
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * Handles the server edit form submission.
 * Prevents default submit, validates input, sends update request,
 * handles API errors, and refreshes data on success.
 */
document.getElementById("editServerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const updatedData = {
        name: document.getElementById("modalName").value.trim(),
        configKey: document.getElementById("modalConfigKey").value.trim(),
        baseURL: document.getElementById("modalBaseURL").value.trim(),
        endpoint: document.getElementById("modalEndpoint").value.trim(),
        subsystem: document.getElementById("modalSubsystem").value.trim(),
        username: document.getElementById("modalUsername").value.trim(),
        password: document.getElementById("modalPassword").value.trim(),
        contentType: document.getElementById("modalContentType").value.trim(),
        description: document.getElementById("modalDescription").value.trim()
    };

    clearErrors();

    if (!currentEditId) {
        alert("Missing ID for update.");
        return;
    }

    if (!validateModalFields(updatedData)) return;

    try {
        const res = await fetch(`${apiPrefix}/security-server/update-server/${currentEditId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${basicAuth}`
            },
            body: JSON.stringify(updatedData)
        });

        if (!res.ok) {
            const errData = await res.json().catch(() => null);

            if (errData) {
                // Handle string-based error message
                if (typeof errData.message === "string") {
                    const message = errData.message.toLowerCase();

                    if (message.includes("name")) {
                        const input = document.getElementById("modalName");
                        showValidationError(input, errData.message);
                    } else if (message.includes("config key")) {
                        const input = document.getElementById("modalConfigKey");
                        showValidationError(input, errData.message);
                    } else {
                        alert(errData.message);
                    }
                }

                // Handle field-based validation errors
                if (typeof errData.error === "object") {
                    Object.entries(errData.error).forEach(([field, msg]) => {
                        const input = document.getElementById(`modal${capitalize(field)}`);
                        if (input) {
                            showValidationError(input, msg);
                        }
                    });
                }
            } else {
                alert("An unknown error occurred.");
            }

            return;
        }

        closeModal();
        fetchData();

    } catch (err) {
        console.error("Update failed:", err);
        alert("Failed to update server. Please try again.");
    }
});

// Filter button triggers data fetch with current filter inputs
filterBtn.addEventListener("click", () => {
    fetchData();
});

// Initial data fetch on page load
fetchData();