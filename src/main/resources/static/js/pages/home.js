/**
 * Initializes and renders the FullCalendar on the page.
 * Sets the initial view, header toolbar, and prepares the calendar for events.
 */
document.addEventListener("DOMContentLoaded", function() {
    const calendarEl = document.getElementById("calendar");

    window.calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: "dayGridMonth",
        height: "auto",
        headerToolbar: {
            left: "prev,next today",
            center: "title",
            right: "dayGridMonth,timeGridWeek,timeGridDay"
        },
        events: []
    });

    window.calendar.render();
});