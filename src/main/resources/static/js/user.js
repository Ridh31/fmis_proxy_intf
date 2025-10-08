// Retrieve credentials and tokens from DOM dataset attributes or default to empty string
const username = document.querySelector("[data-username]")?.dataset.username || "";
const password = document.querySelector("[data-password]")?.dataset.password || "";
const partnerToken = document.querySelector("[data-partner-token]")?.dataset.partnerToken || "";
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;
const adminLevel = document.querySelector(".admin-level")?.dataset.adminLevel;

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
                render: (data, type, row) => {
                    const userLevel = row.role?.level ?? 5;
                    const currentLevel = parseInt(adminLevel, 10);
                    const rowUsername = row.username;

                    if (username === rowUsername || currentLevel < userLevel) {
                        return `<span class="reset-password">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="var(--color-blue-dark)" class="size-6">
                                        <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 5.25a3 3 0 0 1 3 3m3 0a6 6 0 0 1-7.029 5.912c-.563-.097-1.159.026-1.563.43L10.5 17.25H8.25v2.25H6v2.25H2.25v-2.818c0-.597.237-1.17.659-1.591l6.499-6.499c.404-.404.527-1 .43-1.563A6 6 0 1 1 21.75 8.25Z" />
                                    </svg>
                                </span>`
                    } else {
                        return `<span class="not-allowed">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="var(--color-blue-dark)" class="size-6">
                                        <path stroke-linecap="round" stroke-linejoin="round" d="M18.364 18.364A9 9 0 0 0 5.636 5.636m12.728 12.728A9 9 0 0 1 5.636 5.636m12.728 12.728L5.636 5.636" />
                                    </svg>
                                </span>`;
                    }
                }
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

    // User Modal Elements
    const modal = document.getElementById("modal");
    const userModalTitle = document.getElementById("userModalTitle");
    const modalSubmit = document.querySelector('button[form="userForm"]');
    const passwordGroup = document.getElementById("modalPassword")?.parentElement;
    const form = document.getElementById("userForm");
    const partnerSelect = document.getElementById("modalPartner");

    // Inputs
    const inputUsername = document.getElementById("modalUsername");
    const inputEmail = document.getElementById("modalEmail");
    const inputPassword = document.getElementById("modalPassword");
    const inputConfirmPassword = document.getElementById("modalConfirmPassword");
    const inputRole = document.getElementById("modalRole");

    // Mode tracking
    let isUpdateMode = false;

    // Reset Modal Elements
    const resetModal = document.getElementById("resetModal");
    const resetUsernameInput = document.getElementById("resetUsername");
    const resetPasswordInput = document.getElementById("resetPassword");
    const resetConfirmPasswordInput = document.getElementById("resetConfirmPassword");
    const resetForm = document.getElementById("resetPasswordForm");

    /**
     * Displays an error message below a specific input field.
     * @param {string} fieldId - The ID of the error message element.
     * @param {string} message - The message to display.
     */
    function showErrorField(fieldId, message) {
        const el = document.getElementById(fieldId);
        if (el) el.textContent = message;
    }

    /**
     * Clears all error messages from the form or container.
     * @param {HTMLElement} container - Optional container element to clear errors from.
     */
    function clearErrorFields(container = document) {
        container.querySelectorAll(".error-message").forEach(el => el.textContent = "");
    }

    /**
     * Fetches the list of partners from the server and populates the partner dropdown.
     * Shows a toast message if the fetch fails.
     */
    async function loadPartners() {
        try {
            const res = await fetch(`${baseUrl}${apiPrefix}/list-partner`, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Basic ${basicAuth}`,
                    "X-Partner-Token": partnerToken
                }
            });
            if (!res.ok) throw new Error("Failed to fetch partners");

            const json = await res.json();
            const partners = json?.data?.content || [];

            partnerSelect.innerHTML = `<option value="">-- Select --</option>`;
            partners.forEach(p => {
                const option = document.createElement("option");
                option.value = p.id;
                option.textContent = p.name || `Partner ${p.id}`;
                partnerSelect.appendChild(option);
            });
        } catch (err) {
            console.error(err);
            showToast("error", "Failed to load partners.");
        }
    }

    /**
     * Opens the modal for creating or updating a user, and fills in user data if updating.
     * @param {string} type - "create" or "update".
     * @param {Object} userData - Existing user data for update.
     */
    function openUserModal(type, userData = {}) {
        isUpdateMode = type === "update";
        modal.style.display = "flex";
        loadPartners();

        if (isUpdateMode) {
            userModalTitle.textContent = "Update User";
            modalSubmit.textContent = "Save";
            passwordGroup.style.display = "none";
            inputUsername.value = userData.username || "";
            inputEmail.value = userData.email || "";
            inputRole.value = userData.role?.id || "";
            partnerSelect.value = userData.partner?.id || "";
            inputPassword.value = "";
            inputConfirmPassword.value = "";
        } else {
            userModalTitle.textContent = "Create User";
            modalSubmit.textContent = "Save";
            passwordGroup.style.display = "block";
            form.reset();
        }
        clearErrorFields();
    }

    /**
     * Opens the reset password modal for a specific user.
     * Pre-fills the username and clears password fields and any error messages.
     * @param {string} username - The username of the user whose password will be reset.
     */
    function openResetModal(username) {
        resetUsernameInput.value = username || "";
        resetPasswordInput.value = "";
        resetConfirmPasswordInput.value = "";
        clearErrorFields(resetModal);
        resetModal.style.display = "flex";
    }

    // Close Modals
    document.getElementById("modalClose").addEventListener("click", () => modal.style.display = "none");
    document.getElementById("cancelModal").addEventListener("click", () => modal.style.display = "none");
    document.getElementById("resetModalClose").addEventListener("click", () => resetModal.style.display = "none");
    document.getElementById("cancelResetModal").addEventListener("click", () => resetModal.style.display = "none");

    // User Form Submit
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearErrorFields();

        const username = inputUsername.value.trim();
        const email = inputEmail.value.trim();
        const password = inputPassword.value;
        const confirmPassword = inputConfirmPassword.value;
        const roleId = parseInt(inputRole.value);
        const partnerId = parseInt(partnerSelect.value);

        let hasError = false;

        if (!username) { showErrorField("errorUsername", "Username is required."); hasError = true; }
        if (!isUpdateMode && !password) { showErrorField("errorPassword", "Password is required."); hasError = true; }
        if (!isUpdateMode && password !== confirmPassword) { showErrorField("errorConfirmPassword", "Passwords do not match."); hasError = true; }
        if (!roleId) { showErrorField("errorRole", "Role is required."); hasError = true; }
        if (!partnerId) { showErrorField("errorPartner", "Branch is required."); hasError = true; }

        if (hasError) return;

        const data = {
            username,
            email,
            password: !isUpdateMode ? password : undefined,
            roleId,
            partnerId
        };

        try {
            const endpoint = isUpdateMode
                ? `${baseUrl}${apiPrefix}/auth/update-user`
                : `${baseUrl}${apiPrefix}/auth/register`;

            const res = await fetch(endpoint, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Basic ${basicAuth}`,
                    "X-Partner-Token": partnerToken
                },
                body: JSON.stringify(data)
            });

            const json = await res.json();

            if (!res.ok) {
                if (json.error) {
                    for (const [field, message] of Object.entries(json.error)) {
                        const errorFieldId = `error${field.charAt(0).toUpperCase() + field.slice(1)}`;
                        showErrorField(errorFieldId, message);
                    }
                } else if (json.message) {
                    const msg = json.message.toLowerCase();
                    if (msg.includes("username")) showErrorField("errorUsername", json.message);
                    else if (msg.includes("password")) showErrorField("errorPassword", json.message);
                    else if (msg.includes("role")) showErrorField("errorRole", json.message);
                    else if (msg.includes("partner") || msg.includes("branch")) showErrorField("errorPartner", json.message);
                    else showToast("error", json.message);
                } else {
                    showToast("error", "Failed to save user.");
                }
                return;
            }

            showToast("success", isUpdateMode ? "Updated successfully." : "Created successfully.");
            modal.style.display = "none";
            form.reset();
            fetchData();

        } catch (err) {
            console.error(err);
            showToast("error", isUpdateMode ? "Failed to update user." : "Failed to save user.");
        }
    });

    // Reset Password Submit
    resetForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearErrorFields(resetModal);

        const username = resetUsernameInput.value.trim();
        const password = resetPasswordInput.value;
        const confirmPassword = resetConfirmPasswordInput.value;

        if (!password) {
            document.getElementById("errorResetPassword").textContent = "Password is required.";
            return;
        } else if (password.length < 6) {
            document.getElementById("errorResetPassword").textContent = "Password must be at least 6 characters.";
            return;
        } else if (password !== confirmPassword) {
            document.getElementById("errorResetConfirmPassword").textContent = "Passwords do not match.";
            return;
        }

        try {
            const formData = new FormData();
            formData.append("username", username);
            formData.append("password", password);

            const res = await fetch(`${baseUrl}${apiPrefix}/auth/reset-password`, {
                method: "PUT",
                headers: { "Authorization": `Basic ${basicAuth}` },
                body: formData
            });

            const json = await res.json();
            if (!res.ok) {
                showErrorField("errorResetPassword", json.message || "Failed to reset password.");
                return;
            }

            showToast("success", "Password reset successfully.");
            resetModal.style.display = "none";

        } catch (err) {
            console.error(err);
            showToast("error", "Failed to reset password.");
        }
    });

    // Open Create User Modal
    document.getElementById("openModalBtn")?.addEventListener("click", () => openUserModal("create"));

    // Handle clicks on reset and not-allowed icons
    document.querySelector("#logTable tbody").addEventListener("click", (e) => {
        const resetIcon = e.target.closest(".reset-password");
        const notAllowedIcon = e.target.closest(".not-allowed");

        if (resetIcon) {
            const rowData = logTable.DataTable().row(resetIcon.closest("tr")).data();
            if (!rowData) return;

            resetUsernameInput.value = rowData.username || "";
            resetPasswordInput.value = "";
            resetConfirmPasswordInput.value = "";
            document.getElementById("errorResetPassword").textContent = "";
            resetModal.style.display = "flex";
            return;
        }

        if (notAllowedIcon) {
            showToast("warning", "Action not allowed.");
        }
    });

    /**
     * Attaches click event listeners to all edit buttons in the user table.
     * When an edit button is clicked, it opens the user modal in update mode
     * and fills in the selected row's data.
     */
    function attachEditListeners() {
        document.querySelectorAll(".edit-link").forEach(btn => {
            btn.addEventListener("click", () => {
                const rowData = logTable.DataTable().row(btn.closest("tr")).data();
                openUserModal("update", rowData);
            });
        });
    }
});

// Filter button triggers data fetch with current filter inputs
filterBtn.addEventListener("click", () => {
    fetchData();
});

// Initial data fetch on page load
fetchData();