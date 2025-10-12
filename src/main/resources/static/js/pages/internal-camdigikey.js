const url = `${baseUrl}${apiPrefix}/internal/camdigikey/list-host`;

// Initialize jQuery UI draggable for modal and flatpickr datepicker
$(() => {
    flatpickr("#createdDate", {
        dateFormat: "d/m/Y",
        allowInput: true
    });
});

/**
 * Loads or refreshes the DataTable.
 * - Calls renderTable() on first load.
 * - Uses ajax.reload() for filter changes.
 */
async function fetchData() {
    showLoading(7);

    if ($.fn.DataTable.isDataTable("#logTable")) {
        logTable.DataTable().ajax.reload();
        return;
    }

    renderTable();
}

/**
 * Initializes the DataTable for CamDigikey logs with server-side processing.
 * - Fetches only the current page from the server.
 * - Applies filters dynamically.
 * - Shows loading spinner during AJAX requests.
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
                    appKey: document.getElementById("appKey").value || undefined,
                    ipAddress: document.getElementById("ipAddress").value || undefined,
                    accessURL: document.getElementById("accessURL").value || undefined,
                    createdDate: document.getElementById("createdDate").value
                        ? formatDate(document.getElementById("createdDate").value)
                        : undefined
                };
            },
            beforeSend: showLoading(7),
            complete: hideLoading,
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            },
            dataSrc: function (json) {
                json.recordsTotal = json?.data?.totalElements || 0;
                json.recordsFiltered = json?.data?.totalElements || 0;
                return json?.data?.content || [];
            },
            error: function () {
                showError(7);
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
            { data: "name", title: "Name" },
            { data: "appKey", title: "App Key" },
            { data: "ipAddress", title: "IP Address" },
            { data: "accessURL", title: "Access URL" },
            { data: "createdDate", title: "Created Date", defaultContent: "N/A" }
        ]
    });

    // attach handler once per render
    attachEditHandler(table);
}

// Open the Create Modal
document.getElementById("openModalBtn").addEventListener("click", () => {
    document.getElementById("createModal").style.display = "flex";
});

/**
 * Closes the create modal, clears error messages, and resets the form fields.
 */
function closeCreateModal() {
    document.getElementById("createModal").style.display = "none";
    clearErrors();
    document.getElementById("createHostForm").reset();
}

// Close buttons
document.getElementById("createModalClose").addEventListener("click", closeCreateModal);
document.getElementById("cancelCreateModal").addEventListener("click", closeCreateModal);

/**
 * Clears all validation error styles and messages from inputs
 * inside the modal forms (#modal and #createModal).
 */
function clearErrors() {
    const inputs = document.querySelectorAll("#modal input, #createModal input");
    inputs.forEach(input => {
        input.classList.remove("error");
        const nextElem = input.nextElementSibling;
        if (nextElem && nextElem.classList.contains("error-message")) {
            nextElem.remove();
        }
    });
}

/**
 * Validates modal input fields and displays error messages if invalid.
 * @param {object} data - Form data to validate, expected to have properties: name, appKey, ipAddress, accessURL
 * @returns {boolean} - True if all fields are valid, otherwise false
 */
function validateCreateFields(data) {
    let valid = true;
    clearErrors();

    if (!data.name.trim()) {
        showErrorField(document.getElementById("createName"), "Name cannot be empty. Please provide a valid name.");
        valid = false;
    }
    if (!data.appKey.trim()) {
        showErrorField(document.getElementById("createAppKey"), "App key cannot be empty. Please provide a valid app key.");
        valid = false;
    }
    if (!data.ipAddress.trim()) {
        showErrorField(document.getElementById("createIP"), "IP address cannot be empty. Please provide a valid IP address.");
        valid = false;
    }
    if (!validateURL(data.accessURL.trim())) {
        showErrorField(document.getElementById("createURL"), "Access URL is invalid or missing.");
        valid = false;
    }

    return valid;
}

/**
 * Handles create host form submission:
 * - Prevents default submit
 * - Validates fields and sends data to backend
 * - Shows errors or success messages
 */
