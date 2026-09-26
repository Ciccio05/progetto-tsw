<!-- RUOLO DELLA PAGINA: Raccoglie i dati di consegna e conferma l’ordine dopo la validazione client-side. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"
           uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="it">

<head>
    <meta charset="UTF-8">
    <!--rensponsive-->
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Checkout - AthliX</title>
<!--collego il CSS non in maniera fissa cosi se cambia il context root
    JSP costruisce automaticamente il perrcorso corretto-->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="FM-page">

    <!--header-->
<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp"/>

<main class="FM-checkout-page">
    <div class="FM-container">

        <h1 class="FM-page-title">
            Checkout
        </h1>

        <!-- ERRORE CHECKOUT -->
        <c:if test="${not empty checkoutError}">
            <div class="FM-message FM-message-error"
                 role="alert">
                <c:out value="${checkoutError}"/>
            </div>
        </c:if>

        <!--controllo carrello-->
        <c:choose>
            <c:when test="${emptyCart}">

                <!-- CARRELLO VUOTO -->
                <section class="FM-empty-state">
                    <h2>Il tuo carrello è vuoto</h2>

                    <p>
                        Aggiungi almeno un prodotto prima
                        di procedere al checkout.
                    </p>

                    <a class="FM-button FM-button-primary"
                       href="${pageContext.request.contextPath}/catalog">
                        Torna al catalogo
                    </a>
                </section>

            </c:when>

            <c:otherwise>

                <div class="FM-checkout-layout">

                    <!-- FORM CHECKOUT -->
                    <form id="FM-checkout-form"
                          class="FM-checkout-form"
                          action="${pageContext.request.contextPath}/checkout"
                          method="post"
                          novalidate>

                        <h2>Dati di consegna</h2>

                        <div class="FM-form-grid">

                            <!-- NOME -->
                            <div class="FM-form-group">
                                <label class="FM-label"
                                       for="nome">
                                    Nome
                                </label>

                                <!--fn:escape serve a non interpretare eventuale script come HTML-->
                                <input class="FM-input"
                                       id="nome"
                                       name="nome"
                                       type="text"
                                       maxlength="50"
                                       autocomplete="given-name"
                                       placeholder="Es. Mario"
                                       aria-describedby="FM-error-nome"
                                       value="${fn:escapeXml(not empty oldNome ? oldNome : sessionScope.utente.nomeUtente)}"
                                       required>

                                <span id="FM-error-nome"
                                      class="FM-inline-error"></span>
                            </div>

                            <!-- COGNOME -->
                            <div class="FM-form-group">
                                <label class="FM-label"
                                       for="cognome">
                                    Cognome
                                </label>

                                <input class="FM-input"
                                       id="cognome"
                                       name="cognome"
                                       type="text"
                                       maxlength="50"
                                       autocomplete="family-name"
                                       placeholder="Es. Rossi"
                                       aria-describedby="FM-error-cognome"
                                       value="${fn:escapeXml(not empty oldCognome ? oldCognome : sessionScope.utente.cognomeUtente)}"
                                       required>

                                <span id="FM-error-cognome"
                                      class="FM-inline-error"></span>
                            </div>

                            <!-- EMAIL -->
                            <div class="FM-form-group FM-full-width">
                                <label class="FM-label"
                                       for="email">
                                    Email
                                </label>

                                <input class="FM-input"
                                       id="email"
                                       name="email"
                                       type="email"
                                       maxlength="120"
                                       autocomplete="email"
                                       placeholder="nome@email.it"
                                       aria-describedby="FM-error-email"
                                       value="${fn:escapeXml(not empty oldEmail ? oldEmail : sessionScope.utente.email)}"
                                       required>

                                <span id="FM-error-email"
                                      class="FM-inline-error"></span>
                            </div>

                            <!-- INDIRIZZO -->
                            <div class="FM-form-group FM-full-width">
                                <label class="FM-label"
                                       for="indirizzo">
                                    Indirizzo
                                </label>

                                <input class="FM-input"
                                       id="indirizzo"
                                       name="indirizzo"
                                       type="text"
                                       maxlength="150"
                                       autocomplete="street-address"
                                       aria-describedby="FM-error-indirizzo"
                                       value="${fn:escapeXml(not empty oldIndirizzo ? oldIndirizzo : sessionScope.utente.indirizzoUtente)}"
                                       placeholder="Via Roma 123"
                                       required>

                                <span id="FM-error-indirizzo"
                                      class="FM-inline-error"></span>
                            </div>

                            <!-- CITTÀ -->
                            <div class="FM-form-group">
                                <label class="FM-label"
                                       for="citta">
                                    Città
                                </label>

                                <input class="FM-input"
                                       id="citta"
                                       name="citta"
                                       type="text"
                                       maxlength="50"
                                       autocomplete="address-level2"
                                       aria-describedby="FM-error-citta"
                                       value="${fn:escapeXml(oldCitta)}"
                                       placeholder="Salerno"
                                       required>

                                <span id="FM-error-citta"
                                      class="FM-inline-error"></span>
                            </div>

                            <!-- CAP -->
                            <div class="FM-form-group">
                                <label class="FM-label"
                                       for="cap">
                                    CAP
                                </label>

                                <input class="FM-input"
                                       id="cap"
                                       name="cap"
                                       type="text"
                                       inputmode="numeric"
                                       maxlength="5"
                                       autocomplete="postal-code"
                                       aria-describedby="FM-error-cap"
                                       value="${fn:escapeXml(oldCap)}"
                                       placeholder="84100"
                                       required>

                                <span id="FM-error-cap"
                                      class="FM-inline-error"></span>
                            </div>

                            <!-- TELEFONO -->
                            <div class="FM-form-group FM-full-width">
                                <label class="FM-label"
                                       for="telefono">
                                    Telefono
                                </label>

                                <input class="FM-input"
                                       id="telefono"
                                       name="telefono"
                                       type="tel"
                                       maxlength="20"
                                       autocomplete="tel"
                                       aria-describedby="FM-error-telefono"
                                       value="${fn:escapeXml(not empty oldTelefono ? oldTelefono : sessionScope.utente.numeroTelefono)}"
                                       placeholder="3331234567"
                                       required>

                                <span id="FM-error-telefono"
                                      class="FM-inline-error"></span>
                            </div>

                            <!-- METODO DI PAGAMENTO -->
                            <div class="FM-form-group FM-full-width">
                                <label class="FM-label"
                                       for="idMetodoPagamento">
                                    Metodo di pagamento
                                </label>

                                <select class="FM-select"
                                        id="idMetodoPagamento"
                                        name="idMetodoPagamento"
                                        aria-describedby="FM-error-payment"
                                        required>

                                    <option value="">
                                        Seleziona un metodo
                                    </option>

                                    <c:forEach var="method"
                                               items="${paymentMethods}">
                                        <option value="${method.idMetodo}"
                                            ${oldMetodoPagamento == method.idMetodo ? 'selected' : ''}>
                                            <c:out value="${method.tipoCarta}"/>
                                        </option>
                                    </c:forEach>

                                </select>

                                <span id="FM-error-payment"
                                      class="FM-inline-error"></span>
                            </div>

                        </div>

                        <button id="FM-checkout-submit"
                                type="submit"
                                class="FM-button FM-button-primary FM-checkout-submit">
                            Conferma ordine
                        </button>

                    </form>

                    <!-- RIEPILOGO ORDINE -->
                    <aside class="FM-checkout-summary">

                        <h2>Riepilogo ordine</h2>

                        <div class="FM-summary-products">
                            <c:forEach var="item"
                                       items="${cartItems}">

                                <div class="FM-checkout-item">
                                    <span>
                                        <c:out value="${item.nomeProdotto}"/>
                                        ×
                                        <c:out value="${item.quantita}"/>
                                    </span>

                                    <strong>
                                        €
                                        <fmt:formatNumber
                                            value="${item.subtotale}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>
                                    </strong>
                                </div>

                            </c:forEach>
                        </div>

                        <div class="FM-summary-divider"></div>

                        <!-- SUBTOTALE -->
                        <div class="FM-checkout-total-row">
                            <span>Subtotale</span>

                            <strong>
                                €
                                <fmt:formatNumber
                                    value="${subtotal}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            </strong>
                        </div>

                        <!-- SPEDIZIONE -->
                        <div class="FM-checkout-total-row">
                            <span>Spedizione</span>

                            <strong>
                                €
                                <fmt:formatNumber
                                    value="${shipping}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            </strong>
                        </div>

                        <!-- TOTALE -->
                        <div class="FM-total-box">
                            <span class="FM-total-label">
                                Totale
                            </span>

                            <strong class="FM-total-value">
                                €
                                <fmt:formatNumber
                                    value="${total}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            </strong>
                        </div>

                        <a class="FM-button FM-button-light FM-button-full"
                           href="${pageContext.request.contextPath}/cart">
                            Torna al carrello
                        </a>

                    </aside>

                </div>

            </c:otherwise>
        </c:choose>

    </div>
