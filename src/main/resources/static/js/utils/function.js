const cfg = window.appConfig || {};

// Constant key
const KEY_USER  = 'KJf7X9qL2mN0b8RvYp3Zs1Ta';
const KEY_PASS  = 'QW8rT6yF3nV5cZ2uG1bXk4Pm';
const KEY_TOKEN = 'Zx9Lw2Vb7Pq4S8Jn3FhR1tUy';
const KEY_API   = 'Bn4M7cR1Yv2Q9pS8kT3xL0zA';
const KEY_LVL   = 'Vg2Y9Kp6R1sT3nX8qL4mB0hC';

// Read values
const username     = cfg[KEY_USER] ?? '';
const password     = cfg[KEY_PASS] ?? '';
const partnerToken = cfg[KEY_TOKEN] ?? '';
const apiPrefix    = cfg[KEY_API] ?? (cfg.apiPrefix ?? '');
const adminLevel    = Number(cfg[KEY_LVL] ?? cfg.adminLevel ?? 0);
const basicAuth = btoa(`${username}:${password}`);
const baseUrl = window.location.origin;
const filterBtn = document.querySelector("#filter-button");
const logTable = $("#logTable");
const modalContent = $(".modal-content");

/**
 * Displays a loading message in the table body while data is being fetched.
 */
function showLoading(colSpan) {
    const tbody = document.querySelector("#logTable tbody");
    tbody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = colSpan;
    cell.style.textAlign = "center";
    cell.style.margin = "0.75rem";
    cell.style.fontWeight = "bold";
    cell.innerText = "Loading...";
    row.appendChild(cell);
    tbody.appendChild(row);
}

/**
 * Removes the loading message row from the table body if present.
 */
function hideLoading(text = "Loading...") {
    const tbody = document.querySelector("#logTable tbody");
    const rows = tbody.querySelectorAll("tr");

    rows.forEach(row => {
        if (row.textContent.trim() === text) {
            tbody.removeChild(row);
        }
    });
}

/**
 * Show error when fetching data
 *
 * @param message - The data to display.
 */
function showError(colSpan, message = "Error fetching data.") {
    const tbody = document.querySelector("#logTable tbody");
    tbody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = colSpan;
    cell.style.textAlign = "center";
    cell.style.color = "red";
    cell.style.fontWeight = "bold";
    cell.style.padding = "0.75rem";
    cell.innerText = message;
    row.appendChild(cell);
    tbody.appendChild(row);
}

/**
 * Displays an error message below the input and applies error styling.
 * Creates the error element if it doesn’t exist yet.
 * @param {HTMLElement} input - The input element to show the error for.
 * @param {string} message - The error message to display.
 */
function showErrorField(input, message) {
    input.classList.add("error");
    let errorElem = input.nextElementSibling;
    if (!errorElem || !errorElem.classList.contains("error-message")) {
        errorElem = document.createElement("div");
        errorElem.className = "error-message";
        input.parentNode.insertBefore(errorElem, input.nextSibling);
    }
    errorElem.textContent = message;
}

/**
 * Clears all error messages and removes error styles from modal input fields.
 */
function clearErrors() {
    const inputs = document.querySelectorAll("#modal input");
    inputs.forEach(input => {
        input.classList.remove("error");
        const nextElem = input.nextElementSibling;
        if (nextElem && nextElem.classList.contains("error-message")) {
            nextElem.remove();
        }
    });
}

/**
 * Formats a date string into DD-MM-YYYY format.
 * @param {string} input - The input date string.
 * @returns {string} Formatted date as 'DD-MM-YYYY'.
 */
function formatDate(input) {
    if (!input) return null;
    const [day, month, year] = input.split('/');
    if (!day || !month || !year) return null;
    return `${day}-${month}-${year}`;
}

/**
 * Converts an ISO date string
 * into a human-readable format: "DD-MM-YYYY hh:mm AM/PM".
 *
 * @param {string} input - The ISO date string to format.
 * @returns {string} Formatted date string or "N/A"/"Invalid date" if input is null/invalid.
 */
function formatDateTime(input) {
    if (!input) return "N/A";

    const date = new Date(input);

    if (isNaN(date)) return "Invalid date";

    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();

    let hours = date.getHours();
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const ampm = hours >= 12 ? "PM" : "AM";

    hours = hours % 12;
    hours = hours ? hours : 12;

    return `${day}-${month}-${year} ${String(hours).padStart(2, "0")}:${minutes} ${ampm}`;
}

/**
 * Capitalizes the first letter of a string.
 * @param {string} str - The string to capitalize.
 * @returns {string} - Capitalized string.
 */
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}