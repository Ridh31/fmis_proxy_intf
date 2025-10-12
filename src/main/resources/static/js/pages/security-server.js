const url = `${baseUrl}${apiPrefix}/security-server/list-server`;

// Initialize jQuery UI draggable for modal and flatpickr datepicker
$(() => {
    flatpickr("#createdDate", {
        dateFormat: "d/m/Y",
        allowInput: true
    });
});

/**
 * Load or refresh the Security Server DataTable.
 * Uses server-side processing for faster rendering on large datasets.
 */
async function fetchData() {
    showLoading(11);

    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().ajax.reload();
        return;
    }

    renderTable();
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
            beforeSend: showLoading(11),
            complete: hideLoading,
            dataSrc: function (json) {
                json.recordsTotal = json?.data?.totalElements || 0;
                json.recordsFiltered = json?.data?.totalElements || 0;
                return json?.data?.content || [];
            },
            error: function () {
                showError(11);
                showToast("error", "Error fetching data.");
            }
        },
        columns: [
            { data: null, title: "#", render: (data, type, row, meta) => meta.row + 1 },
            {
                data: null,
                title: "Action",
                render: (data, type, row, meta) => `
                    <span class="edit-link">
                         <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="var(--color-blue-dark)" class="size-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"/>
                         </svg>
                    </span>`
            },
            { data: "name", title: "Name" },
            { data: "configKey", title: "Config Key" },
            { data: "baseURL", title: "Base URL" },
            { data: "endpoint", title: "Endpoint" },
            { data: "subsystem", title: "Subsystem" },
            { data: "username", title: "Username" },
            { data: "password", title: "Password" },
            { data: "contentType", title: "Content Type" },
            { data: "description", title: "Description" }
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

// Open the Create Modal
document.getElementById("openModalBtn").addEventListener("click", () => {
    document.getElementById("createModal").style.display = "flex";
});

/**
 * Closes the create server modal, clears any validation errors,
 * and resets all form fields to their default values.
 */
function closeCreateModal() {
    document.getElementById("createModal").style.display = "none";
    clearCreateErrors();
    document.getElementById("createServerForm").reset();
}

// Close buttons
document.getElementById("createModalClose").addEventListener("click", closeCreateModal);
document.getElementById("cancelCreateModal").addEventListener("click", closeCreateModal);

/**
 * Clears all validation error styles and messages from inputs
 * inside the modal forms (#modal and #createModal).
 */
function clearCreateErrors() {
    const inputs = document.querySelectorAll("#createModal input, #createModal textarea");
    inputs.forEach(input => {
        input.classList.remove("error");
        const nextElem = input.nextElementSibling;
        if (nextElem && nextElem.classList.contains("error-message")) {
            nextElem.remove();
        }
    });
}

/**
 * Handles create server form submission:
 * - Prevents default submit
 * - Clears previous errors
 * - Sends form data to backend
 * - Handles validation and backend errors
 */
document.getElementById("createServerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const formData = {
        name: document.getElementById("createName").value.trim(),
        configKey: document.getElementById("createConfigKey").value.trim(),
        baseURL: document.getElementById("createBaseURL").value.trim(),
        endpoint: document.getElementById("createEndpoint").value.trim(),
        subsystem: document.getElementById("createSubsystem").value.trim(),
        username: document.getElementById("createUsername").value.trim(),
        password: document.getElementById("createPassword").value.trim(),
        contentType: document.getElementById("createContentType").value.trim(),
        description: document.getElementById("createDescription").value.trim()
    };

    clearCreateErrors();

    try {
        const res = await fetch(`${apiPrefix}/security-server/create-server`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${basicAuth}`
            },
            body: JSON.stringify(formData)
        });

        const resData = await res.json().catch(() => null);

        if (!res.ok) {
            if (resData?.error) {
                Object.entries(resData.error).forEach(([field, msg]) => {
                    const input = document.getElementById(`create${capitalize(field)}`);
                    if (input) showErrorField(input, msg);
                });
            } else if (resData?.message) {
                const msg = resData.message;
                // Try to detect the field from the message
                if (msg.toLowerCase().includes("config_key") || msg.toLowerCase().includes("already taken")) {
                    const input = document.getElementById("createConfigKey");
                    if (input) showErrorField(input, msg);
                } else if (msg.toLowerCase().includes("name")) {
                    const input = document.getElementById("createName");
                    if (input) showErrorField(input, msg);
                } else {
                    // Fallback: show toast
                    showToast("error", msg);
                }
            }
            else {
                showToast("error", "An unknown error occurred.");
            }
            return;
        }

        showToast("success", "Created successfully.");
        closeCreateModal();
        fetchData();

    } catch (err) {
        console.error("Error creating server:", err);
        showToast("error", "Something went wrong while creating server.");
    }
});

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
        showToast("error", "Missing ID for update.");
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
                        showToast("error", errData.message);
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
                showToast("error", "An error occurred.");
            }

            return;
        }

        showToast("success", "Update successful.");
        closeModal();
        fetchData();

    } catch (err) {
        console.error("Update failed:", err);
        showToast("error", "Failed to update server. Please try again.");
    }
});

// Filter button triggers data fetch with current filter inputs
filterBtn.addEventListener("click", () => {
    fetchData();
});

// Initial data fetch on page load
fetchData();