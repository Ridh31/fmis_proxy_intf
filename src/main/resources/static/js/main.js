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
        console.log(this.dataset.redirectUrl);
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