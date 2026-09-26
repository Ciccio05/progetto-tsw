<!-- RUOLO DELLA PAGINA: Mostra lo storico degli ordini del cliente. -->
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

    <title>I miei ordini - AthliX</title>

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
                        I miei ordini
                    </h1>

                    <p class="FM-muted">
                        Consulta gli ordini effettuati su AthliX.
                    </p>

                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/account">

                    Area personale

                </a>

            </div>

            <c:choose>

                <c:when test="${empty ordini}">

                    <div class="FM-empty-state">

                        <h2>
                            Nessun ordine trovato
                        </h2>

                        <p>
                            Non hai ancora effettuato alcun ordine.
                        </p>

                        <a class="FM-button FM-button-primary"
                           href="${pageContext.request.contextPath}/catalog">

                            Vai al catalogo

                        </a>

                    </div>

                </c:when>

                <c:otherwise>

                    <div class="FM-order-list">

                        <c:forEach var="ordine"
                                   items="${ordini}">

                            <article class="FM-order-card">

                                <div>

                                    <strong>
                                        Ordine #
                                        <c:out value="${ordine.idOrdine}" />
                                    </strong>

                                    <p class="FM-muted">

                                        Effettuato il

                                        <fmt:formatDate
                                                value="${ordine.dataOrdine}"
                                                pattern="dd/MM/yyyy" />

                                    </p>

                                    <span class="FM-order-status">

                                        <c:out value="${ordine.stato}" />

                                    </span>

                                </div>

                                <div class="FM-order-total">

                                    <fmt:formatNumber
                                            value="${ordine.totale}"
                                            type="currency"
                                            currencySymbol="€"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />

                                </div>

                                <c:url var="detailUrl"
                                       value="/order-detail">

                                    <c:param
                                            name="id"
                                            value="${ordine.idOrdine}" />

                                </c:url>

                                <a class="FM-button FM-button-secondary"
                                   href="${detailUrl}">

                                    Dettagli

                                </a>

                            </article>

                        </c:forEach>

                    </div>

                </c:otherwise>

            </c:choose>

        </section>

    </div>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />

</body>

</html>
