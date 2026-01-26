const chatContainer = document.getElementById("chatContainer");
const userInput = document.getElementById("userInput");
const sendBtn = document.getElementById("sendBtn");

// Append message to chat container
function appendMessage(message, sender) {
    const msgDiv = document.createElement("div");
    msgDiv.classList.add("message", sender);
    msgDiv.style.whiteSpace = "pre-wrap";
    msgDiv.textContent = message;
    chatContainer.appendChild(msgDiv);
    chatContainer.scrollTop = chatContainer.scrollHeight;
    return msgDiv;
}

// Typing effect for bot response
function typeText(element, text, speed = 20) {
    let i = 0;
    return new Promise((resolve) => {
        const interval = setInterval(() => {
            element.textContent += text[i];
            i++;
            chatContainer.scrollTop = chatContainer.scrollHeight;
            if (i >= text.length) {
                clearInterval(interval);
                resolve();
            }
        }, speed);
    });
}

// Show animated three-dot loading
function showLoadingDots(element) {
    element.classList.add("loading");
    let dots = 0;
    const interval = setInterval(() => {
        element.textContent = "•".repeat(dots % 4);
        dots++;
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }, 400);
    return interval;
}

// Send message function
async function sendMessage() {
    const text = userInput.value.trim();
    if (!text) return;

    // Show user message in green
    appendMessage(text, "user");
    userInput.value = "";

    // Show bot placeholder with three-dot loading
    const botMsgDiv = appendMessage("", "bot");
    const loadingInterval = showLoadingDots(botMsgDiv);

    try {
        const response = await fetch(
            `${baseUrl}${apiPrefix}/fmis-proxy-ai/chat?question=${encodeURIComponent(text)}`,
            { method: "GET", headers: { "Content-Type": "text/plain" } }
        );

        if (!response.ok) throw new Error("API Error");

        const answer = await response.text();

        // Stop loading
        clearInterval(loadingInterval);
        botMsgDiv.classList.remove("loading");
        botMsgDiv.textContent = "";

        // Show answer character by character
        await typeText(botMsgDiv, answer, 20);

    } catch (err) {
        clearInterval(loadingInterval);
        botMsgDiv.textContent = "Error: " + err.message;
    }
}

function scrollToBottom() {
    chatContainer.scrollTo({
        top: chatContainer.scrollHeight,
        behavior: "smooth"
    });
}

// Event listeners
sendBtn.addEventListener("click", sendMessage);
userInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});