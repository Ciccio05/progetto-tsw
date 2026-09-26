<!-- RUOLO DELLA PAGINA: Gestisce il form di accesso e i messaggi relativi all’autenticazione. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"
           uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="it">

<head>
    <meta charset="UTF-8">
            <!--per il responsive sui mobile-->
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Accedi - AthliX</title>

    <!--collego il CSS non in maniera fissa cosi se cambia il context root
    JSP costruisce automaticamente il perrcorso corretto-->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="FM-page FM-auth-page">

    <!--header-->
<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp"/>

<main class="FM-main">
    <div class="FM-container">

        <section class="FM-auth-container">
            <div class="FM-auth-card">

                <h1 class="FM-auth-title">
                    Accedi al tuo account
                </h1>

                <!-- REGISTRAZIONE COMPLETATA -->
                <c:if test="${param.success == 'registered'}">
                    <div class="FM-message FM-message-success"
                         role="status">
                        Registrazione completata.
                        Ora puoi effettuare il login.
                    </div>
                </c:if>

                <!-- CREDENZIALI NON VALIDE -->
                <c:if test="${not empty param.error}">
                    <div class="FM-message FM-message-error"
                         role="alert">
                        Email o password non corrette.
                    </div>
                </c:if>

                <!-- FORM LOGIN -->
                <form id="FM-login-form"
                      class="FM-auth-form"
                      action="${pageContext.request.contextPath}/login"
                      method="post"
                      novalidate> <!--novalidate disabilita i messaggi standard del browser-->

                    <!-- Mantiene la destinazione richiesta prima del login. -->
                    <input type="hidden"
                           name="redirect"
                           value="${fn:escapeXml(param.redirect)}">

                    <!-- EMAIL -->
                    <div class="FM-form-group">

                        <label class="FM-label"
                               for="FM-login-email">
                            Email
                        </label>

                        <input class="FM-input"
                               type="email"
                               id="FM-login-email"
                               name="email"
                               maxlength="120"
                               autocomplete="email"
                               aria-describedby="FM-login-email-error"
                               placeholder="nome@email.it"
                               required
                               autofocus>
                               <!--con autofocus quando entro nela pagina il cursore viene automaticamante posizionato nel campo email-->

                        <span class="FM-inline-error"
                              id="FM-login-email-error">
                            Inserisci un indirizzo email valido.
                        </span>

                    </div>

                    <!-- PASSWORD -->
                    <div class="FM-form-group">

                        <label class="FM-label"
                               for="FM-login-password">
                            Password
                        </label>
<!--il tipo password nasconde i caratteri-->
                        <input class="FM-input"
                               type="password"
                               id="FM-login-password"
                               name="password"
                               maxlength="20"
                               autocomplete="current-password"
                               aria-describedby="FM-login-password-error"
                               placeholder="Inserisci la password"
                               required>

                        <span class="FM-inline-error"
                              id="FM-login-password-error">
                            Inserisci la password.
                        </span>

                    </div>

                    <!-- SUBMIT -->
                    <button class="FM-button FM-button-primary FM-button-full"
                            type="submit">
                        Accedi
                    </button>

                </form>

                <!--link alla registrazione se non ho un account-->
                <p class="FM-auth-links">
                    Non hai un account?

                    <a href="${pageContext.request.contextPath}/register">
                        Registrati
                    </a>
                </p>

            </div>
        </section>

    </div>
</main>

<!--footer-->
<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp"/>

<script>
    /*IIFE, funzione immediatamente eseguibile*/
(() => {

    /* ELEMENTI DEL FORM */
    const form = document.getElementById('FM-login-form');
    const email = document.getElementById('FM-login-email');
    const password = document.getElementById('FM-login-password');
    const emailError = document.getElementById('FM-login-email-error');
    const passwordError = document.getElementById('FM-login-password-error');

    /* Stessa regex utilizzata dal LoginServlet. */
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    /* Imposta lo stato valido/non valido del campo. */
    function setFieldState(input,error,valid) {
        input.classList.toggle('FM-input-error',!valid);
        input.classList.toggle('FM-input-success',valid);
        input.setAttribute('aria-invalid',valid ? 'false' : 'true');

        error.classList.toggle('FM-inline-error-visible',!valid);

        return valid;
    }

    /* Rimuove lo stato precedente quando il campo viene modificato. */
    function clearFieldState(input,error) {
        input.classList.remove('FM-input-error','FM-input-success');

        input.removeAttribute('aria-invalid');

        error.classList.remove('FM-inline-error-visible');
    }

    /* VALIDAZIONE SUBMIT */
    form.addEventListener('submit',event => {

        const emailValid = setFieldState(
            email,
            emailError,
            emailPattern.test(email.value.trim().toLowerCase())
        );

        const passwordValid = setFieldState(
            password,
            passwordError,
            password.value.length > 0
        );

        /* Blocchiamo il POST se almeno un campo non è valido. */
        if (!emailValid || !passwordValid) {
            event.preventDefault();

            if (!emailValid) {
                email.focus();
            } else {
                password.focus();
            }
        }
    });

    /* Rimuoviamo gli errori quando l'utente modifica i campi. */
    email.addEventListener('input',() => {
        clearFieldState(email,emailError);
    });

    password.addEventListener('input',() => {
        clearFieldState(password,passwordError);
    });

})();
</script>

</body>
</html>
