<!-- RUOLO DELLA PAGINA: Mostra il dettaglio del prodotto, l’acquisto e la gestione delle recensioni. -->
<!--DIRETTIVE-->
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
    <!--per il responsive sui mobile-->
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title><c:out value="${product.nome}"/> - AthliX</title>

<!--collego il CSS non in maniera fissa cosi se cambia il context root
    JSP costruisce automaticamente il perrcorso corretto-->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="FM-page">

    <!--header-->
<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp"/>

<main class="FM-main">
    <div class="FM-container">

        <!-- RITORNO AL CATALOGO -->
        <section class="FM-section">
            <a class="FM-button FM-button-light"
               href="${pageContext.request.contextPath}/catalog">
                Torna al catalogo
            </a>
        </section>

        <!-- DETTAGLIO PRODOTTO -->
        <section class="FM-product-detail">

            <!-- IMMAGINE -->
            <div class="FM-product-detail-image">
                <!--equivale a if/else JSTL-->
                <img src="${pageContext.request.contextPath}${empty product.immagine
                        ? '/images/products/default-product.svg'
                        : fn:escapeXml(product.immagine)}"
                     alt="${fn:escapeXml(product.nome)}">
            </div>

            <!-- INFORMAZIONI -->
            <div class="FM-product-detail-info">

                <p class="FM-product-category">
                    <c:out value="${product.nomeCategoria}"/>
                </p>

                <h1 class="FM-section-title">
                    <c:out value="${product.nome}"/>
                </h1>

                <p class="FM-product-description">
                    <c:out value="${product.descrizione}"/>
                </p>

                <!-- PREZZO E SCONTO -->
                <c:choose>
                    <c:when test="${product.disponibilita && product.quantita > 0 && product.percentualeSconto > 0}">

                    <!--crea il badge dello sconto-->
                        <span class="FM-discount-badge">
                            -<fmt:formatNumber
                                value="${product.percentualeSconto}"
                                maxFractionDigits="0"/>%
                        </span>

                        <!--crea il blocco del prezzo -->
                        <div class="FM-product-price-block">
                            <span class="FM-price-old">
                                € <fmt:formatNumber
                                    value="${product.prezzo}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            </span>

                            <!--crea il prezzo effettivo con sconto-->
                            <span class="FM-price">
                                € <fmt:formatNumber
                                    value="${product.prezzoFinale}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            </span>
                        </div>

                    </c:when>

                    <c:otherwise>
                        <div class="FM-product-price-block">
                            <span class="FM-price">
                                € <fmt:formatNumber
                                    value="${product.prezzo}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            </span>
                        </div>
                    </c:otherwise>
                </c:choose>

                <!-- IVA -->
                <p class="FM-muted">
                    IVA:
                    <fmt:formatNumber
                        value="${product.iva}"
                        maxFractionDigits="2"/>%
                </p>

                <!-- DISPONIBILITÀ -->
                <c:choose>
                    <c:when test="${product.disponibilita && product.quantita > 0}">
                        <p class="FM-stock FM-stock-available">
                            Disponibile - ${product.quantita} pezzi
                        </p>
                    </c:when>

                    <c:otherwise>
                        <p class="FM-stock FM-stock-unavailable">
                            Non disponibile
                        </p>
                    </c:otherwise>
                </c:choose>

                <!-- UTENTE NON LOGGATO -->
                <c:if test="${product.disponibilita
                              && product.quantita > 0
                              && empty sessionScope.utente}">
                    <a class="FM-button FM-button-primary"
                       href="${pageContext.request.contextPath}/login?redirect=cart">
                        Accedi per acquistare
                    </a>
                </c:if>

                <!-- CLIENTE LOGGATO -->
                <c:if test="${product.disponibilita
                              && product.quantita > 0
                              && not empty sessionScope.utente
                              && not sessionScope.utente.admin}">

                    <form method="post"
                          action="${pageContext.request.contextPath}/cart"
                          class="FM-auth-form">

                        <input type="hidden"
                               name="action"
                               value="add">

                        <input type="hidden"
                               name="idProdotto"
                               value="${product.idProdotto}">

                        <div class="FM-form-group">
                            <label for="quantita">
                                Quantità
                            </label>

                            <input class="FM-input"
                                   id="quantita"
                                   name="quantita"
                                   type="number"
                                   min="1"
                                   max="${product.quantita}"
                                   value="1"
                                   required>
                        </div>

                        <button class="FM-button FM-button-primary"
                                type="submit">
                            Aggiungi al carrello
                        </button>
                    </form>
                </c:if>

                <!-- VALUTAZIONE MEDIA, deve esistere almeno 1 recensione-->
                <div class="FM-section">
                    <strong>Valutazione media:</strong>

                    <c:choose>
                        <c:when test="${numeroRecensioni > 0}">
                            <fmt:formatNumber
                                value="${mediaRecensioni}"
                                minFractionDigits="1"
                                maxFractionDigits="1"/>
                            / 5
                            (${numeroRecensioni}
                            ${numeroRecensioni == 1 ? 'recensione' : 'recensioni'})
                        </c:when>

                        <c:otherwise>
                            Nessuna recensione
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </section>

        <!-- RECENSIONI -->
        <section class="FM-section" id="FM-reviews">

            <h2 class="FM-section-title">
                Recensioni
            </h2>

            <!-- MESSAGGI -->
            <c:if test="${not empty reviewMessage}">
                <div class="FM-message FM-message-success"
                     role="status">
                    <c:out value="${reviewMessage}"/>
                </div>
            </c:if>

            <c:if test="${not empty reviewError}">
                <div class="FM-message FM-message-error"
                     role="alert">
                    <c:out value="${reviewError}"/>
                </div>
            </c:if>

            <!-- FORM RECENSIONE -->
            <c:if test="${not empty sessionScope.utente
                          && not sessionScope.utente.admin
                          && canReview}">

                <section class="FM-account-card">

                    <h3>
                        <c:choose>
                            <c:when test="${not empty userReview}">
                                Modifica la tua recensione
                            </c:when>

                            <c:otherwise>
                                Scrivi una recensione
                            </c:otherwise>
                        </c:choose>
                    </h3>

                    <form method="post"
                          action="${pageContext.request.contextPath}/review"
                          class="FM-auth-form"
                          id="FM-review-form"
                          novalidate>

                        <input type="hidden"
                               name="idProdotto"
                               value="${product.idProdotto}">

                        <!-- VOTO -->
                        <div class="FM-form-group">
                            <label for="voto">
                                Voto
                            </label>

                            <select class="FM-input"
                                    id="voto"
                                    name="voto"
                                    aria-describedby="votoError"
                                    required>

                                <option value="">
                                    Seleziona
                                </option>

                                <c:forEach begin="1"
                                           end="5"
                                           var="rating">

                                    <option value="${rating}"
                                            ${not empty userReview && userReview.voto == rating
                                              ? 'selected'
                                              : ''}>
                                        ${rating} / 5
                                    </option>

                                </c:forEach>
                            </select>

                            <span class="FM-inline-error"
                                  id="votoError"></span>
                        </div>

                        <!-- COMMENTO -->
                        <div class="FM-form-group">
                            <label for="commento">
                                Commento
                            </label>

                            <textarea class="FM-input"
                                      id="commento"
                                      name="commento"
                                      minlength="3"
                                      maxlength="1000"
                                      rows="5"
                                      aria-describedby="commentoError"
                                      required
                                      placeholder="Racconta la tua esperienza con questo prodotto..."><c:out value="${userReview.commento}"/></textarea>

                            <span class="FM-inline-error"
                                  id="commentoError"></span>
                        </div>

                        <button class="FM-button FM-button-primary"
                                type="submit">
                            Salva recensione
                        </button>

                    </form>
                </section>
            </c:if>

            <!-- CLIENTE SENZA ACQUISTO -->
            <c:if test="${not empty sessionScope.utente
                          && not sessionScope.utente.admin
                          && not canReview}">
                <p class="FM-muted">
                    Potrai recensire questo prodotto dopo averlo acquistato.
                </p>
            </c:if>

            <!-- UTENTE NON LOGGATO -->
            <c:if test="${empty sessionScope.utente}">
                <p class="FM-muted">
                    Accedi per recensire i prodotti che hai acquistato.
                </p>
            </c:if>

            <!-- ELENCO RECENSIONI -->
            <c:choose>
                <c:when test="${empty recensioni}">
                    <div class="FM-empty-state">
                        Non ci sono ancora recensioni per questo prodotto.
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="FM-order-list">

                        <c:forEach var="recensione"
                                   items="${recensioni}">

                            <article class="FM-account-card">

                                <div class="FM-account-header">
                                    <div>
                                        <strong>
                                            <c:out value="${recensione.nomeUtente}"/>
                                            <c:out value="${recensione.cognomeUtente}"/>
                                        </strong>

                                        <p class="FM-muted">
                                            <fmt:formatDate
                                                value="${recensione.dataRecensione}"
                                                pattern="dd/MM/yyyy"/>
                                        </p>
                                    </div>

                                    <span class="FM-order-status">
                                        ${recensione.voto} / 5
                                    </span>
                                </div>

                                <p>
                                    <c:out value="${recensione.commento}"/>
                                </p>

                            </article>

                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

        </section>

    </div>