document.getElementById("createHostForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const formData = {
        name: document.getElementById("createName").value.trim(),
        appKey: document.getElementById("createAppKey").value.trim(),
        ipAddress: document.getElementById("createIP").value.trim(),
        accessURL: document.getElementById("createURL").value.trim()
    };

    if (!validateCreateFields(formData)) return;

    try {
        const res = await fetch(`${apiPrefix}/internal/camdigikey/import-host`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${basicAuth}`
            },
            body: JSON.stringify(formData)
        });

        const resData = await res.json().catch(() => null);

        if (!res.ok) {
            // Handle backend validation (field errors)
            if (resData?.error) {
                Object.entries(resData.error).forEach(([field, msg]) => {
                    const input = document.getElementById(`create${capitalize(field)}`);
                    if (input) showErrorField(input, msg);
                });
            } else if (resData?.message) {
                const msgLower = resData.message.toLowerCase();
                if (msgLower.includes("app key")) {
                    showErrorField(document.getElementById("createAppKey"), resData.message);
                } else if (msgLower.includes("name")) {
                    showErrorField(document.getElementById("createName"), resData.message);
                } else if (msgLower.includes("ip address")) {
                    showErrorField(document.getElementById("createIP"), resData.message);
                } else if (msgLower.includes("access url")) {
                    showErrorField(document.getElementById("createURL"), resData.message);
                } else {
                    showToast("error", resData.message);
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
        console.error("Error creating host:", err);
        showToast("error", "Something went wrong while creating host.");
    }
});


// Variable to track the currently edited item ID
let currentEditId = null;

/**
 * Opens the edit modal and fills it with data of the selected item.
 * @param {object} item - Data item to edit
 */
function openEditModal(item) {
    if (!item) {
        console.error("No row data found for edit");
        return;
    }

    currentEditId = item.id;

    document.getElementById("modalName").value = item.name || "";
    document.getElementById("modalAppKey").value = item.appKey || "";
    document.getElementById("modalIP").value = item.ipAddress || "";
    document.getElementById("modalURL").value = item.accessURL || "";

    document.getElementById("modal").style.display = "flex";  // Show modal
}

/**
 * Attach edit handler to table
 */
function attachEditHandler(table) {
    logTable.off("click", ".edit-link").on("click", ".edit-link", function () {
        const rowData = table.row($(this).closest('tr')).data();
        openEditModal(rowData);
    });
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
 * Validates modal input fields and displays error messages if invalid.
 * @param {object} data - Form data to validate
 * @returns {boolean} - True if all fields are valid, otherwise false
 */
function validateModalFields(data) {
    let valid = true;
    clearErrors();

    if (!data.name.trim()) {
        const input = document.getElementById("modalName");
        showErrorField(input, "Name is required. Please provide a valid name.");
        valid = false;
    }
    if (!data.appKey.trim()) {
        const input = document.getElementById("modalAppKey");
        showErrorField(input, "App key is required. Please provide a valid app key.");
        valid = false;
    }
    if (!data.ipAddress.trim()) {
        const input = document.getElementById("modalIP");
        showErrorField(input, "IP address is required. Please provide a valid IP address.");
        valid = false;
    }
    if (!validateURL(data.accessURL.trim())) {
        const input = document.getElementById("modalURL");
        showErrorField(input, "Access URL is required. Please provide a valid access URL.");
        valid = false;
    }

    return valid;
}

/**
 * Validates a URL by attempting to create a URL object.
 * @param {string} url - URL string to validate
 */
function validateURL(url) {
    return url && url.trim().length > 0;
}


/**
 * Handles the server edit form submission.
 * Prevents default submit, validates input, sends update request,
 * handles API errors, and refreshes data on success.
 */
document.getElementById("editHostForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const updatedData = {
        name: document.getElementById("modalName").value.trim(),
        appKey: document.getElementById("modalAppKey").value.trim(),
        ipAddress: document.getElementById("modalIP").value.trim(),
        accessURL: document.getElementById("modalURL").value.trim()
    };

    clearErrors();

    if (!currentEditId) return showToast("error", "Missing ID for update.");

    // Client-side validation: stop submission if invalid
    if (!validateModalFields(updatedData)) return;

    try {
        const res = await fetch(`${apiPrefix}/internal/camdigikey/update-host/${currentEditId}`, {
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
                if (errData.message && typeof errData.message === "string") {
                    const message = errData.message.toLowerCase();

                    if (message.includes("name") && !errData.error) {
                        const input = document.getElementById("modalName");
                        showErrorField(input, errData.message);
                    } else if (message.includes("app key")) {
                        const input = document.getElementById("modalAppKey");
                        showErrorField(input, errData.message);
                    } else if (message.includes("ip address")) {
                        const input = document.getElementById("modalIP");
                        showErrorField(input, errData.message);
                    } else if (message.includes("access url")) {
                        const input = document.getElementById("modalURL");
                        showErrorField(input, errData.message);
                    } else {
                        showToast("error", errData.message);
                    }
                }

                if (errData.error && typeof errData.error === "object") {
                    Object.entries(errData.error).forEach(([field, msg]) => {
                        const input = document.getElementById(`modal${capitalize(field)}`);
                        if (input) {
                            showErrorField(input, msg);
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
        showToast("error", "Something went wrong.");
        console.error(err);
    }
});

// Filter button triggers data fetch with current filter inputs
filterBtn.addEventListener("click", () => {
    fetchData();
});

// Initial data fetch on page load
fetchData();