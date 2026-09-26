<!-- RUOLO DELLA PAGINA: Mostra e gestisce il dettaglio di un ordine. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Dettaglio ordine - AthliX</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page">

<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main class="FM-main">

    <div class="FM-container FM-account-container">

        <!-- =====================================================
             INFORMAZIONI GENERALI ORDINE
             ===================================================== -->

        <section class="FM-account-card">

            <div class="FM-account-header">

                <div>

                    <h1 class="FM-section-title">

                        Ordine #
                        <c:out value="${ordine.idOrdine}" />

                    </h1>

                    <p class="FM-muted">

                        Effettuato il

                        <fmt:formatDate
                                value="${ordine.dataOrdine}"
                                pattern="dd/MM/yyyy" />

                    </p>

                </div>

                <span class="FM-order-status">

                    <c:out value="${ordine.stato}" />

                </span>

            </div>

            <div class="FM-account-grid">

                <div class="FM-account-info">

                    <strong>
                        Totale ordine
                    </strong>

                    <span class="FM-order-total">

                        <fmt:formatNumber
                                value="${ordine.totale}"
                                type="currency"
                                currencySymbol="€"
                                minFractionDigits="2"
                                maxFractionDigits="2" />

                    </span>

                </div>

                <div class="FM-account-info">

                    <strong>
                        Stato spedizione
                    </strong>

                    <span>

                        <c:choose>

                            <c:when test="${not empty ordine.statoSpedizione}">

                                <c:out value="${ordine.statoSpedizione}" />

                            </c:when>

                            <c:otherwise>

                                Non disponibile

                            </c:otherwise>

                        </c:choose>

                    </span>

                </div>

                <div class="FM-account-info">

                    <strong>
                        Indirizzo di spedizione
                    </strong>

                    <span>

                        <c:choose>

                            <c:when test="${not empty ordine.indirizzoSpedizione}">

                                <c:out value="${ordine.indirizzoSpedizione}" />

                            </c:when>

                            <c:otherwise>

                                Non disponibile

                            </c:otherwise>

                        </c:choose>

                    </span>

                </div>

            </div>

        </section>

        <!-- =====================================================
             TRACCIAMENTO SPEDIZIONE
             ===================================================== -->

        <c:choose>
            <c:when test="${ordine.annullato}">
                <section class="FM-account-card">
                    <h2 class="FM-section-title">Ordine annullato</h2>
                    <div class="FM-message FM-message-error" role="status">
                        Questo ordine è stato annullato. La spedizione non proseguirà.
                    </div>
                </section>
            </c:when>

            <c:otherwise>
                <section class="FM-account-card">

                    <h2 class="FM-section-title">
                        Tracciamento spedizione
                    </h2>

                    <div class="FM-account-grid">
                        <div class="FM-account-info">
                            <strong>1. Ordine confermato</strong>
                            <span>Completato</span>
                        </div>

                        <div class="FM-account-info">
                            <strong>2. Preparazione</strong>
                            <span>
                                <c:choose>
                                    <c:when test="${ordine.spedito or ordine.consegnato}">Completata</c:when>
                                    <c:otherwise>In corso</c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="FM-account-info">
                            <strong>3. Spedizione</strong>
                            <span>
                                <c:choose>
                                    <c:when test="${ordine.spedito}">
                                        Spedito
                                        <c:if test="${not empty ordine.dataPartenza}">
                                            il <fmt:formatDate value="${ordine.dataPartenza}" pattern="dd/MM/yyyy" />
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>Non ancora spedito</c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="FM-account-info">
                            <strong>4. Consegna</strong>
                            <span>
                                <c:choose>
                                    <c:when test="${ordine.consegnato}">
                                        Consegnato
                                        <c:if test="${not empty ordine.dataConsegna}">
                                            il <fmt:formatDate value="${ordine.dataConsegna}" pattern="dd/MM/yyyy" />
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>Non ancora consegnato</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>

                </section>
            </c:otherwise>
        </c:choose>

        <!-- =====================================================
             PRODOTTI DELL'ORDINE
             ===================================================== -->

        <section class="FM-account-card">

            <h2 class="FM-section-title">
                Prodotti acquistati
            </h2>

            <c:choose>

                <c:when test="${empty dettaglio}">

                    <div class="FM-empty-state">

                        Nessun prodotto associato a questo ordine.

                    </div>

                </c:when>

                <c:otherwise>

                    <div class="FM-order-list">

                        <c:forEach var="item"
                                   items="${dettaglio}">

                            <article class="FM-order-card">

                                <!-- PRODOTTO -->

                                <div>

                                    <strong>

                                        <c:out value="${item.nomeProdotto}" />

                                    </strong>

                                    <p class="FM-muted">

                                        Quantità:

                                        <c:out value="${item.quantita}" />

                                    </p>

                                </div>

                                <!-- PREZZO E IVA -->

                                <div>

                                    <strong>
                                        Prezzo unitario
                                    </strong>

                                    <br>

                                    <fmt:formatNumber
                                            value="${item.prezzo}"
                                            type="currency"
                                            currencySymbol="€"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />

                                    <div class="FM-muted">

                                        IVA:

                                        <fmt:formatNumber
                                                value="${item.iva}"
                                                minFractionDigits="0"
                                                maxFractionDigits="2" />

                                        %

                                    </div>

                                </div>

                                <!-- SUBTOTALE -->

                                <div class="FM-order-total">

                                    <fmt:formatNumber
                                            value="${item.subtotale}"
                                            type="currency"
                                            currencySymbol="€"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />

                                </div>

                            </article>

                        </c:forEach>

                    </div>

                </c:otherwise>

            </c:choose>

            <!-- =================================================
                 AZIONI
                 ================================================= -->

            <div class="FM-page-actions">

                <!-- TORNA AGLI ORDINI -->

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/orders">

                    Torna ai miei ordini

                </a>

                <!-- STAMPA RIEPILOGO -->

                <c:url var="printUrl"
                       value="/order-print">

                    <c:param
                            name="id"
                            value="${ordine.idOrdine}" />

                </c:url>

                <a class="FM-button FM-button-primary"
                   href="${printUrl}"
                   target="_blank"
                   rel="noopener">

                    Stampa riepilogo

                </a>

                <!-- CONTINUA GLI ACQUISTI -->

                <a class="FM-button FM-button-secondary"
                   href="${pageContext.request.contextPath}/catalog">

                    Continua gli acquisti

                </a>

            </div>

        </section>

    </div>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />

</body>
</html>
