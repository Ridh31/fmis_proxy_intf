// Get credentials from data attributes
const username = document.querySelector("[data-username]")?.dataset.username;
const password = document.querySelector("[data-password]")?.dataset.password;
const basicAuth = btoa(`${username}:${password}`);
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;

// Fetch configuration data
async function fetchConfig() {
    try {
        const response = await fetch(`${window.location.origin}${apiPrefix}/list-config`, {
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "Accept": "application/json"
            }
        });
        const result = await response.json();
        if (result.code === 200 && result.data?.content?.length > 0) {
            const config = result.data.content[0];
            window.currentConfig = config; // store globally

            document.getElementById("baseURLField").textContent = config.baseURL;
            document.getElementById("usernameField").textContent = config.username;
            document.getElementById("passwordField").value = "•••••••••";
            document.getElementById("editPasswordHidden").textContent = config.password;
            document.getElementById("contentTypeField").textContent = config.contentType;
            document.getElementById("descriptionField").textContent = config.description;
        }
    } catch (error) {
        console.error("❌ Error fetching config:", error);
        showToast("error", "Error fetching config.");
    }
}

// Open modal and populate fields
function openEditModal() {
    const modal = document.getElementById("modal");
    const config = window.currentConfig;

    document.getElementById("editBaseURL").value = config.baseURL || "";
    document.getElementById("editUsername").value = config.username || "";
    document.getElementById("editPassword").value = config.password || "";
    document.getElementById("editPasswordHidden").textContent = config.password || "";
    document.getElementById("editContentType").value = config.contentType || "";
    document.getElementById("editDescription").value = config.description || "";

    modal.style.display = "flex"; // Show modal
}

// Close modal
function closeModal() {
    const modal = document.getElementById("modal");
    modal.style.display = "none";
    clearValidationErrors();
}

// Save config changes
async function saveConfigChanges() {
    clearValidationErrors();

    const payload = {
        baseURL: document.getElementById("editBaseURL").value.trim(),
        username: document.getElementById("editUsername").value.trim(),
        password: document.getElementById("editPassword").value.trim(),
        contentType: document.getElementById("editContentType").value.trim(),
        description: document.getElementById("editDescription").value.trim()
    };

    // Validation
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
        const response = await fetch(`${window.location.origin}${apiPrefix}/update-fmis-config`, {
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
                if (errorResponse.error) showValidationErrors(errorResponse.error);
                else showToast("error", errorResponse.message || "Validation failed.");
                return;
            }
            throw new Error("Failed to update config");
        }

        // Update display fields
        document.getElementById("baseURLField").textContent = payload.baseURL;
        document.getElementById("usernameField").textContent = payload.username;
        document.getElementById("passwordField").value = "•••••••••";
        document.getElementById("editPasswordHidden").textContent = payload.password;
        document.getElementById("contentTypeField").textContent = payload.contentType;
        document.getElementById("descriptionField").textContent = payload.description;

        showToast("success", "Update successful.");
        closeModal();
        await fetchConfig();
    } catch (error) {
        console.error("❌ Error updating config:", error);
        showToast("error", "Failed to update configuration. Please try again.");
    }
}

/**
 * Clears all validation error messages and removes error styling from inputs.
 * Resets the form to a clean state for further user input.
 */
function clearValidationErrors() {
    document.querySelectorAll(".validation-error").forEach(el => el.remove());
    document.querySelectorAll(".input-error").forEach(el => el.classList.remove("input-error"));
}

/**
 * Displays validation error messages next to the corresponding form inputs.
 * Adds error styling to inputs and inserts error text for each field.
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
 * Capitalizes the first letter of a given string.
 * Returns the string with the first character in uppercase.
 */
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

// DOM Event Listeners
document.addEventListener("DOMContentLoaded", () => {
    fetchConfig();

    $(".modal-content").draggable({ cursor: "move" });

    // Open modal
    document.getElementById("openModalBtn")?.addEventListener("click", openEditModal);

    // Close modal
    document.getElementById("cancelModal").addEventListener("click", closeModal);
    document.getElementById("modalClose").addEventListener("click", closeModal);

    // Handle form submission
    document.getElementById("editConfigForm")?.addEventListener("submit", async (e) => {
        e.preventDefault();
        await saveConfigChanges();
    });

    // Toggle password visibility
    document.querySelector(".toggle-password")?.addEventListener("click", () => {
        const passwordInput = document.getElementById("passwordField");
        const eyeOn = document.querySelector(".eye-on");
        const eyeOff = document.querySelector(".eye-off");
        if (passwordInput.type === "password") {
            passwordInput.type = "text";
            eyeOn.classList.add("active");
            eyeOff.classList.remove("active");
        } else {
            passwordInput.type = "password";
            eyeOn.classList.remove("active");
            eyeOff.classList.add("active");
        }
    });
});
