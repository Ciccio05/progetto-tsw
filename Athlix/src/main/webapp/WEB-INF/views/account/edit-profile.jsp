<!-- RUOLO DELLA PAGINA: Gestisce la modifica dei dati del profilo con validazione lato client. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="fn"
           uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:formatDate
        value="${sessionScope.utente.dataNascita}"
        pattern="yyyy-MM-dd"
        var="currentBirthDate" />

<!DOCTYPE html>

<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Modifica profilo - AthliX</title>

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
                        Modifica dati personali
                    </h1>

                    <p class="FM-muted">
                        Aggiorna le informazioni associate al tuo account.
                    </p>

                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/account">

                    Torna all'area personale

                </a>

            </div>

            <c:if test="${not empty profileError}">

                <div class="FM-message FM-message-error"
                     role="alert">

                    <c:out value="${profileError}" />

                </div>

            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/account/edit"
                  class="FM-auth-form"
                  id="FM-profile-form"
                  novalidate>

                <div class="FM-form-row">

                    <div class="FM-form-group">

                        <label for="nome">
                            Nome
                        </label>

                        <input class="FM-input"
                               id="nome"
                               name="nome"
                               type="text"
                               maxlength="50"
                               autocomplete="given-name"
                               required
                               placeholder="Nome"
                               value="${fn:escapeXml(not empty oldNome ? oldNome : sessionScope.utente.nomeUtente)}">

                        <span class="FM-inline-error"
                              id="nomeError"></span>

                    </div>

                    <div class="FM-form-group">

                        <label for="cognome">
                            Cognome
                        </label>

                        <input class="FM-input"
                               id="cognome"
                               name="cognome"
                               type="text"
                               maxlength="50"
                               autocomplete="family-name"
                               required
                               placeholder="Cognome"
                               value="${fn:escapeXml(not empty oldCognome ? oldCognome : sessionScope.utente.cognomeUtente)}">

                        <span class="FM-inline-error"
                              id="cognomeError"></span>

                    </div>

                </div>

                <div class="FM-form-group">

                    <label for="email">
                        Email
                    </label>

                    <input class="FM-input"
                           id="email"
                           name="email"
                           type="email"
                           maxlength="120"
                           autocomplete="email"
                           required
                           placeholder="nome@example.com"
                           data-original-email="${fn:escapeXml(sessionScope.utente.email)}"
                           value="${fn:escapeXml(not empty oldEmail ? oldEmail : sessionScope.utente.email)}">

                    <span class="FM-inline-error"
                          id="emailError"></span>

                </div>

                <div class="FM-form-row">

                    <div class="FM-form-group">

                        <label for="dataNascita">
                            Data di nascita
                        </label>

                        <input class="FM-input"
                               id="dataNascita"
                               name="dataNascita"
                               type="date"
                               autocomplete="bday"
                               value="${not empty oldDataNascita ? oldDataNascita : currentBirthDate}">

                        <span class="FM-inline-error"
                              id="dataNascitaError"></span>

                    </div>

                    <div class="FM-form-group">

                        <label for="telefono">
                            Telefono
                        </label>

                        <input class="FM-input"
                               id="telefono"
                               name="telefono"
                               type="tel"
                               maxlength="20"
                               autocomplete="tel"
                               placeholder="+39 333 1234567"
                               value="${fn:escapeXml(not empty oldTelefono ? oldTelefono : sessionScope.utente.numeroTelefono)}">

                        <span class="FM-inline-error"
                              id="telefonoError"></span>

                    </div>

                </div>

                <div class="FM-form-group">

                    <label for="indirizzo">
                        Indirizzo
                    </label>

                    <input class="FM-input"
                           id="indirizzo"
                           name="indirizzo"
                           type="text"
                           maxlength="150"
                           autocomplete="street-address"
                           required
                           placeholder="Via, città, CAP e provincia"
                           value="${fn:escapeXml(not empty oldIndirizzo ? oldIndirizzo : sessionScope.utente.indirizzoUtente)}">

                    <span class="FM-inline-error"
                          id="indirizzoError"></span>

                </div>

                <button class="FM-button FM-button-primary"
                        type="submit">

                    Salva modifiche

                </button>

            </form>

        </section>

    </div>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />

