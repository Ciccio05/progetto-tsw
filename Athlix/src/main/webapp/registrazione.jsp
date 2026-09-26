<!-- RUOLO DELLA PAGINA: Gestisce il form di registrazione con validazione JavaScript e controllo email AJAX. -->
<!--direttive-->
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

         <!--abilito JSTL-->
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

    <title>Registrazione - AthliX</title>

    <!--collego il CSS non in maniera fissa cosi se cambia il context root
    JSP costruisce automaticamente il perrcorso corretto-->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page FM-auth-page">

    <!--includo l'header-->
<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp"/>

<main class="FM-main">

    <div class="FM-container">

        <section class="FM-auth-container FM-auth-container-wide">

            <div class="FM-auth-card FM-auth-card-wide">

                <h1 class="FM-auth-title">
                    Crea il tuo account
                </h1>

                <!-- Errore restituito dal RegistrazioneServlet -->
                <c:if test="${not empty registrationError}">

                    <div class="FM-message FM-message-error"
                         role="alert">

                        <c:out value="${registrationError}"/>

                    </div>

                </c:if>

                <form id="FM-registration-form"
                      class="FM-auth-form"
                      action="${pageContext.request.contextPath}/register"
                      method="post"
                      novalidate> <!--novalidate disattiva i popup standard del browser-->

                    <!-- NOME E COGNOME -->
                    <div class="FM-form-row">

                        <div class="FM-form-group">

                            <label class="FM-label"
                                   for="FM-name">
                                Nome
                            </label>

                            <input class="FM-input"
                                   id="FM-name"
                                   name="nome"
                                   type="text"
                                   maxlength="50"
                                   autocomplete="given-name"
                                   placeholder="Es. Mario"
                                   value="${fn:escapeXml(oldNome)}"
                                   required>

                            <span id="FM-name-error"
                                  class="FM-inline-error">
                                Inserisci un nome valido.
                            </span>

                        </div>

                        <div class="FM-form-group">

                            <label class="FM-label"
                                   for="FM-surname">
                                Cognome
                            </label>

                            <input class="FM-input"
                                   id="FM-surname"
                                   name="cognome"
                                   type="text"
                                   maxlength="50"
                                   autocomplete="family-name"
                                   placeholder="Es. Rossi"
                                   value="${fn:escapeXml(oldCognome)}"
                                   required>

                            <span id="FM-surname-error"
                                  class="FM-inline-error">
                                Inserisci un cognome valido.
                            </span>

                        </div>

                    </div>

                    <!-- CITTÀ, CAP E PROVINCIA -->
                    <div class="FM-form-row FM-form-row-three">

                        <div class="FM-form-group">

                            <label class="FM-label"
                                   for="FM-city">
                                Città
                            </label>

                            <input class="FM-input"
                                   id="FM-city"
                                   name="citta"
                                   type="text"
                                   maxlength="50"
                                   autocomplete="address-level2"
                                   placeholder="Es. Salerno"
                                   value="${fn:escapeXml(oldCitta)}"
                                   required>

                            <span id="FM-city-error"
                                  class="FM-inline-error">
                                Inserisci una città valida.
                            </span>

                        </div>

                        <div class="FM-form-group">

                            <label class="FM-label"
                                   for="FM-cap">
                                CAP
                            </label>

                            <input class="FM-input"
                                   id="FM-cap"
                                   name="cap"
                                   type="text"
                                   inputmode="numeric"
                                   maxlength="5"
                                   autocomplete="postal-code"
                                   placeholder="84100"
                                   value="${fn:escapeXml(oldCap)}"
                                   required>

                            <span id="FM-cap-error"
                                  class="FM-inline-error">
                                Il CAP deve contenere esattamente 5 cifre.
                            </span>

                        </div>

                        <div class="FM-form-group">

                            <label class="FM-label"
                                   for="FM-province">
                                Provincia
                            </label>

                            <input class="FM-input"
                                   id="FM-province"
                                   name="provincia"
                                   type="text"
                                   maxlength="2"
                                   autocomplete="address-level1"
                                   placeholder="SA"
                                   value="${fn:escapeXml(oldProvincia)}"
                                   required>

                            <span id="FM-province-error"
                                  class="FM-inline-error">
                                Inserisci una sigla di 2 lettere.
                            </span>

                        </div>

                    </div>

                    <!-- EMAIL -->
                    <div class="FM-form-group">

                        <label class="FM-label"
                               for="FM-register-email">
                            Email
                        </label>

                        <input class="FM-input"
                               id="FM-register-email"
                               name="email"
                               type="email"
                               maxlength="120"
                               autocomplete="email"
                               placeholder="nome@email.it"
                               value="${fn:escapeXml(oldEmail)}"
                               required>

                        <span id="FM-register-email-error"
                              class="FM-inline-error">
                            Inserisci un indirizzo email valido.
                        </span>

                    </div>

                    <!-- PASSWORD -->
                    <div class="FM-form-group">

                        <label class="FM-label"
                               for="FM-register-password">
                            Password
                        </label>

                        <input class="FM-input"
                               id="FM-register-password"
                               name="password"
                               type="password"
                               maxlength="20"
                               autocomplete="new-password"
                               placeholder="Minimo 8 caratteri, una maiuscola e un numero"
                               required>

                        <span id="FM-register-password-error"
                              class="FM-inline-error">
                            Usa 8-20 caratteri, almeno una maiuscola
                            e un numero, senza spazi.
                        </span>

                    </div>

                    <!-- SUBMIT -->
                    <button id="FM-register-button"
                            class="FM-button FM-button-primary FM-button-full"
                            type="submit">

                        Registrati

                    </button>

                </form>

                <p class="FM-auth-links">

                    Hai già un account?

                    <a href="${pageContext.request.contextPath}/login">
                        Accedi
                    </a>

                </p>

            </div>

        </section>

    </div>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp"/>

