/**
 * Displays a SweetAlert2 toast notification.
 *
 * @param {string} type - The type of toast ("success", "error", "info", "warning").
 * @param {string} message - The message to display in the toast.
 *
 * This function uses predefined background and text colors
 * to keep notifications consistent with the design system.
 * The toast appears at the top-end and automatically disappears after 3 seconds.
 * Hovering over the toast pauses the dismissal timer.
 */
function showToast(type = "success", message = "Operation successful.") {
    // Define toast background and text colors based on the alert type
    const toastStyles = {
        success: {
            background: "#DFF5E3",
            color: "#2E7D32"
        },
        error: {
            background: "#FDECEA",
            color: "#C62828"
        },
        info: {
            background: "#E8F0FE",
            color: "#1A73E8"
        },
        warning: {
            background: "#FFF4E5",
            color: "#C27803"
        }
    };

    // Fallback to 'success' styles if an unknown type is provided
    const { background, color } = toastStyles[type] || toastStyles.success;

    Swal.fire({
        toast: true,
        position: "top-end",
        icon: type,
        title: message,
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        background,
        color,
        didOpen: (toast) => {
            toast.addEventListener("mouseenter", Swal.stopTimer);
            toast.addEventListener("mouseleave", Swal.resumeTimer);
        }
    });
}

// Usage example:
// showToast("success", "Message");