const apiPrefix = document.querySelector("#api-data")?.dataset.apiPrefix;
const config = {
    url: {
        baseUrl: window.location.origin,
        specUrl: `${window.location.origin}${apiPrefix}/open-api`,
        docsUrl: `${window.location.origin}${apiPrefix}/docs`,
        overviewUrl: `${window.location.origin}${apiPrefix}/docs#tag/Overview`
    },
    color: {
        bgActive: "#E1E1E1",
        bgInactive: "#FAFAFA",
        textActive: "#007BFF",
        textInactive: "#333"
    }
};

/**
 * Toggles the visibility of ReDoc's right panel and manages API content sections.
 * @param {HTMLElement} wrapper - The main ReDoc wrapper element.
 * @param {HTMLElement} content - The API documentation container.
 * @param {string} visibility - The display style to apply (e.g., "none", "").
 * @param {HTMLElement} [clickedItem=null] - The sidebar item that was clicked.
 */
function manipulateReDoc(wrapper, content, visibility, clickedItem = null) {
    if (wrapper) {
        let rightPanel = wrapper.lastElementChild;
        if (rightPanel) rightPanel.style.display = visibility;
    }

    if (content) {
        if (clickedItem) {
            // Show only the selected API content section
            Array.from(content.children).forEach(child => {
                child.style.display = (child.id === clickedItem.dataset.tagId) ?
                    (child.style.display === visibility ? "" : "none") : "";
            });
        } else {
            // Default to showing only the "Overview" section
            Array.from(content.children).forEach(child => {
                child.style.display = (child.id !== "tag/Overview") ? "none" : "";
            });
        }
    }
}

/**
 * Removes a sidebar menu item with the specified text content.
 * @param {HTMLElement} sideBar - The sidebar container.
 * @param {string} text - The text of the sidebar item to remove.
 */
function removeSidebarItemByText(sideBar, text) {
    if (sideBar) {
        let items = sideBar.querySelectorAll("li");
        items.forEach(item => {
            if (item.textContent.trim() === text) item.remove();
        });
    }
}

/**
 * Updates the "Overview" menu item appearance based on its active state.
 * @param {HTMLElement} overview - The "Overview" menu item.
 * @param {HTMLElement} overviewContent - The corresponding API content section.
 */
