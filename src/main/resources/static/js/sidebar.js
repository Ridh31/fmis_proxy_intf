const toggleBtn = document.getElementById("sidebarToggle");
const sidebar = document.getElementById("sidebar");

const overlay = document.createElement("div");
overlay.id = "sidebarOverlay";
overlay.style.position = "fixed";
overlay.style.top = "0";
overlay.style.left = "0";
overlay.style.width = "100%";
overlay.style.height = "100%";
overlay.style.backgroundColor = "rgba(0,0,0,0.3)";
overlay.style.zIndex = "900";
overlay.style.display = "none";
document.body.appendChild(overlay);

function toggleSidebar() {
    if (window.innerWidth <= 768) {
        // Mobile
        const isShown = sidebar.classList.contains('show');
        if (isShown) {
            sidebar.classList.remove('show');
            overlay.style.display = 'none';
        } else {
            sidebar.classList.add('show');
            overlay.style.display = 'block';
        }
    } else {
        // Desktop
        sidebar.classList.toggle('collapsed');
    }
}

toggleBtn.addEventListener("click", toggleSidebar);

overlay.addEventListener("click", () => {
    sidebar.classList.remove("show");
    overlay.style.display = "none";
});