<script>
    /*creo la funzione che poi verra eseguita immediatamente*/
(() => {

    const contextPath =
        '${pageContext.request.contextPath}';

    /* Riferimento al form di registrazione. */
    const form =
        document.getElementById(
            'FM-registration-form'
        );

    const submitButton =
        document.getElementById(
            'FM-register-button'
        );

    /*REGEX*/

    const namePattern =
        /^[\p{L}][\p{L}' -]{1,49}$/u;

    const cityPattern =
        /^[\p{L}][\p{L}'. -]{1,49}$/u;

    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const passwordPattern =
        /^(?=.*[A-Z])(?=.*\d)\S{8,20}$/;

    const capPattern =
        /^\d{5}$/;

    const provinciaPattern =
        /^[A-Za-z]{2}$/;

    /*CAMPI DEL FORM*/

    const fields = {
        nome:document.getElementById('FM-name'),
        cognome:document.getElementById('FM-surname'),
        email:document.getElementById('FM-register-email'),
        password:document.getElementById('FM-register-password'),
        citta:document.getElementById('FM-city'),
        cap:document.getElementById('FM-cap'),
        provincia:document.getElementById('FM-province')
    };

    /*MESSAGGI DI ERRORE*/

    const errors = {
        nome:document.getElementById('FM-name-error'),
        cognome:document.getElementById('FM-surname-error'),
        email:document.getElementById('FM-register-email-error'),
        password:document.getElementById('FM-register-password-error'),
        citta:document.getElementById('FM-city-error'),
        cap:document.getElementById('FM-cap-error'),
        provincia:document.getElementById('FM-province-error')
    };

    /* Memorizziamo quale email è stata verificata tramite AJAX.*/
    let emailChecked = ''; /*controlla l'ultima mail*/
    let emailAvailable = false; /*uale email è dispnibile*/

    /* GESTIONE ERRORI*/

    function setError(key,message)
    {
        const field = fields[key];
        const error = errors[key];
        const hasError = Boolean(message);

        field.classList.toggle('FM-input-error',hasError);
        field.classList.toggle('FM-input-success',!hasError);
        error.classList.toggle('FM-inline-error-visible',hasError);
        error.textContent = message || '';
    }

    /*VALIDAZIONE LOCALE
    *serve a controllare tutti i campi senza controllare il server*/

    function validateLocalFields() {

        let valid = true;
        const nome =fields.nome.value.trim(); /*trim () elimina spazi all'inizio e alla fine*/
        const cognome =fields.cognome.value.trim();
        const email =fields.email.value.trim().toLowerCase();
        const password =fields.password.value;
        const citta =fields.citta.value.trim();
        const cap =fields.cap.value.trim();
        const provincia =fields.provincia.value.trim();

        /*questi test indicano che se i campi non rispettano la regex allora da errore*/
        /*NOME*/
        if (!namePattern.test(nome)) {
            setError('nome','Inserisci un nome valido.');
            valid = false;
        } else {
            setError('nome','');
        }

        /*COGNOME*/
        if (!namePattern.test(cognome)) {
            setError('cognome','Inserisci un cognome valido.');
            valid = false;
        } else {
            setError('cognome','');
        }

        /*CITTÀ*/
        if (!cityPattern.test(citta)) {
            setError('citta','Inserisci una città valida.');
            valid = false;
        } else {
            setError('citta','');
        }

        /*CAP*/
        if (!capPattern.test(cap)) {
            setError('cap','Il CAP deve contenere esattamente 5 cifre.');
            valid = false;
        } else {
            setError('cap','');
        }

        /* PROVINCIA*/
        if (!provinciaPattern.test(provincia)) {
            setError('provincia','Inserisci una sigla di 2 lettere.');
            valid = false;
        } else {
            setError('provincia','');
        }

        /*EMAIL*/
        if (!emailPattern.test(email)) {
            setError('email','Inserisci un indirizzo email valido.');
            valid = false;
        } else if (emailChecked === email && !emailAvailable) {
            setError('email','Questa email non è disponibile.');
            valid = false;
        } else {
            setError('email','');
        }

        /*PASSWORD*/
        if (!passwordPattern.test(password)) {
            setError('password','Usa 8-20 caratteri, almeno una maiuscola e un numero, senza spazi.');
            valid = false;
        } else {
            setError('password','');
        }
        return valid;
    }

    /* CONTROLLO EMAIL AJAX */
    async function checkEmail() {
        const email = fields.email.value.trim().toLowerCase();

        emailChecked = email;
        emailAvailable = false;

        /* Prima controlliamo il formato dell'email. */
        if (!emailPattern.test(email)) {
            setError('email','Inserisci un indirizzo email valido.');
            return false;
        }

        try {
            const response = await fetch(contextPath + '/check-email?email=' + encodeURIComponent(email),
                {
                    method: 'GET',
                    headers: {'Accept': 'application/json'}
                }
            );

            /* Una risposta HTTP non valida blocca la registrazione. */
            if (!response.ok) {
                setError('email','Impossibile verificare l’email in questo momento.');
                return false;
            }

            const data = await response.json();

            /*Se l'utente ha modificato l'email mentre aspettavamo la risposta, ignoriamo la vecchia risposta.*/
            const currentEmail = fields.email.value.trim().toLowerCase();

            if (currentEmail !== email) {
                return false;
            }

            /*
             * RICORDARE
             * Il VerificaEmailServlet restituisce:
             * {
             *     valida: true/false,
             *     disponibile: true/false
             * }
             */
            emailAvailable =
                data.valida === true &&
                data.disponibile === true;

            if (!emailAvailable) {
                setError('email','Questa email è già registrata.');
                return false;
            }

            setError('email','');
            return true;

        } catch (error) {
            console.error('Errore verifica email:',error);

            /* Se il server non risponde l'email non viene considerata valida. */
            emailAvailable = false;
            setError('email','Impossibile verificare l’email in questo momento.');
            return false;
        }
    }

    /* EMAIL MODIFICATA */
    /*Se l'utente modifica l'email dopo il controllo AJAX,il risultato precedente non è più valido.*/
    fields.email.addEventListener('input',() => {
        emailChecked = '';
        emailAvailable = false;
    });

    /* Controllo AJAX quando l'utente esce dal campo email. */
    fields.email.addEventListener('blur',checkEmail);

    /* SUBMIT */
    form.addEventListener('submit',async event => {
        event.preventDefault();

        /* Prima controlliamo localmente tutti i campi. */
        if (!validateLocalFields()) {
            const firstError = form.querySelector('.FM-input-error');

            if (firstError) {
                firstError.focus();
            }
            return;
        }

        /* Impedisce doppi click mentre aspettiamo la risposta AJAX. */
        submitButton.disabled = true;

        /* Ricontrolliamo l'email prima della registrazione. */
        const emailOk = await checkEmail();

        /*Se l'email è disponibile e tutti i campi sono ancora validi,effettuiamo il normale POST verso /register.*/
        if (emailOk && validateLocalFields()) {
            form.submit();
            return;
        }

        /* In caso di errore riabilitiamo il pulsante. */
        submitButton.disabled = false;

        const firstError = form.querySelector('.FM-input-error');

        if (firstError) {
            firstError.focus();
        }
    });

})();
/*la eseguo immediatamente*/
</script>

</body>

</html>
