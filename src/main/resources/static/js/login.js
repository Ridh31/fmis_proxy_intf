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
        e.preventDefault(); // Prevent default form behavior (page reload)

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

        // Show spinner and disable button during request
        loginBtn.disabled = true;
        spinner.style.display = "inline-block";
        buttonText.style.display = "none";

        // Build form data
        const formData = new FormData();
        formData.append("username", username.value.trim());
        formData.append("password", password.value.trim());

        try {
            // Send login credentials to API
            const response = await fetch(`${window.location.origin}/api/v1/auth/verify-admin`, {
                method: "POST",
                body: formData
            });

            const result = await response.json();

            if (result.code === 200 && result.data === true) {
                // On success, store admin credentials in cookies
                document.cookie = "isAdmin=true; path=/; SameSite=Lax; Secure";
                document.cookie = `adminUsername=${username.value.trim()}; path=/; SameSite=Lax; Secure`;
                document.cookie = `adminPassword=${password.value.trim()}; path=/; SameSite=Lax; Secure`;

                // Show the admin modal
                popupModal.style.display = "flex";
            } else {
                // Show error if credentials are invalid
                username.classList.add("error");
                password.classList.add("error");
                usernameError.textContent = "Access denied: Invalid admin credentials.";
                usernameError.style.display = "block";
            }

        } catch (err) {
            // Show error if the request fails
            console.error("Error verifying admin: ", err);
            username.classList.add("error");
            password.classList.add("error");
            usernameError.textContent = "An unexpected error occurred. Please try again.";
            usernameError.style.display = "block";
        } finally {
            // Re-enable the button and reset spinner
            loginBtn.disabled = false;
            spinner.style.display = "none";
            buttonText.style.display = "inline";
        }
    });
});