</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp"/>

<script>
    /*IIFE funzione eseguita automaticamente*/
(() => {

    /* Se il carrello è vuoto il form non esiste. */
    const form = document.getElementById('FM-checkout-form');

    if (!form) {
        return;
    }

    const submitButton = document.getElementById('FM-checkout-submit');

    /* REGEX */
    const namePattern = /^[\p{L}][\p{L}' -]{1,49}$/u;
    const cityPattern = /^[\p{L}][\p{L}'. -]{1,49}$/u;
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const capPattern = /^\d{5}$/;
    const phonePattern = /^\+?[0-9 .-]{7,20}$/;

    /* CAMPI */
    const fields = {
        nome: document.getElementById('nome'),
        cognome: document.getElementById('cognome'),
        email: document.getElementById('email'),
        indirizzo: document.getElementById('indirizzo'),
        citta: document.getElementById('citta'),
        cap: document.getElementById('cap'),
        telefono: document.getElementById('telefono'),
        payment: document.getElementById('idMetodoPagamento')
    };

    /* MESSAGGI DI ERRORE */
    const errors = {
        nome: document.getElementById('FM-error-nome'),
        cognome: document.getElementById('FM-error-cognome'),
        email: document.getElementById('FM-error-email'),
        indirizzo: document.getElementById('FM-error-indirizzo'),
        citta: document.getElementById('FM-error-citta'),
        cap: document.getElementById('FM-error-cap'),
        telefono: document.getElementById('FM-error-telefono'),
        payment: document.getElementById('FM-error-payment')
    };

    /* Mostra o rimuove l'errore associato a un campo. */
    function setError(key,message) {
        const field = fields[key];
        const error = errors[key];
        const hasError = Boolean(message);

        field.classList.toggle('FM-input-error',hasError);
        field.classList.toggle('FM-input-success',!hasError);
        field.setAttribute(
            'aria-invalid',
            hasError ? 'true' : 'false'
        );

        error.textContent = message || '';
        error.classList.toggle(
            'FM-inline-error-visible',
            hasError
        );
    }

    /* VALIDAZIONE */
    function validate() {
        let valid = true;

        const nome = fields.nome.value.trim();
        const cognome = fields.cognome.value.trim();
        const email = fields.email.value.trim().toLowerCase();
        const indirizzo = fields.indirizzo.value.trim();
        const citta = fields.citta.value.trim();
        const cap = fields.cap.value.trim();
        const telefono = fields.telefono.value.trim();

        /* NOME */
        if (!namePattern.test(nome)) {
            setError('nome','Inserisci un nome valido.');
            valid = false;
        } else {
            setError('nome','');
        }

        /* COGNOME */
        if (!namePattern.test(cognome)) {
            setError('cognome','Inserisci un cognome valido.');
            valid = false;
        } else {
            setError('cognome','');
        }

        /* EMAIL */
        if (!emailPattern.test(email)) {
            setError('email','Inserisci un indirizzo email valido.');
            valid = false;
        } else {
            setError('email','');
        }

        /* INDIRIZZO */
        if (indirizzo.length < 3 || indirizzo.length > 150) {
            setError('indirizzo','Inserisci un indirizzo valido.');
            valid = false;
        } else {
            setError('indirizzo','');
        }

        /* CITTÀ */
        if (!cityPattern.test(citta)) {
            setError('citta','Inserisci una città valida.');
            valid = false;
        } else {
            setError('citta','');
        }

        /* CAP */
        if (!capPattern.test(cap)) {
            setError('cap','Il CAP deve contenere esattamente 5 cifre.');
            valid = false;
        } else {
            setError('cap','');
        }

        /* TELEFONO */
        if (!phonePattern.test(telefono)) {
            setError('telefono','Inserisci un numero di telefono valido.');
            valid = false;
        } else {
            setError('telefono','');
        }

        /* METODO DI PAGAMENTO */
        if (!fields.payment.value) {
            setError('payment','Seleziona un metodo di pagamento.');
            valid = false;
        } else {
            setError('payment','');
        }

        return valid;
    }

    /* SUBMIT */
    form.addEventListener('submit',event => {

        if (!validate()) {
            event.preventDefault();

            const firstError = form.querySelector('.FM-input-error');

            if (firstError) {
                firstError.focus();
            }

            return;
        }

        /* Evita doppi click mentre viene creato l'ordine. */
        submitButton.disabled = true;
    });

    /*Quando l'utente modifica un camporimuoviamo il vecchio stato di errore*/
    Object.keys(fields).forEach(key => {
        const field = fields[key];

        const eventName =
            field.tagName === 'SELECT'
                ? 'change'
                : 'input';

        field.addEventListener(eventName,() => {
            field.classList.remove(
                'FM-input-error',
                'FM-input-success'
            );

            field.removeAttribute('aria-invalid');

            errors[key].textContent = '';
            errors[key].classList.remove(
                'FM-inline-error-visible'
            );
        });
    });

})();
</script>

</body>
</html>
