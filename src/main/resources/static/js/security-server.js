// Retrieve credentials and tokens from DOM dataset attributes or default to empty string
const username = document.querySelector("[data-username]")?.dataset.username || "";
const password = document.querySelector("[data-password]")?.dataset.password || "";
const partnerToken = document.querySelector("[data-partner-token]")?.dataset.partnerToken || "";
const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;

// Encode Basic Auth credentials in base64
const basicAuth = btoa(`${username}:${password}`);

// Base URL for API calls
const baseUrl = window.location.origin;
const url = `${baseUrl}${apiPrefix}/security-server/list-server`;

// Elements references
const filterBtn = document.querySelector(".filter-button");
let fullData = [];
let modalContent = $(".modal-content");

// Initialize jQuery UI draggable for modal and flatpickr datepicker
$(() => {
    modalContent.draggable({
        cursor: "move"
    });

    flatpickr("#createdDate", {
        dateFormat: "d/m/Y",
        allowInput: true
    });
});

/**
 * Display a loading indicator in the table while fetching data.
 * Clears existing table body and shows a single row with "Loading..." message.
 */
function showLoading() {
    const tbody = document.querySelector("#securityServerTable tbody");
    tbody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 11;
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
    const tbody = document.querySelector("#securityServerTable tbody");
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
    const tbody = document.querySelector("#securityServerTable tbody");
    tbody.innerHTML = "";
    showLoading();

    // Collect filter values from input fields
    const name = document.getElementById("name").value;
    const configKey = document.getElementById("configKey").value;
    const description = document.getElementById("description").value;
    const createdDate = document.getElementById("createdDate").value;

    // Prepare query parameters based on filled filters
    const params = new URLSearchParams();
    if (name) params.append("name", name);
    if (configKey) params.append("configKey", configKey);
    if (description) params.append("description", description);
    if (createdDate) params.append("createdDate", formatDate(createdDate));

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
        tbody.innerHTML = `<tr><td colspan="11" style="text-align: center; color: red;">Error fetching data.</td></tr>`;
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
    let table = $("#securityServerTable");

    if ($.fn.DataTable.isDataTable("#securityServerTable")) {
        table.DataTable().destroy();
    }

    table.DataTable({
        data: fullData.map((item, i) => [
            i + 1,
            item.name,
            item.configKey,
            item.baseURL,
            item.endpoint,
            item.subsystem,
            item.username,
            item.password,
            item.contentType,
            item.description,
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
            { title: "Config Key" },
            { title: "Base URL" },
            { title: "Endpoint" },
            { title: "Subsystem" },
            { title: "Username" },
            { title: "Password" },
            { title: "Content Type" },
            { title: "Description" },
            { title: "Action" }
        ],
        pageLength: 10,
        lengthMenu: [10, 25, 50, 100],
        scrollX: true,
        destroy: true
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
    document.getElementById("modalConfigKey").value = item.configKey || "";
    document.getElementById("modalBaseURL").value = item.baseURL || "";
    document.getElementById("modalEndpoint").value = item.endpoint || "";
    document.getElementById("modalSubsystem").value = item.subSystem || "";
    document.getElementById("modalUsername").value = item.username || "";
    document.getElementById("modalPassword").value = item.password || "";
    document.getElementById("modalContentType").value = item.contentType || "";
    document.getElementById("modalDescription").value = item.description || "";

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
 * Clears previous errors, then validates 'name' and 'configKey'.
 * Shows errors if fields are empty.
 * @param {Object} data - Form data to validate.
 * @returns {boolean} - True if valid, false otherwise.
 */
function validateModalFields(data) {
    let valid = true;
    clearErrors();

    if (!data.name.trim()) {
        const input = document.getElementById("modalName");
        showError(input, "Name is required. Please provide a valid name.");
        valid = false;
    }

    if (!data.configKey.trim()) {
        const input = document.getElementById("modalConfigKey");
        showError(input, "Config key is required. Please provide a valid config key.");
        valid = false;
    }

    return valid;
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
 * Handles the server edit form submission.
 * Prevents default submit, validates input, sends update request,
 * handles API errors, and refreshes data on success.
 */
document.getElementById("editServerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const updatedData = {
        name: document.getElementById("modalName").value.trim(),
        configKey: document.getElementById("modalConfigKey").value.trim(),
        baseURL: document.getElementById("modalBaseURL").value.trim(),
        endpoint: document.getElementById("modalEndpoint").value.trim(),
        subSystem: document.getElementById("modalSubsystem").value.trim(),
        username: document.getElementById("modalUsername").value.trim(),
        password: document.getElementById("modalPassword").value.trim(),
        contentType: document.getElementById("modalContentType").value.trim(),
        description: document.getElementById("modalDescription").value.trim()
    };

    clearErrors();

    if (!currentEditId) {
        alert("Missing ID for update.");
        return;
    }

    if (!validateModalFields(updatedData)) return;

    try {
        const res = await fetch(`${apiPrefix}/security-server/update-server/${currentEditId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${basicAuth}`
            },
            body: JSON.stringify(updatedData)
        });

        if (!res.ok) {
            const errData = await res.json().catch(() => null);

            if (errData) {
                // Handle string-based error message
                if (typeof errData.message === "string") {
                    const message = errData.message.toLowerCase();

                    if (message.includes("name")) {
                        const input = document.getElementById("modalName");
                        showError(input, errData.message);
                    } else if (message.includes("config key")) {
                        const input = document.getElementById("modalConfigKey");
                        showError(input, errData.message);
                    } else {
                        alert(errData.message);
                    }
                }

                // Handle field-based validation errors
                if (typeof errData.error === "object") {
                    Object.entries(errData.error).forEach(([field, msg]) => {
                        const input = document.getElementById(`modal${capitalize(field)}`);
                        if (input) {
                            showError(input, msg);
                        }
                    });
                }
            } else {
                alert("An unknown error occurred.");
            }

            return;
        }

        closeModal();
        fetchData();

    } catch (err) {
        console.error("Update failed:", err);
        alert("Failed to update server. Please try again.");
    }
});

// Attach event listener to view icons inside the table to open modal with data for editing
$("#securityServerTable tbody").on("click", ".view-link", function () {
    const index = $(this).data("index");
    const item = fullData[index];
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