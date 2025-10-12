/**
 * Deletes a browser cookie by setting its Max-Age to 0.
 * The cookie is removed from the root path with SameSite set to Lax.
 */
function deleteCookie(name) {
    document.cookie = `${name}=; Max-Age=0; path=/; SameSite=Lax;`;
}

document.addEventListener("DOMContentLoaded", () => {
    const logoutBtn = document.getElementById("logout");
    const sidebarToggleBtn = document.getElementById("sidebarToggle");
    let logDatatable = $("#logTable");
    let modalContent = $(".modal-content");
    const calendarEl = document.getElementById("calendar");

    deleteCookie("isAdmin");
    deleteCookie("adminUsername");
    deleteCookie("adminPassword");

    // Logout
    logoutBtn.addEventListener("click", function () {
        window.location.href = this.dataset.redirectUrl;
    });

    // Collapse sidebar
    sidebarToggleBtn.addEventListener("click", function() {
        const leftSidebar = document.getElementById("sidebar");

        leftSidebar.addEventListener("transitionend", function handler(e) {
            if (e.propertyName === "width") {
                // Adjust DataTable columns
                logDatatable.DataTable().columns.adjust().draw();

                // Resize FullCalendar if exists
                if (window.calendar) {
                    window.calendar.updateSize();
                }

                leftSidebar.removeEventListener("transitionend", handler);
            }
        });
    });

    modalContent.draggable({
        cursor: "move"
    });
});

const notificationIconContainer = document.getElementById("notification-icon");
const notificationIconSVG = notificationIconContainer.querySelector("svg");
const notificationModal = document.getElementById("notification-modal");
const closeNotificationModalBtn = document.getElementById("close-notification-modal");

/**
 * Toggles the visibility of the notification modal.
 * When active, the modal is shown and accessibility attributes are updated accordingly.
 * When inactive, the modal is hidden and accessibility attributes reflect the hidden state.
 * Focus is moved to the modal when it is opened for better keyboard navigation.
 */
function toggleNotificationModal() {
    const isActive = notificationModal.classList.toggle("active");

    notificationModal.setAttribute("aria-hidden", isActive ? "false" : "true");
    notificationIconContainer.setAttribute("aria-expanded", isActive ? "true" : "false");

    if (isActive) {
        notificationModal.inert = false;
        notificationModal.focus();
    } else {
        notificationModal.inert = true;
        notificationIconContainer.focus();
    }
}

// Toggle modal when clicking on notification icon container
notificationIconContainer.addEventListener("click", toggleNotificationModal);

// Toggle modal when clicking directly on the SVG, and stop event bubbling
notificationIconSVG.addEventListener("click", (e) => {
    e.stopPropagation();
    toggleNotificationModal();
});

// Close modal when clicking the close button and reset accessibility states
closeNotificationModalBtn.addEventListener("click", () => {
    closeNotificationModalBtn.blur();
    notificationIconContainer.focus();
    notificationModal.classList.remove("active");
    notificationModal.setAttribute("aria-hidden", "true");
    notificationIconContainer.setAttribute("aria-expanded", "false");
});

// Close modal when clicking outside the modal and icon container
document.addEventListener("click", (e) => {
    if (!notificationModal.contains(e.target) && !notificationIconContainer.contains(e.target)) {
        notificationModal.classList.remove("active");
        notificationModal.setAttribute("aria-hidden", "true");
        notificationIconContainer.setAttribute("aria-expanded", "false");
    }
});