<!-- RUOLO DELLA PAGINA: Interfaccia di assistenza con comunicazione AJAX verso il bot. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Assistenza - AthliX</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page">

<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main class="FM-main">

    <div class="FM-container FM-account-container">

        <section class="FM-account-card">

            <div class="FM-account-header">

                <div>

                    <h1 class="FM-section-title">
                        Assistenza AthliX
                    </h1>

                    <p class="FM-muted">
                        Chiedi informazioni su ordini, spedizioni, prodotti,
                        carrello, account, pagamenti e recensioni.
                    </p>

                </div>

                <button class="FM-button FM-button-light"
                        id="FM-bot-clear"
                        type="button">

                    Nuova conversazione

                </button>

            </div>

            <div class="FM-bot-quick-actions"
                 aria-label="Domande rapide">

                <button class="FM-button FM-button-light FM-bot-suggestion"
                        type="button"
                        data-message="Dov'è il mio ordine?">
                    Dov'è il mio ordine?
                </button>

                <button class="FM-button FM-button-light FM-bot-suggestion"
                        type="button"
                        data-message="Come funziona il carrello?">
                    Come funziona il carrello?
                </button>

                <button class="FM-button FM-button-light FM-bot-suggestion"
                        type="button"
                        data-message="Come modifico la password?">
                    Cambio password
                </button>

                <button class="FM-button FM-button-light FM-bot-suggestion"
                        type="button"
                        data-message="Come posso lasciare una recensione?">
                    Recensioni
                </button>

            </div>

            <div class="FM-bot-chat"
                 id="FM-bot-chat"
                 aria-live="polite"
                 aria-label="Conversazione con assistente AthliX">

                <c:forEach var="item"
                           items="${botHistory}">

                    <div class="FM-bot-message ${item.ruolo == 'utente' ? 'FM-bot-message-user' : 'FM-bot-message-assistant'}">

                        <strong>
                            ${item.ruolo == 'utente' ? 'Tu' : 'AthliX Bot'}
                        </strong>

                        <p>
                            <c:out value="${item.testo}" />
                        </p>

                    </div>

                </c:forEach>

            </div>

            <form class="FM-auth-form FM-bot-form"
                  id="FM-bot-form">

                <div class="FM-form-group">

                    <label for="FM-bot-input">
                        Il tuo messaggio
                    </label>

                    <textarea class="FM-input"
                              id="FM-bot-input"
                              name="message"
                              rows="3"
                              maxlength="500"
                              required
                              placeholder="Es. Dov'è il mio ordine?"></textarea>

                    <span class="FM-inline-error"
                          id="FM-bot-error"></span>

                </div>

                <button class="FM-button FM-button-primary"
                        id="FM-bot-submit"
                        type="submit">

                    Invia

                </button>

            </form>

        </section>

    </div>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />

<script>
(() => {

    const contextPath =
        '${pageContext.request.contextPath}';

    const form =
        document.getElementById("FM-bot-form");

    const input =
        document.getElementById("FM-bot-input");

    const chat =
        document.getElementById("FM-bot-chat");

    const submit =
        document.getElementById("FM-bot-submit");

    const clear =
        document.getElementById("FM-bot-clear");

    const error =
        document.getElementById("FM-bot-error");

    const suggestions =
        document.querySelectorAll(
            ".FM-bot-suggestion"
        );

    const escapeHtml = value => {

        const div =
            document.createElement("div");

        div.textContent =
            value;

        return div.innerHTML;
    };

    const scrollToBottom = () => {

        chat.scrollTop =
            chat.scrollHeight;
    };

    const appendMessage = (
            role,
            text,
            actionLabel = null,
            actionUrl = null) => {

        const wrapper =
            document.createElement("div");

        wrapper.className =
            "FM-bot-message "
            + (role === "utente"
                ? "FM-bot-message-user"
                : "FM-bot-message-assistant");

        let html =
            "<strong>"
            + (role === "utente"
                ? "Tu"
                : "AthliX Bot")
            + "</strong>"
            + "<p>"
            + escapeHtml(text)
            + "</p>";

        if (actionLabel && actionUrl) {

            html +=
                '<a class="FM-button FM-button-secondary" href="'
                + escapeHtml(actionUrl)
                + '">'
                + escapeHtml(actionLabel)
                + '</a>';
        }

        wrapper.innerHTML =
            html;

        chat.appendChild(
            wrapper
        );

        scrollToBottom();
    };

    const sendMessage = async message => {

        const text =
            message.trim();

        error.textContent = "";

        error.classList.remove(
            "FM-inline-error-visible"
        );

        if (!text) {

            error.textContent =
                "Scrivi un messaggio.";

            error.classList.add(
                "FM-inline-error-visible"
            );

            input.focus();

            return;
        }

        if (text.length > 500) {

            error.textContent =
                "Il messaggio può contenere al massimo 500 caratteri.";

            error.classList.add(
                "FM-inline-error-visible"
            );

            input.focus();

            return;
        }

        appendMessage(
            "utente",
            text
        );

        input.value = "";

        submit.disabled = true;

        try {

            const params =
                new URLSearchParams();

            params.set(
                "message",
                text
            );

            const response =
                await fetch(
                    contextPath
                    + "/bot/message",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/x-www-form-urlencoded;charset=UTF-8",

                            "Accept":
                                "application/json"
                        },

                        body:
                            params.toString()
                    }
                );

            const data =
                await response.json();

            if (!response.ok
                    || !data.success) {

                throw new Error(
                    data.message
                    || "Risposta non disponibile."
                );
            }

            appendMessage(
                "bot",
                data.reply,
                data.actionLabel,
                data.actionUrl
            );

        } catch (exception) {

            appendMessage(
                "bot",
                "Non riesco a rispondere in questo momento. Riprova tra poco."
            );

        } finally {

            submit.disabled = false;

            input.focus();
        }
    };

    form.addEventListener(
        "submit",
        event => {

            event.preventDefault();

            sendMessage(
                input.value
            );
        }
    );

    suggestions.forEach(
        button => {

            button.addEventListener(
                "click",
                () => {

                    sendMessage(
                        button.dataset.message
                    );
                }
            );
        }
    );

    clear.addEventListener(
        "click",
        async () => {

            try {

                await fetch(
                    contextPath
                    + "/bot/clear",
                    {
                        method: "POST",

                        headers: {
                            "Accept":
                                "application/json"
                        }
                    }
                );

            } finally {

                chat.innerHTML = "";

                appendMessage(
                    "bot",
                    "Ciao! Sono l'assistente AthliX. Posso aiutarti con ordini, spedizioni, prodotti, carrello, account, pagamenti e recensioni."
                );

                input.focus();
            }
        }
    );

    scrollToBottom();

})();
</script>

</body>
</html>
