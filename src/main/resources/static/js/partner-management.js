// Retrieve credentials and tokens from DOM dataset attributes or default to empty string
const username = document.querySelector("[data-username]")?.dataset.username || "";
const password = document.querySelector("[data-password]")?.dataset.password || "";
const partnerToken = document.querySelector("[data-partner-token]")?.dataset.partnerToken || "";
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;

// Encode Basic Auth credentials in base64
const basicAuth = btoa(`${username}:${password}`);

// Base URL for API calls
const baseUrl = window.location.origin;
const url = `${baseUrl}${apiPrefix}/list-partner`;

// Elements references
const filterBtn = document.querySelector(".filter-button");
let fullData = [];
let modalContent = $(".modal-content");

// Initialize jQuery UI draggable for modal and flatpickr datepicker
$(() => {
    modalContent.draggable({
        cursor: "move"
    });
});

/**
 * Display a loading indicator in the table while fetching data.
 * Clears existing table body and shows a single row with "Loading..." message.
 */
function showLoading() {
    const tbody = document.querySelector("#partnerTable tbody");
    tbody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 8;
    cell.style.textAlign = "center";
    cell.style.margin = "0.75rem";
    cell.style.fontWeight = "bold";
    cell.innerText = "Loading...";
    row.appendChild(cell);
    tbody.appendChild(row);
}

/**
 * Remove the "Loading..." row from the table body after data has loaded.
 */
function hideLoading() {
    const tbody = document.querySelector("#partnerTable tbody");
    const rows = tbody.querySelectorAll("tr");

    rows.forEach(row => {
        if (row.textContent.trim() === "Loading...") {
            tbody.removeChild(row);
        }
    });
}

/**
 * Fetch data from the API with optional filters, then render the data table.
 * Shows loading indicator during fetch and handles errors gracefully.
 */