<script>
(() => {

    /*
     * Context path generato dalla JSP.
     * Esempio: /athlix
     */
    const contextPath =
        '${pageContext.request.contextPath}';

    const form =
        document.getElementById("FM-profile-form");

    const nome =
        document.getElementById("nome");

    const cognome =
        document.getElementById("cognome");

    const email =
        document.getElementById("email");

    const dataNascita =
        document.getElementById("dataNascita");

    const telefono =
        document.getElementById("telefono");

    const indirizzo =
        document.getElementById("indirizzo");

    if (!form) {
        return;
    }

    /*
     * Regex utilizzate anche lato server.
     */
    const namePattern =
        /^[\p{L}][\p{L}' -]{1,49}$/u;

    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const phonePattern =
        /^\+?[0-9 .-]{7,20}$/;

    /*
     * Mostra o elimina un errore sotto un campo.
     */
    const setError = (input, message) => {

        const error =
            document.getElementById(
                input.id + "Error"
            );

        if (!error) {
            return;
        }

        const hasError =
            Boolean(message);

        input.classList.toggle(
            "FM-input-error",
            hasError
        );

        error.textContent =
            message || "";

        error.classList.toggle(
            "FM-inline-error-visible",
            hasError
        );
    };

    /*
     * Controlla tramite AJAX che una nuova email
     * non sia già utilizzata.
     *
     * Se l'email è rimasta uguale a quella originale,
     * non è necessario interrogare il server.
     */
    const validateEmailAvailability =
        async () => {

            const value =
                email.value
                    .trim()
                    .toLowerCase();

            const original =
                (email.dataset.originalEmail || "")
                    .trim()
                    .toLowerCase();

            if (!emailPattern.test(value)) {
                return true;
            }

            if (value === original) {
                return true;
            }

            try {

                /*
                 * IMPORTANTE:
                 * niente template literal JavaScript con dollaro-graffa
                 * perché questa è una JSP e l'espressione verrebbe
                 * interpretata come EL.
                 */
                const url =
                    contextPath
                    + "/check-email?email="
                    + encodeURIComponent(value);

                const response =
                    await fetch(
                        url,
                        {
                            method: "GET",

                            headers: {
                                "Accept":
                                    "application/json"
                            }
                        }
                    );

                if (!response.ok) {
                    return false;
                }

                const data =
                    await response.json();

                return data.valida === true
                    && data.disponibile === true;

            } catch (error) {

                console.error(
                    "Errore verifica email:",
                    error
                );

                return false;
            }
        };

    /*
     * Quando l'utente modifica un campo,
     * eliminiamo il vecchio messaggio di errore.
     */
    [
        nome,
        cognome,
        email,
        dataNascita,
        telefono,
        indirizzo
    ].forEach(input => {

        input.addEventListener(
            "input",
            () => {
                setError(
                    input,
                    ""
                );
            }
        );

    });

    /*
     * Validazione del form.
     */
    form.addEventListener(
        "submit",
        async event => {

            event.preventDefault();

            let valid = true;

            let firstInvalid = null;

            const validate =
                (input, message) => {

                    if (message) {

                        setError(
                            input,
                            message
                        );

                        valid = false;

                        if (!firstInvalid) {
                            firstInvalid =
                                input;
                        }

                    } else {

                        setError(
                            input,
                            ""
                        );
                    }
                };

            /*
             * Nome
             */
            validate(
                nome,
                namePattern.test(
                    nome.value.trim()
                )
                    ? ""
                    : "Inserisci un nome valido."
            );

            /*
             * Cognome
             */
            validate(
                cognome,
                namePattern.test(
                    cognome.value.trim()
                )
                    ? ""
                    : "Inserisci un cognome valido."
            );

            /*
             * Email
             */
            validate(
                email,
                emailPattern.test(
                    email.value.trim()
                )
                    ? ""
                    : "Inserisci un indirizzo email valido."
            );

            /*
             * Data di nascita.
             * È facoltativa, ma non può essere futura.
             */
            if (dataNascita.value) {

                const selectedDate =
                    new Date(
                        dataNascita.value
                        + "T00:00:00"
                    );

                const today =
                    new Date();

                today.setHours(
                    0,
                    0,
                    0,
                    0
                );

                validate(
                    dataNascita,
                    selectedDate > today
                        ? "La data di nascita non può essere nel futuro."
                        : ""
                );

            } else {

                setError(
                    dataNascita,
                    ""
                );
            }

            /*
             * Telefono.
             * È facoltativo.
             */
            const phoneValue =
                telefono.value.trim();

            validate(
                telefono,
                phoneValue
                && !phonePattern.test(
                    phoneValue
                )
                    ? "Inserisci un numero di telefono valido."
                    : ""
            );

            /*
             * Indirizzo.
             */
            const addressValue =
                indirizzo.value.trim();

            validate(
                indirizzo,
                addressValue.length >= 3
                && addressValue.length <= 150
                    ? ""
                    : "Inserisci un indirizzo valido."
            );

            /*
             * Se i dati locali sono validi,
             * controlliamo la disponibilità dell'email.
             */
            if (valid) {

                const available =
                    await validateEmailAvailability();

                if (!available) {

                    setError(
                        email,
                        "Questa email è già utilizzata oppure la verifica non è disponibile."
                    );

                    valid = false;

                    if (!firstInvalid) {
                        firstInvalid =
                            email;
                    }
                }
            }

            /*
             * Se c'è almeno un errore,
             * portiamo il cursore sul primo campo errato.
             */
            if (!valid) {

                if (firstInvalid) {
                    firstInvalid.focus();
                }

                return;
            }

            /*
             * Tutti i controlli client-side sono superati.
             *
             * Il server eseguirà comunque nuovamente
             * tutte le validazioni.
             */
            form.submit();
        }
    );

})();
</script>

</body>

</html>
