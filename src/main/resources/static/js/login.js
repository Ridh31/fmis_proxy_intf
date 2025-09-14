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

    // Make the modal draggable using jQuery UI
    $(".modal-box").draggable({
        cursor: "move"
    });

    // Close modal button functionality
    closeModalBtn.addEventListener("click", () => {
        popupModal.style.display = "none";
    });

    // Login form submission handler
    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        let isValid = true;

        // Reset error styles and messages
        username.classList.remove("error");
        password.classList.remove("error");
        usernameError.style.display = "none";
        passwordError.style.display = "none";

        // Input validation
        if (username.value.trim() === "") {
            username.classList.add("error");
            usernameError.style.display = "block";
            isValid = false;
        }

        if (password.value.trim() === "") {
            password.classList.add("error");
            passwordError.style.display = "block";
            isValid = false;
        }

        if (!isValid) return;

        // Show spinner
        loginBtn.disabled = true;
        spinner.style.display = "inline-block";
        buttonText.style.display = "none";

        const formData = new FormData();
        formData.append("username", username.value.trim());
        formData.append("password", password.value.trim());

        try {
            const response = await fetch(`${window.location.origin}${apiPrefix}/auth/verify-admin`, {
                method: "POST",
                body: formData
            });

            const result = await response.json();

            if (result.code === 200 && result.data === true) {
                // Save admin cookies
                document.cookie = "isAdmin=true; path=/; SameSite=Lax; Secure";
                document.cookie = `adminUsername=${username.value.trim()}; path=/; SameSite=Lax; Secure`;
                document.cookie = `adminPassword=${password.value.trim()}; path=/; SameSite=Lax; Secure`;

                // Determine where to redirect
                const urlParams = new URLSearchParams(window.location.search);
                const redirectTo = urlParams.get("redirect") || `${apiPrefix}/admin/dashboard`;

                // Redirect
                window.location.href = redirectTo;
            } else {
                username.classList.add("error");
                password.classList.add("error");
                usernameError.textContent = "Access denied: Invalid admin credentials.";
                usernameError.style.display = "block";
            }
        } catch (err) {
            console.error("Error verifying admin: ", err);
            username.classList.add("error");
            password.classList.add("error");
            usernameError.textContent = "An unexpected error occurred. Please try again.";
            usernameError.style.display = "block";
        } finally {
            loginBtn.disabled = false;
            spinner.style.display = "none";
            buttonText.style.display = "inline";
        }
    });
});