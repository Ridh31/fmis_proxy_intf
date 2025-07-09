// Retrieve credentials and tokens from DOM dataset attributes or default to empty string
const username = document.querySelector('[data-username]')?.dataset.username || "";
const password = document.querySelector('[data-password]')?.dataset.password || "";
const partnerToken = document.querySelector('[data-partner-token]')?.dataset.partnerToken || "";

// Encode Basic Auth credentials in base64
const basicAuth = btoa(`${username}:${password}`);

// Base URL for API calls
const baseUrl = window.location.origin;
const url = `${baseUrl}/api/v1/internal/camdigikey/list-host`;

// Elements references
const filterBtn = document.querySelector(".filter-button");
let fullData = [];
let modalContent = $(".modal-content");

// Initialize jQuery UI draggable for modal and flatpickr datepicker
$(() => {
    modalContent.draggable({
        handle: ".modal-header",
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
    const tbody = document.querySelector("#internalCamDigiKeyTable tbody");
    tbody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 7;
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
    const tbody = document.querySelector("#internalCamDigiKeyTable tbody");
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
    const tbody = document.querySelector("#internalCamDigiKeyTable tbody");
    tbody.innerHTML = "";
    showLoading();

    // Collect filter values from input fields
    const name = document.getElementById("name").value;
    const appKey = document.getElementById("appKey").value;
    const ipAddress = document.getElementById("ipAddress").value;
    const accessURL = document.getElementById("accessURL").value;
    const createdDate = document.getElementById("createdDate").value;

    // Prepare query parameters based on filled filters
    const params = new URLSearchParams();
    if (name) params.append("name", name);
    if (appKey) params.append("appKey", appKey);
    if (ipAddress) params.append("ipAddress", ipAddress);
    if (accessURL) params.append("accessURL", accessURL);
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
        tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: red;">Error fetching data.</td></tr>`;
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
    let table = $('#internalCamDigiKeyTable');

    if ($.fn.DataTable.isDataTable('#internalCamDigiKeyTable')) {
        table.DataTable().destroy();
    }

    table.DataTable({
        data: fullData.map((item, i) => [
            i + 1,
            item.name,
            item.appKey,
            item.ipAddress,
            item.accessURL,
            item.createdDate || 'N/A',
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
            { title: "App Key" },
            { title: "IP Address" },
            { title: "Access URL" },
            { title: "Created Date" },
            { title: "Action" }
        ],
        pageLength: 10,
        lengthMenu: [10, 25, 50, 100],
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
    document.getElementById("modalAppKey").value = item.appKey || "";
    document.getElementById("modalIP").value = item.ipAddress || "";
    document.getElementById("modalURL").value = item.accessURL || "";

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
 * Validates modal input fields and displays error messages if invalid.
 * @param {object} data - Form data to validate
 * @returns {boolean} - True if all fields are valid, otherwise false
 */
function validateModalFields(data) {
    let valid = true;
    clearErrors();

    if (!data.name.trim()) {
        const input = document.getElementById("modalName");
        showError(input, "Name is required. Please provide a valid name.");
        valid = false;
    }
    if (!data.appKey.trim()) {
        const input = document.getElementById("modalAppKey");
        showError(input, "App key is required. Please provide a valid app key.");
        valid = false;
    }
    if (!data.ipAddress.trim()) {
        const input = document.getElementById("modalIP");
        showError(input, "IP address is required. Please provide a valid IP address.");
        valid = false;
    }
    if (!validateURL(data.accessURL.trim())) {
        const input = document.getElementById("modalURL");
        showError(input, "Access URL is required. Please provide a valid access URL.");
        valid = false;
    }

    return valid;
}

/**
 * Validates a URL by attempting to create a URL object.
 * @param {string} url - URL string to validate
 * @returns {boolean} - True if valid URL, otherwise false
 */
function validateURL(url) {
    try {
        new URL(url);
        return true;
    } catch {
        return false;
    }
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

// Handle form submission for editing host data
document.getElementById("editHostForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const updatedData = {
        name: document.getElementById("modalName").value.trim(),
        appKey: document.getElementById("modalAppKey").value.trim(),
        ipAddress: document.getElementById("modalIP").value.trim(),
        accessURL: document.getElementById("modalURL").value.trim()
    };

    clearErrors();

    if (!currentEditId) return alert("Missing ID for update.");

    // Client-side validation: stop submission if invalid
    if (!validateModalFields(updatedData)) return;

    try {
        const res = await fetch(`/api/v1/internal/camdigikey/update-host/${currentEditId}`, {
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
                if (errData.message && typeof errData.message === "string") {
                    const message = errData.message.toLowerCase();

                    if (message.includes("name") && !errData.error) {
                        const input = document.getElementById("modalName");
                        showError(input, errData.message);
                    } else if (message.includes("app key")) {
                        const input = document.getElementById("modalAppKey");
                        showError(input, errData.message);
                    } else if (message.includes("ip address")) {
                        const input = document.getElementById("modalIP");
                        showError(input, errData.message);
                    } else if (message.includes("access url")) {
                        const input = document.getElementById("modalURL");
                        showError(input, errData.message);
                    } else {
                        alert(errData.message);
                    }
                }

                if (errData.error && typeof errData.error === "object") {
                    Object.entries(errData.error).forEach(([field, msg]) => {
                        const input = document.getElementById(`modal${capitalize(field)}`);
                        if (input) {
                            showError(input, msg);
                        }
                    });
                }
            } else {
            }
            return;
        }

        closeModal();
        fetchData();

    } catch (err) {
        console.error(err);
    }
});

// Attach event listener to view icons inside the table to open modal with data for editing
$("#internalCamDigiKeyTable tbody").on("click", ".view-link", function () {
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

    window.location.href = "/api/v1/admin/login";
});

// Initial data fetch on page load
fetchData();