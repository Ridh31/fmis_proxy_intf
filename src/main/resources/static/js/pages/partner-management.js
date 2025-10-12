const url = `${baseUrl}${apiPrefix}/list-partner`;

/**
 * Fetch or reload Partner Management DataTable
 */
async function fetchData() {
    showLoading(8);

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
                    page: page,
                    size: d.length,
                    name: document.getElementById("name").value || undefined,
                    identifier: document.getElementById("identifier").value || undefined,
                    systemCode: document.getElementById("systemCode").value || undefined,
                    description: document.getElementById("description").value || undefined
                };
            },
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            },
            beforeSend: showLoading(8),
            complete: hideLoading,
            dataSrc: function (json) {
                json.recordsTotal = json?.data?.totalElements || 0;
                json.recordsFiltered = json?.data?.totalElements || 0;
                return json?.data?.content || [];
            },
            error: function () {
                showError(8)
                showToast("error", "Error fetching data.");
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
                            <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"/>
                        </svg>
                    </span>`
            },
            {
                data: "public_key",
                title: "Key",
                render: (data) => `
                    <span class="copy-key-btn" data-key="${data}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="var(--color-blue-dark)" class="size-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M15.666 3.888A2.25 2.25 0 0 0 13.5 2.25h-3c-1.03 0-1.9.693-2.166 1.638m7.332 0c.055.194.084.4.084.612v0a.75.75 0 0 1-.75.75H9a.75.75 0 0 1-.75-.75v0c0-.212.03-.418.084-.612m7.332 0c.646.049 1.288.11 1.927.184 1.1.128 1.907 1.077 1.907 2.185V19.5a2.25 2.25 0 0 1-2.25 2.25H6.75A2.25 2.25 0 0 1 4.5 19.5V6.257c0-1.108.806-2.057 1.907-2.185a48.208 48.208 0 0 1 1.927-.184" />
                        </svg>
                    </span>`
            },
            { data: "name", title: "Name" },
            { data: "code", title: "Secret", render: (data) => `<span class="blur-text">${data}</span>` },
            { data: "identifier", title: "Identifier" },
            { data: "systemCode", title: "System Code" },
            { data: "isBank", title: "Bank", render: (data) => data ? "✔️" : "❌" },
            { data: "description", title: "Description" },
        ]
    });

    // Attach click event for copy key buttons
    logTable.off("click", ".copy-key-btn").on("click", ".copy-key-btn", function () {
        const key = $(this).data("key");
        navigator.clipboard.writeText(key)
            .then(() => showToast("success", "Copied to clipboard."))
            .catch(err => showToast("error", "Copy failed."));
    });

    // Attach click event for view/edit
    logTable.off("click", ".view-link").on("click", ".edit-link", function () {
        const rowData = table.row($(this).closest("tr")).data();
        if (!rowData) {
            console.error("No row data found for view/edit");
            return;
        }
        openEditModal(rowData);
    });
}

// Elements
const openCreateModalBtn = document.getElementById("openCreateModalBtn");
const createModal = document.getElementById("createModal");
const createModalClose = document.getElementById("createModalClose");
const cancelCreateModal = document.getElementById("cancelCreateModal");
const createPartnerForm = document.getElementById("createPartnerForm");

// Open Modal
openCreateModalBtn.addEventListener("click", () => {
    clearErrors(createModal);
    createPartnerForm.reset();
    createModal.style.display = "flex";
});

// Close Modal
[createModalClose, cancelCreateModal].forEach(el => {
    el.addEventListener("click", () => {
        closeCreateModal();
    });
});

/**
 * Closes the create partner modal, resets the form, and clears validation errors.
 */
function closeCreateModal() {
    createModal.style.display = "none";
    createPartnerForm.reset();
    clearErrors(createModal);
}

/**
 * Clears all validation errors and styles within the given modal.
 * @param {HTMLElement} modal - The modal element containing inputs and error messages
 */
function clearErrors(modal) {
    modal.querySelectorAll(".error-message").forEach(el => el.textContent = "");
    modal.querySelectorAll("input, textarea").forEach(input => input.classList.remove("error"));
}

/**
 * Handles API error responses by displaying field-specific errors
 * or showing a toast message if no field matches.
 * @param {object} result - API response containing error or message
 */
function handleApiErrorMessage(result) {
    if (!result) return;

    clearErrors(createModal);

    // Field-level errors
    if (result.error && typeof result.error === "object") {
        Object.keys(result.error).forEach(key => {
            const inputId = "create" + capitalize(key); // e.g. createName
            const inputEl = document.getElementById(inputId);
            const errorEl = document.getElementById(`error${capitalize(inputId)}`);
            if (inputEl && errorEl) {
                inputEl.classList.add("error");
                errorEl.textContent = result.error[key];
            }
        });
    }
    // If only message exists, try mapping to field by keyword
    else if (result.message) {
        const msg = result.message.toLowerCase();
        const fieldMap = [
            { keyword: "name", id: "createName" },
            { keyword: "identifier", id: "createIdentifier" },
            { keyword: "code", id: "createCode" },
            { keyword: "system code", id: "createSystemCode" },
            { keyword: "description", id: "createDescription" },
        ];

        let matched = false;
        fieldMap.forEach(f => {
            if (msg.includes(f.keyword)) {
                const inputEl = document.getElementById(f.id);
                const errorEl = document.getElementById(`error${capitalize(f.id)}`);
                if (inputEl && errorEl) {
                    inputEl.classList.add("error");
                    errorEl.textContent = result.message;
                    matched = true;
                }
            }
        });

        if (!matched) {
            // fallback: show toast if no keyword matches
            showToast("error", result.message);
        }
    }
}

/**
 * Handles create partner form submission:
 * - Prevents default submit
 * - Clears previous errors
 * - Sends form data to backend
 * - Shows field errors or success messages
 */
createPartnerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearErrors(createModal);

    const payload = {
        name: document.getElementById("createName").value.trim(),
        identifier: document.getElementById("createIdentifier").value.trim(),
        systemCode: document.getElementById("createSystemCode").value.trim(),
        code: document.getElementById("createCode").value.trim(),
        description: document.getElementById("createDescription").value.trim(),
        isBank: document.getElementById("createIsBank").value === "true"
    };

    try {
        const response = await fetch(`${apiPrefix}/create-partner`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        // Treat 201 code or HTTP OK as success
        if (response.ok || result.code === 201) {
            showToast("success", "Created successfully.");
            closeCreateModal();
            fetchData();
            return;
        }

        // Otherwise handle errors
        handleApiErrorMessage(result);

    } catch (err) {
        console.error("Create partner failed:", err);
        showToast("error", "Failed to create partner.");
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

    document.getElementById("modalName").value = item.name || "";
    document.getElementById("modalIdentifier").value = item.identifier || "";
    document.getElementById("modalSystemCode").value = item.systemCode || "";
    document.getElementById("modalDescription").value = item.description || "";
    document.getElementById("modalCode").value = item.code || "";

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
 * Validates required fields in the modal form.
 * Clears previous errors, then validates 'name', 'identifier', 'systemCode', 'code', and 'description'.
 * Shows errors if fields are empty or API returns errors.
 * @param {Object} apiErrors - Optional API error messages keyed by field names.
 * @returns {boolean} - True if valid, false otherwise.
 */
function validateModalFields(apiErrors = {}) {
    const fields = ["name", "identifier", "systemCode", "code", "description"];
    let isValid = true;

    fields.forEach(field => {
        const errorEl = document.getElementById(`error${capitalize(field)}`);
        if (errorEl) errorEl.textContent = ''; // Clear previous error

        if (apiErrors[field]) {
            if (errorEl) errorEl.textContent = apiErrors[field];
            isValid = false;
        }
    });

    return isValid;
}

/**
 * Handles the partner edit form submission.
 * Prevents default submit, validates input, sends update request,
 * handles API errors, and refreshes data on success.
 */
// Handle partner edit form submission
document.getElementById("editPartnerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!currentEditId) {
        showToast("error", "No partner selected for editing.");
        return;
    }

    // Collect modal input data
    const modalData = {
        name: document.getElementById("modalName").value.trim(),
        identifier: document.getElementById("modalIdentifier").value.trim(),
        systemCode: document.getElementById("modalSystemCode").value.trim(),
        description: document.getElementById("modalDescription").value.trim(),
        code: document.getElementById("modalCode").value.trim()
    };

    // Simple front-end validation
    let hasError = false;
    Object.keys(modalData).forEach(field => {
        const inputEl = document.getElementById(`modal${capitalize(field)}`);
        if (!modalData[field]) {
            showErrorField(inputEl, `${capitalize(field)} is required`);
            hasError = true;
        }
    });
    if (hasError) return;

    try {
        const response = await fetch(`${apiPrefix}/update-partner/${currentEditId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            },
            body: JSON.stringify(modalData)
        });

        const result = await response.json();

        if (!response.ok) {
            // Handle API validation errors
            if (result.errors) {
                validateModalFields(result.errors);
            } else {
                showToast("error", result.message || "Failed to update partner.");
            }
            return;
        }

        showToast("success", "Update successful.");
        closeModal();
        fetchData();

    } catch (err) {
        console.error("Update failed:", err);
        showToast("error", "Failed to update partner.");
    }
});

// Filter button triggers data fetch with current filter inputs
filterBtn.addEventListener("click", () => {
    fetchData();
});

// Initial data fetch on page load
fetchData();