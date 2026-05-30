document.addEventListener("DOMContentLoaded", function () {
    setupChannelRadios();
    setupMembersToggle();
    setupTranslateButtons();
    setupMessageForm();
});

function getCsrfHeaders() {
    const tokenMeta = document.querySelector("meta[name='_csrf']");
    const headerMeta = document.querySelector("meta[name='_csrf_header']");

    if (tokenMeta && headerMeta) {
        return {
            [headerMeta.content]: tokenMeta.content
        };
    }

    const csrfInput = document.querySelector("input[name='_csrf']");

    if (csrfInput) {
        return {
            "X-CSRF-TOKEN": csrfInput.value
        };
    }

    return {};
}

function setupChannelRadios() {
    document.querySelectorAll("input[name='channel']").forEach(function (radio) {
        radio.addEventListener("change", function () {
            if (radio.dataset.url) {
                window.location.href = radio.dataset.url;
            }
        });
    });
}

function setupMembersToggle() {
    const button = document.getElementById("toggleMembersButton");
    const members = document.getElementById("membersList");

    if (!button || !members) {
        return;
    }

    button.addEventListener("click", function () {
        members.classList.toggle("hidden");
        button.textContent = members.classList.contains("hidden") ? "show" : "hide";
    });
}

function setupTranslateButtons() {
    document.querySelectorAll(".translate-button").forEach(function (button) {
        bindTranslateButton(button);
    });
}

function bindTranslateButton(button) {
    if (!button || button.dataset.bound === "true") {
        return;
    }

    button.dataset.bound = "true";

    button.addEventListener("click", async function () {
        const messageId = button.dataset.messageId;
        const card = button.closest(".message-card");
        const result = card ? card.querySelector(".translation-result") : null;

        if (!messageId || !result) {
            return;
        }

        button.disabled = true;
        result.textContent = "translating...";

        try {
            const response = await fetch(`/web/messages/${messageId}/translate`, {
                method: "POST",
                headers: getCsrfHeaders()
            });

            if (!response.ok) {
                const errorText = await response.text();
                result.textContent = "Could not translate this message.";
                console.error("Translation error:", response.status, errorText);
                return;
            }

            const data = await response.json();
            result.textContent = data.translatedText;
        } catch (error) {
            console.error(error);
            result.textContent = "translation error";
        } finally {
            button.disabled = false;
        }
    });
}

function setupMessageForm() {
    const form = document.getElementById("messageForm");

    if (!form) {
        return;
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        const channelId = form.dataset.channelId;
        const input = document.getElementById("messageInput");
        const messagesList = document.getElementById("messagesList");

        if (!channelId || !input || !messagesList) {
            return;
        }

        const content = input.value.trim();

        if (!content) {
            return;
        }

        const formData = new FormData();
        formData.append("content", content);

        try {
            const response = await fetch(`/web/channels/${channelId}/messages`, {
                method: "POST",
                headers: getCsrfHeaders(),
                body: formData
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error("Message send error:", response.status, errorText);
                return;
            }

            const message = await response.json();

            messagesList.insertAdjacentHTML("beforeend", createMessageHtml(message));
            input.value = "";

            const newButton = messagesList.querySelector(
                `.translate-button[data-message-id="${message.id}"]`
            );

            bindTranslateButton(newButton);
            messagesList.scrollTop = messagesList.scrollHeight;
        } catch (error) {
            console.error(error);
        }
    });
}

function createMessageHtml(message) {
    const author = message.authorPublicName || message.authorUsername || "User";

    return `
        <div class="message-card" data-message-id="${escapeHtml(message.id)}">
            <div class="message-author">${escapeHtml(author)}</div>
            <div class="message-content">${escapeHtml(message.content)}</div>

            <button type="button"
                    class="link-button translate-button"
                    data-message-id="${escapeHtml(message.id)}">
                translate
            </button>

            <div class="translation-result"></div>
        </div>
    `;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}