function activeOverview(overview, overviewContent) {
    if (overview) {
        let label = overview.querySelector("label");
        let span = overview.querySelector("span");
        let isOverviewActive = overviewContent.getAttribute("data-visible");

        if (label) {
            // Toggle appearance based on visibility state
            label.style.backgroundColor = isOverviewActive === "1" ? config.color.bgActive : config.color.bgInactive;
            span.style.color = isOverviewActive === "1" ? config.color.textActive : config.color.textInactive;

            // Show or hide the Overview content based on active state
            overviewContent.style.display = isOverviewActive === "1" ? "" : "none";
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const baseUrl = config.url.baseUrl;
    const specUrl = config.url.specUrl;

    // Initialize ReDoc with custom configurations
    Redoc.init(specUrl, {
        scrollYOffset: 50,
        hideDownloadButton: true,
        theme: {
            typography: {
                fontSize: "14px",
                fontFamily: "Arial, sans-serif"
            },
            colors: {
                primary: { main: config.color.textActive },
                text: { primary: config.color.textInactive }
            }
        },
    }, document.getElementById("redoc-container"));

    // Wait for ReDoc to load using MutationObserver
    const observer = new MutationObserver((mutations, obs) => {
        let redocWrap = document.querySelector("div.redoc-wrap");
        let searchRole = document.querySelector("div[role=search]");
        let sideBar = document.querySelector("ul[role=menu]");
        let apiContent = document.querySelector("div.api-content");

        // Define custom "Overview" navigation item
        const overviewNav = `
            <div id="overview" tabindex="0" data-item-id="tag/Overview" role="menuitem" class="sc-bpUBKd itmOnH">
                <label class="sc-eyvILC gszkfq -depth1">
                    <span width="calc(100% - 38px)" title="Overview" class="sc-gfoqjT ijqoQq">Overview</span>
                </label>
            </div>
        `;

        if (apiContent) {
            obs.disconnect();

            removeSidebarItemByText(sideBar, "Overview");
            searchRole.innerHTML = overviewNav;

            let overview = document.getElementById("overview");
            let overviewContent = document.getElementById("tag/Overview");
            overviewContent.setAttribute("data-visible", "1");
            overviewContent.style.padding = "0";

            let apiData = document.getElementById("api-data");
            let apiTitle = apiData.getAttribute("data-api-title");
            let apiDescription = apiData.getAttribute("data-api-description");
            let apiVersion = apiData.getAttribute("data-api-version");
            let apiBaseUrl = apiData.getAttribute("data-api-base-url");

            overviewContent.innerHTML = `
                <div id="content">
                    <header>
                        <div class="api-title">
                            <h1>${apiTitle ?? ""}</h1>
                            <div class="api-version-container">
                                <div class="api-version">v${apiVersion ?? ""}</div> 
                            </div>
                        </div>
                        <div class="api-box">
                            <div class="api-box-title">API Base URL</div>
                            <div class="api-box-url">
                                <div>Live Server: <span>${apiBaseUrl ?? ""}</span></div>
                            </div>
                        </div>
                    </header>
                    
                    <section id="introduction">
                        <h2>1. Introduction</h2>
                        <p>
                            The <strong>FMIS Proxy Interface API</strong> serves as a bridge between external systems and the FMIS platform, enabling seamless and secure communication. It provides a well-structured set of endpoints that allow developers to test connectivity, manage test entities, and interact with FMIS data in a standardized manner.
                        </p>    
                        <p>
                            Designed for <strong>efficiency, scalability, and security</strong>, this API ensures smooth integration by abstracting complex interactions, reducing manual intervention, and streamlining data exchange processes. It supports various authentication mechanisms, enforces strict access controls, and facilitates robust logging and monitoring to enhance reliability.
                        </p>
                        <p>
                            By acting as an <strong>intermediary layer</strong>, the FMIS Proxy Interface API simplifies interactions with FMIS, allowing organizations to integrate their financial management workflows, automate transactions, and maintain data integrity. Whether for real-time data retrieval, batch processing, or system-to-system communication, this API is designed to meet the evolving needs of modern financial systems while ensuring compliance with industry standards.
                        </p>
                    </section>
                
                    <section id="api-environment">
                        <h2>2. API Environment</h2>
                        <p><strong>Request Base URL:</strong></p>
                        <pre><code>${apiBaseUrl ?? ""}${apiPrefix}/endpoint</code></pre>
                        <p><strong>Request Headers:</strong></p>
                        <pre><code>X-Partner-Token: PARTNER_ACCESS_TOKEN</code></pre>
                        <ul class="code-description">
                            <li>
                                <span>X-Partner-Token</span>
                                The identifier of the partner, which is recognized and authorized by the provider.
                            </li>
                        </ul>
                    </section>
                
                    <section id="error-codes">
                        <h2>3. API ERROR CODES</h2>
                        <table class="fmis-table">
                            <thead>
                                <tr>
                                    <th>Error Code</th>
                                    <th>Message</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr><td>400</td><td>Invalid request. Please check the input parameters.</td></tr>
                                <tr><td>401</td><td>You are not authorized to perform this action.</td></tr>
                                <tr><td>403</td><td>You do not have permission to access this resource.</td></tr>
                                <tr><td>404</td><td>Requested resource not found.</td></tr>
                                <tr><td>500</td><td>An unexpected error occurred. Please try again later.</td></tr>
                                <tr><td>502</td><td>Could not connect to any target host.</td></tr>
                            </tbody>
                        </table>
                    </section>
                </div>
            `;

            // Handle the initial page load and "Overview" display
            if (window.location.href === config.url.docsUrl || window.location.href === config.url.overviewUrl) {
                manipulateReDoc(redocWrap, apiContent, "none");
                activeOverview(overview, overviewContent);
                overviewContent.style.display = "";
            } else {
                overviewContent.style.display = "none";
            }

            // Handle "Overview" click
            overview.addEventListener("click", () => {
                overviewContent.setAttribute("data-visible", "1");
                manipulateReDoc(redocWrap, apiContent, "none");
                activeOverview(overview, overviewContent);
                window.location.href = config.url.overviewUrl;
            });

            // Handle sidebar item clicks
            if (sideBar) {
                let sideBarItems = sideBar.querySelectorAll("li");
                sideBarItems.forEach(item => {
                    item.addEventListener("click", function () {
                        overviewContent.setAttribute("data-visible", "2");
                        manipulateReDoc(redocWrap, apiContent, "", item);
                        activeOverview(overview, overviewContent);
                    });
                });
            }

            // Stop observing after modifications are applied
            obs.disconnect();
        }
    });

    // Start observing the document body for changes
    observer.observe(document.body, { childList: true, subtree: true });
});