</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp"/>

<script>
    /*funzione eseguita immediatamente per evitare di usare variabili globali(IIFE)*/
(() => {

    /* Recuperiamo il form recensione. */
    const form = document.getElementById('FM-review-form');

    /* Il form potrebbe non esistere se l'utente non può recensire. */
    if (!form) {
        return;
    }

    const voto = document.getElementById('voto');
    const commento = document.getElementById('commento');

    /* Mostra o rimuove l'errore associato a un campo. */
    function setError(input,message) {
        const error = document.getElementById(input.id + 'Error');
        const hasError = Boolean(message);

        input.classList.toggle('FM-input-error',hasError);
        input.classList.toggle('FM-input-success',!hasError);
        input.setAttribute( 'aria-invalid', hasError ? 'true' : 'false');

        error.textContent = message || '';
        error.classList.toggle( 'FM-inline-error-visible',hasError);
    }

    /* Validazione prima dell'invio della recensione. */
    form.addEventListener('submit',event => {

        let valid = true;
        let firstInvalid = null;

        /* VOTO */
        const rating = Number(voto.value);

        if (!Number.isInteger(rating) || rating < 1  || rating > 5) {

            setError(voto,'Seleziona un voto tra 1 e 5.');
            valid = false;
            if (!firstInvalid) {
                firstInvalid = voto;
            }
        } else {
            setError(voto,'');
        }

        /* COMMENTO */
        const text = commento.value.trim();

        if (text.length < 3 || text.length > 1000) {

            setError(commento,'Il commento deve contenere tra 3 e 1000 caratteri.');
            valid = false;

            if (!firstInvalid) {
                firstInvalid = commento;
            }

        } else {
            setError(commento,'');
            commento.value = text;
        }

        /* Se ci sono errori blocchiamo il POST. */
        if (!valid) {
            event.preventDefault();

            if (firstInvalid) {
                firstInvalid.focus();
            }
        }
    });

})();
</script>

</body>
</html>
