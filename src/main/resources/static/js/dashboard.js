/**
 * Animates counting a number from 0 up to a target value within a specified duration.
 * The count increments dynamically so the animation completes smoothly and quickly,
 * even for large target numbers.
 *
 * @param {HTMLElement} el - The DOM element where the animated number will be displayed.
 * @param {number} target - The final number to count up to.
 * @param {number} duration - Total time in milliseconds for the animation to complete (default 1000ms).
 */
function animateCount(el, target, duration = 1000) {
    const start = 0;
    const range = target - start;
    const minStepTime = 20;

    // Calculate how many steps based on duration and minStepTime
    const steps = Math.max(Math.floor(duration / minStepTime), 1);
    const increment = Math.ceil(range / steps);
    let current = start;

    const timer = setInterval(() => {
        current += increment;
        if (current > target) current = target;
        el.textContent = current.toLocaleString();
        if (current === target) clearInterval(timer);
    }, minStepTime);
}

/**
 * Runs after the HTML document is fully loaded.
 * Fetches the dashboard summary data from the API,
 * then renders a pie chart and updates the count cards with animated numbers.
 */
document.addEventListener("DOMContentLoaded", () => {
    const ctx = document.getElementById("dashboard-chart");
    const apiPrefix = document.querySelector(".api-prefix")?.dataset.apiPrefix;

    const baseUrl = window.location.origin;
    const url = `${baseUrl}${apiPrefix}/dashboard/summary`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            const summary = data?.data || {};
            const bank = summary.bank_statement || 0;
            const sarmis = summary.sarmis_interface || 0;

            // Animate card numbers
            document.querySelectorAll(".dashboard-card-number").forEach(el => {
                const key = el.dataset.key;
                const value = summary[key] || 0;
                animateCount(el, value, 1500);
            });

            // Render chart
            new Chart(ctx, {
                type: "pie",
                data: {
                    labels: ["Bank Statement", "SARMIS Interface"],
                    datasets: [{
                        label: "Records",
                        data: [bank, sarmis],
                        backgroundColor: [
                            "rgba(59, 130, 246, 0.6)",
                            "rgba(243, 156, 18, 0.6)"
                        ],
                        borderColor: [
                            "rgba(59, 130, 246, 1)",
                            "rgba(243, 156, 18, 1)"
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: "bottom"
                        }
                    }
                }
            });
        })
        .catch(error => {
            console.error("Error loading dashboard data:", error);
            showToast("error", "Error loading dashboard data.");
        });
});