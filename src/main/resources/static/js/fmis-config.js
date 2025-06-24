// Get credentials from data attributes
const username = document.querySelector("[data-username]")?.dataset.username;
const password = document.querySelector("[data-password]")?.dataset.password;

// Encode credentials in base64 for Basic Auth
const basicAuth = btoa(`${username}:${password}`);

/**
 * Fetches configuration data from the API and updates UI fields accordingly.
 * Requires username and password stored in HTML data attributes.
 */
async function fetchConfig() {
    try {
        const response = await fetch(`${window.location.origin}/api/v1/list-config`, {
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "Accept": "application/json"
            }
        });

        const result = await response.json();

        if (result.code === 200 && result.data?.content?.length > 0) {
            const config = result.data.content[0];

            // Populate display fields
            document.getElementById("baseURLField").textContent = config.baseURL;
            document.getElementById("usernameField").textContent = config.username;
            document.getElementById("passwordField").value = "•••••••••";
            document.getElementById("editPasswordHidden").textContent = config.password;
            document.getElementById("contentTypeField").textContent = config.contentType;
            document.getElementById("descriptionField").textContent = config.description;
        } else {
            console.warn("No config data found.");
        }
    } catch (error) {
        console.error("❌ Error fetching config:", error);
    }
}

// Fetch config on DOM load
window.addEventListener("DOMContentLoaded", fetchConfig);

// Make modal draggable with jQuery UI
$(".modal-content").draggable({ cursor: "move" });

/**
 * Handle logout:
 * - Clear cookies
 * - Redirect to login page
 */
document.querySelector(".btn-logout")?.addEventListener("click", () => {
    const deleteCookie = name => {
        document.cookie = `${name}=; Max-Age=0; path=/; SameSite=Lax;`;
    };

    deleteCookie("isAdmin");
    deleteCookie("adminUsername");
    deleteCookie("adminPassword");

    window.location.href = "/api/v1/admin/login";
});

// Modal and form handling
document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("editModal");
    const openModalBtn = document.getElementById("openModalBtn");
    const saveBtn = document.getElementById("saveBtn");
    const cancelBtn = document.getElementById("cancelBtn");

    const baseURLField = document.getElementById("baseURLField");
    const usernameField = document.getElementById("usernameField");
    const passwordField = document.getElementById("passwordField");
    const contentTypeField = document.getElementById("contentTypeField");
    const descriptionField = document.getElementById("descriptionField");

    const editBaseURL = document.getElementById("editBaseURL");
    const editUsername = document.getElementById("editUsername");
    const editPassword = document.getElementById("editPassword");
    const hiddenPassword = document.getElementById("editPasswordHidden");
    const editContentType = document.getElementById("editContentType");
    const editDescription = document.getElementById("editDescription");

    // Open modal and populate fields
    openModalBtn?.addEventListener("click", () => {
        editBaseURL.value = baseURLField.textContent.trim();
        editUsername.value = usernameField.textContent.trim();
        editPassword.value = hiddenPassword.textContent.trim();
        editContentType.value = contentTypeField.textContent.trim();
        editDescription.value = descriptionField.textContent.trim();

        modal.classList.remove("hidden");
    });

    // Close modal
    cancelBtn?.addEventListener("click", () => {
        modal.classList.add("hidden");
        clearValidationErrors();
    });

    // Save changes
    saveBtn?.addEventListener("click", async () => {
        clearValidationErrors();

        const payload = {
            baseURL: editBaseURL.value.trim(),
            username: editUsername.value.trim(),
            password: editPassword.value.trim(),
            contentType: editContentType.value.trim(),
            description: editDescription.value.trim()
        };

        const validationErrors = {};

        if (!payload.baseURL) validationErrors.baseURL = "Base URL cannot be empty.";
        if (!payload.username) validationErrors.username = "Username cannot be empty.";
        if (!payload.password) validationErrors.password = "Password cannot be empty.";
        if (!payload.contentType) validationErrors.contentType = "Content Type cannot be empty.";
        if (!payload.description) validationErrors.description = "Description cannot be empty.";

        if (Object.keys(validationErrors).length > 0) {
            showValidationErrors(validationErrors);
            return;
        }

        try {
            const response = await fetch(`${window.location.origin}/api/v1/update-fmis-config`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Basic ${basicAuth}`
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                if (response.status === 400) {
                    const errorResponse = await response.json();
                    if (errorResponse.error) {
                        showValidationErrors(errorResponse.error);
                    } else {
                        alert(errorResponse.message || "Validation failed.");
                    }
                    return;
                }
                throw new Error("Failed to update config");
            }

            // Success: update display fields
            baseURLField.textContent = payload.baseURL;
            usernameField.textContent = payload.username;
            passwordField.value = "•••••••••";
            document.getElementById("editPasswordHidden").textContent = payload.password;
            contentTypeField.textContent = payload.contentType;
            descriptionField.textContent = payload.description;

            modal.classList.add("hidden");
            await fetchConfig();
        } catch (error) {
            console.error("❌ Error updating config:", error);
            alert("Failed to update configuration. Please try again.");
        }
    });
});

/**
 * Removes all previously displayed validation error messages from the form.
 * This ensures a clean state before showing new validation errors.
 */
function clearValidationErrors() {
    document.querySelectorAll(".validation-error").forEach(el => el.remove());
    document.querySelectorAll(".input-error").forEach(el => el.classList.remove("input-error"));
}

/**
 * Displays validation error messages next to their respective input fields.
 *
 * @param {Object} errors - A map of field names to error messages.
 */
function showValidationErrors(errors) {
    for (const [field, message] of Object.entries(errors)) {
        const inputEl = document.getElementById("edit" + capitalize(field));
        if (inputEl) {
            inputEl.classList.add("input-error");
            const errorEl = document.createElement("div");
            errorEl.className = "validation-error";
            errorEl.style.color = "red";
            errorEl.style.fontSize = "0.85rem";
            errorEl.textContent = message;
            inputEl.parentNode.appendChild(errorEl);
        }
    }
}

/**
 * Helper function to capitalize the first letter of a given string.
 *
 * @param {string} str - The input string to be capitalized.
 * @returns {string} A new string with the first character in uppercase, followed by the rest of the original string.
 */
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}