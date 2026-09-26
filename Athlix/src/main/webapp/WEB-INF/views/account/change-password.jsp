<!-- RUOLO DELLA PAGINA: Gestisce il form per cambiare la password dell’utente autenticato. -->
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

    <title>Cambia password - AthliX</title>

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
                        Cambia password
                    </h1>

                    <p class="FM-muted">
                        Inserisci la password attuale e scegli una nuova password.
                    </p>

                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/account">

                    Torna all'area personale

                </a>

            </div>

            <c:if test="${not empty passwordError}">

                <div class="FM-message FM-message-error"
                     role="alert">

                    <c:out value="${passwordError}" />

                </div>

            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/account/password"
                  class="FM-auth-form"
                  id="FM-password-form"
                  novalidate>

                <div class="FM-form-group">

                    <label for="currentPassword">
                        Password attuale
                    </label>

                    <input class="FM-input"
                           id="currentPassword"
                           name="currentPassword"
                           type="password"
                           autocomplete="current-password"
                           placeholder="Password attuale"
                           required>

                    <span class="FM-inline-error"
                          id="currentPasswordError"></span>

                </div>

                <div class="FM-form-group">

                    <label for="newPassword">
                        Nuova password
                    </label>

                    <input class="FM-input"
                           id="newPassword"
                           name="newPassword"
                           type="password"
                           minlength="8"
                           maxlength="20"
                           autocomplete="new-password"
                           required
                           placeholder="8-20 caratteri, una maiuscola e un numero">

                    <span class="FM-inline-error"
                          id="newPasswordError"></span>

                </div>

                <div class="FM-form-group">

                    <label for="confirmPassword">
                        Conferma nuova password
                    </label>

                    <input class="FM-input"
                           id="confirmPassword"
                           name="confirmPassword"
                           type="password"
                           minlength="8"
                           maxlength="20"
                           autocomplete="new-password"
                           placeholder="Ripeti la nuova password"
                           required>

                    <span class="FM-inline-error"
                          id="confirmPasswordError"></span>

                </div>

                <button class="FM-button FM-button-primary"
                        type="submit">

                    Aggiorna password

                </button>

            </form>

        </section>

    </div>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />

<script>
(() => {
    const form = document.getElementById("FM-password-form");

    const currentPassword =
        document.getElementById("currentPassword");

    const newPassword =
        document.getElementById("newPassword");

    const confirmPassword =
        document.getElementById("confirmPassword");

    const passwordPattern =
        /^(?=.*[A-Z])(?=.*\d)\S{8,20}$/;

    const setError = (input, message) => {
        const error =
            document.getElementById(
                input.id + "Error"
            );

        input.classList.toggle(
            "FM-input-error",
            Boolean(message)
        );

        error.textContent =
            message || "";

        error.classList.toggle(
            "FM-inline-error-visible",
            Boolean(message)
        );
    };

    form.addEventListener("submit", event => {
        let valid = true;
        let firstInvalid = null;

        const validate = (input, message) => {
            if (message) {
                setError(input, message);
                valid = false;
                firstInvalid ??= input;
            } else {
                setError(input, "");
            }
        };

        validate(
            currentPassword,
            currentPassword.value
                ? ""
                : "Inserisci la password attuale."
        );

        validate(
            newPassword,
            passwordPattern.test(newPassword.value)
                ? ""
                : "Usa 8-20 caratteri, almeno una maiuscola e un numero, senza spazi."
        );

        validate(
            confirmPassword,
            confirmPassword.value === newPassword.value
                && confirmPassword.value
                ? ""
                : "Le due nuove password non coincidono."
        );

        if (!valid) {
            event.preventDefault();
            firstInvalid?.focus();
        }
    });
})();
</script>

</body>
</html>