async function fetchData() {
    const tbody = document.querySelector("#partnerTable tbody");
    tbody.innerHTML = "";
    showLoading();

    // Collect filter values from input fields
    const name = document.getElementById("name").value;
    const identifier = document.getElementById("identifier").value;
    const systemCode = document.getElementById("systemCode").value;
    const description = document.getElementById("description").value;

    // Prepare query parameters based on filled filters
    const params = new URLSearchParams();
    if (name) params.append("name", name);
    if (identifier) params.append("identifier", identifier);
    if (systemCode) params.append("systemCode", systemCode);
    if (description) params.append("description", description);

    try {
        const response = await fetch(`${url}?${params.toString()}`, {
            headers: {
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const responseData = await response.json();
        // Normalize data array for rendering
        fullData = Array.isArray(responseData?.data) ? responseData.data : (responseData?.data?.content || []);
        renderTable();

    } catch (err) {
        console.error("Fetch failed:", err);
        tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; color: red;">Error fetching data.</td></tr>`;
    } finally {
        hideLoading();
    }
}

/**
 * Convert date from "dd/mm/yyyy" format to "dd-mm-yyyy" for API parameter.
 * @param {string} input - Date string in dd/mm/yyyy format
 * @returns {string} - Reformatted date string
 */
function formatDate(input) {
    const [day, month, year] = input.split('/');
    return `${day}-${month}-${year}`;
}

/**
 * Render the data table using DataTables jQuery plugin.
 * Reinitializes DataTable if already initialized.
 */
function renderTable() {
    let table = $("#partnerTable");

    if ($.fn.DataTable.isDataTable("#partnerTable")) {
        table.DataTable().destroy();
    }

    table.DataTable({
        data: fullData.map((item, i) => [
            i + 1,
            item.name,
            item.identifier,
            item.systemCode,
            item.isBank === true ? "✔️" : "❌",
            item.description,
            item.code,
            `<span class="copy-key-btn" data-key="${item.public_key}">
                <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" viewBox="0 0 24 24" fill="none">
                    <path d="M6 11C6 8.17157 6 6.75736 6.87868 5.87868C7.75736 5 9.17157 5 12 5H15C17.8284 5 19.2426 5 20.1213 5.87868C21 6.75736 21 8.17157 21 11V16C21 18.8284 21 20.2426 20.1213 21.1213C19.2426 22 17.8284 22 15 22H12C9.17157 22 7.75736 22 6.87868 21.1213C6 20.2426 6 18.8284 6 16V11Z" stroke="#1A73E8" stroke-width="1.5"/>
                    <path opacity="1.5" d="M6 19C4.34315 19 3 17.6569 3 16V10C3 6.22876 3 4.34315 4.17157 3.17157C5.34315 2 7.22876 2 11 2H15C16.6569 2 18 3.34315 18 5" stroke="#1A73E8" stroke-width="1.5"/>
                </svg>
            </span>`,
            `<span class="view-link" data-index='${i}'>
                <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" viewBox="0 0 24 24" fill="none">
                    <path d="M21.2799 6.40005L11.7399 15.94C10.7899 16.89 7.96987 17.33 7.33987 16.7C6.70987 16.07 7.13987 13.25 8.08987 12.3L17.6399 2.75002C17.8754 2.49308 18.1605 2.28654 18.4781 2.14284C18.7956 1.99914 19.139 1.92124 19.4875 1.9139C19.8359 1.90657 20.1823 1.96991 20.5056 2.10012C20.8289 2.23033 21.1225 2.42473 21.3686 2.67153C21.6147 2.91833 21.8083 3.21243 21.9376 3.53609C22.0669 3.85976 22.1294 4.20626 22.1211 4.55471C22.1128 4.90316 22.0339 5.24635 21.8894 5.5635C21.7448 5.88065 21.5375 6.16524 21.2799 6.40005V6.40005Z" stroke="#1A73E8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M11 4H6C4.93913 4 3.92178 4.42142 3.17163 5.17157C2.42149 5.92172 2 6.93913 2 8V18C2 19.0609 2.42149 20.0783 3.17163 20.8284C3.92178 21.5786 4.93913 22 6 22H17C19.21 22 20 20.2 20 18V13" stroke="#1A73E8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </span>`
        ]),
        columns: [
            { title: "#" },
            { title: "Name" },
            { title: "Identifier" },
            { title: "System Code" },
            { title: "Bank" },
            { title: "Description" },
            { title: "Secret" },
            { title: "Key" },
            { title: "Action" }
        ],
        pageLength: 10,
        lengthMenu: [10, 25, 50, 100],
        scrollX: true,
        destroy: true
    });

    // Attach click event for copy buttons
    $(".copy-key-btn").click(function () {
        const key = $(this).data("key");
        navigator.clipboard.writeText(key)
            .then(() => alert("Copied to clipboard!"))
            .catch(err => console.error("Copy failed", err));
    });
}

// Variable to track the currently edited item ID
let currentEditId = null;

/**
 * Opens the edit modal and fills it with data of the selected item.
 * @param {object} item - Data item to edit
 */
function openEditModal(item) {
    currentEditId = item.id;

    document.getElementById("modalName").value = item.name || "";
    document.getElementById("modalIdentifier").value = item.identifier || "";
    document.getElementById("modalSystemCode").value = item.systemCode || "";
    document.getElementById("modalDescription").value = item.description || "";
    document.getElementById("modalCode").value = item.code || "";

    document.getElementById("modal").style.display = "flex";  // Show modal
}

/**
 * Closes the edit modal and resets the current edit ID.
 */
function closeModal() {
    document.getElementById("modal").style.display = "none";
    currentEditId = null;
    clearErrors();
}

// Attach event listeners to close buttons of modal
document.getElementById("modalClose").addEventListener("click", closeModal);
document.getElementById("cancelModal").addEventListener("click", closeModal);

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
 * Displays an error message below the input and applies error styling.
 * Creates the error element if it doesn’t exist yet.
 * @param {HTMLElement} input - The input element to show the error for.
 * @param {string} message - The error message to display.
 */
function showError(input, message) {
    input.classList.add("error");
    let errorElem = input.nextElementSibling;
    if (!errorElem || !errorElem.classList.contains("error-message")) {
        errorElem = document.createElement("div");
        errorElem.className = "error-message text-red-600 text-sm";
        input.parentNode.insertBefore(errorElem, input.nextSibling);
    }
    errorElem.textContent = message;
}

/**
 * Validates required fields in the modal form.
 * Clears previous errors, then validates 'name', 'identifier', 'systemCode', 'code', and 'description'.
 * Shows errors if fields are empty or API returns errors.
 * @param {Object} apiErrors - Optional API error messages keyed by field names.
 * @returns {boolean} - True if valid, false otherwise.
 */
function validateModalFields(apiErrors = {}) {
    const fields = ["name", "identifier", "systemCode", "code", "description"];
    let isValid = true;

    fields.forEach(field => {
        const errorEl = document.getElementById(`error${capitalize(field)}`);
        if (errorEl) errorEl.textContent = ''; // Clear previous error

        if (apiErrors[field]) {
            if (errorEl) errorEl.textContent = apiErrors[field];
            isValid = false;
        }
    });

    return isValid;
}

/**
 * Capitalizes the first letter of a string.
 * @param {string} str - The string to capitalize.
 * @returns {string} - Capitalized string.
 */
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * Handles the partner edit form submission.
 * Prevents default submit, validates input, sends update request,
 * handles API errors, and refreshes data on success.
 */
// Handle partner edit form submission
document.getElementById("editPartnerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!currentEditId) {
        alert("No partner selected for editing.");
        return;
    }

    // Collect modal input data
    const modalData = {
        name: document.getElementById("modalName").value.trim(),
        identifier: document.getElementById("modalIdentifier").value.trim(),
        systemCode: document.getElementById("modalSystemCode").value.trim(),
        description: document.getElementById("modalDescription").value.trim(),
        code: document.getElementById("modalCode").value.trim()
    };

    // Simple front-end validation
    let hasError = false;
    Object.keys(modalData).forEach(field => {
        const inputEl = document.getElementById(`modal${capitalize(field)}`);
        if (!modalData[field]) {
            showError(inputEl, `${capitalize(field)} is required`);
            hasError = true;
        }
    });
    if (hasError) return;

    try {
        const response = await fetch(`${apiPrefix}/update-partner/${currentEditId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${basicAuth}`,
                "X-Partner-Token": partnerToken
            },
            body: JSON.stringify(modalData)
        });

        const result = await response.json();

        if (!response.ok) {
            // Handle API validation errors
            if (result.errors) {
                validateModalFields(result.errors);
            } else {
                alert(result.message || "Failed to update partner.");
            }
            return;
        }

        closeModal();
        fetchData();

    } catch (err) {
        console.error("Update failed:", err);
        alert("Failed to update partner. See console for details.");
    }
});

// Attach event listener to view icons inside the table to open modal with data for editing
$("#partnerTable tbody").on("click", ".view-link", function () {
    const index = $(this).data("index");
    const item = fullData[index];
    console.clear();
    console.log(item);
    openEditModal(item);
});

// Filter button triggers data fetch with current filter inputs
filterBtn.addEventListener("click", () => {
    fetchData();
});

// Logout button clears cookies and redirects to login page
document.querySelector(".btn-logout")?.addEventListener("click", () => {
    const deleteCookie = name => {
        document.cookie = `${name}=; Max-Age=0; path=/; SameSite=Lax;`;
    };

    deleteCookie("isAdmin");
    deleteCookie("adminUsername");
    deleteCookie("adminPassword");

    window.location.href = `${apiPrefix}/admin/login`;
});

// Initial data fetch on page load
fetchData();