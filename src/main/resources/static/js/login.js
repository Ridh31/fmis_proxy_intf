document.addEventListener("DOMContentLoaded", () => {
    // Form and UI element references
    const form = document.getElementById("login-form");
    const username = document.getElementById("username");
    const password = document.getElementById("password");
    const usernameError = document.getElementById("username-error");
    const passwordError = document.getElementById("password-error");
    const loginBtn = document.querySelector(".login-btn");
    const spinner = document.getElementById("spinner");
    const buttonText = document.getElementById("button-text");
    const closeModalBtn = document.querySelector(".modal-close-btn");
    const popupModal = document.getElementById("popup-modal");
    const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;

    const MESSAGES = {
        LOGIN_SUCCESS: "Login successful.",
        LOGIN_ERROR_INVALID: "Access denied: Invalid admin credentials.",
        LOGIN_ERROR_SERVER: "Something went wrong. Please try again later."
    };

    if (!form) return;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        let isValid = true;

        // Reset error styles and messages
        if (username) username.classList.remove("error");
        if (password) password.classList.remove("error");
        if (usernameError) usernameError.style.display = "none";
        if (passwordError) passwordError.style.display = "none";

        // Input validation
        if (!username?.value.trim()) {
            if (username) username.classList.add("error");
            if (usernameError) usernameError.style.display = "block";
            isValid = false;
        }

        if (!password?.value.trim()) {
            if (password) password.classList.add("error");
            if (passwordError) passwordError.style.display = "block";
            isValid = false;
        }

        if (!isValid) return;

        // Show loading spinner
        if (loginBtn) loginBtn.disabled = true;
        if (spinner) spinner.style.display = "inline-block";
        if (buttonText) buttonText.style.display = "none";

        const formData = new FormData();
        formData.append("username", username.value.trim());
        formData.append("password", password.value.trim());

        try {
            const response = await fetch(`${window.location.origin}${apiPrefix}/auth/verify-admin`, {
                method: "POST",
                body: formData
            });

            const contentType = response.headers.get("content-type");
            const isJson = contentType && contentType.includes("application/json");
            const result = isJson ? await response.json() : null;

            if (response.ok && result?.code === 200 && result.data?.status === true) {
                document.cookie = "isAdmin=true; path=/; SameSite=Lax; Secure";
                document.cookie = `adminUsername=${username.value.trim()}; path=/; SameSite=Lax; Secure`;
                document.cookie = `adminPassword=${password.value.trim()}; path=/; SameSite=Lax; Secure`;

                const urlParams = new URLSearchParams(window.location.search);
                const redirectTo = urlParams.get("redirect") || `${apiPrefix}/admin/home`;

                window.location.href = redirectTo;
                return;
            } else {
                if (username) username.classList.add("error");
                if (password) password.classList.add("error");
                if (usernameError) {
                    usernameError.textContent = MESSAGES.LOGIN_ERROR_INVALID;
                    usernameError.style.display = "block";
                    showToast("error", MESSAGES.LOGIN_ERROR_INVALID);
                }
            }
        } catch (err) {
            console.error("Error verifying admin:", err);
            if (username) username.classList.add("error");
            if (password) password.classList.add("error");
            if (usernameError) {
                usernameError.textContent = MESSAGES.LOGIN_ERROR_SERVER;
                usernameError.style.display = "block";
                showToast("error", MESSAGES.LOGIN_ERROR_SERVER);
            }
        } finally {
            if (loginBtn) loginBtn.disabled = false;
            if (spinner) spinner.style.display = "none";
            if (buttonText) buttonText.style.display = "inline";
        }
    });
});