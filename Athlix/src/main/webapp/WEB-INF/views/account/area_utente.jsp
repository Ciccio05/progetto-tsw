<!-- RUOLO DELLA PAGINA: Mostra il riepilogo dell’account e i collegamenti alle funzioni personali. -->
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

    <title>Area personale - AthliX</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page">

<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main class="FM-main">

    <div class="FM-container FM-account-container">

        <c:if test="${not empty profileMessage}">

            <div class="FM-message FM-message-success"
                 role="status">

                <c:out value="${profileMessage}" />

            </div>

        </c:if>

        <section class="FM-account-card">

            <div class="FM-account-header">

                <div>

                    <h1 class="FM-section-title">
                        Area personale
                    </h1>

                    <p class="FM-muted">

                        Ciao
                        <c:out value="${sessionScope.utente.nomeUtente}" />.

                    </p>

                </div>

                <div class="FM-page-actions">

                    <a class="FM-button FM-button-secondary"
                       href="${pageContext.request.contextPath}/account/edit">
                        Modifica profilo
                    </a>

                    <a class="FM-button FM-button-light"
                       href="${pageContext.request.contextPath}/account/password">
                        Cambia password
                    </a>

                    <a class="FM-button FM-button-light"
                       href="${pageContext.request.contextPath}/logout">
                        Esci
                    </a>

                </div>

            </div>

            <div class="FM-account-grid">

                <div class="FM-account-info">

                    <strong>
                        Nome
                    </strong>

                    <span>
                        <c:out value="${sessionScope.utente.nomeUtente}" />
                    </span>

                </div>

                <div class="FM-account-info">

                    <strong>
                        Cognome
                    </strong>

                    <span>
                        <c:out value="${sessionScope.utente.cognomeUtente}" />
                    </span>

                </div>

                <div class="FM-account-info">

                    <strong>
                        Email
                    </strong>

                    <span>
                        <c:out value="${sessionScope.utente.email}" />
                    </span>

                </div>

                <div class="FM-account-info">

                    <strong>
                        Data di nascita
                    </strong>

                    <span>

                        <c:choose>

                            <c:when test="${not empty sessionScope.utente.dataNascita}">

                                <fmt:formatDate
                                        value="${sessionScope.utente.dataNascita}"
                                        pattern="dd/MM/yyyy" />

                            </c:when>

                            <c:otherwise>
                                Non specificata
                            </c:otherwise>

                        </c:choose>

                    </span>

                </div>

                <div class="FM-account-info">

                    <strong>
                        Indirizzo
                    </strong>

                    <span>

                        <c:choose>
                            <c:when test="${not empty sessionScope.utente.indirizzoUtente}">
                                <c:out value="${sessionScope.utente.indirizzoUtente}" />
                            </c:when>
                            <c:otherwise>
                                Non specificato
                            </c:otherwise>
                        </c:choose>

                    </span>

                </div>

                <div class="FM-account-info">

                    <strong>
                        Telefono
                    </strong>

                    <span>

                        <c:choose>
                            <c:when test="${not empty sessionScope.utente.numeroTelefono}">
                                <c:out value="${sessionScope.utente.numeroTelefono}" />
                            </c:when>
                            <c:otherwise>
                                Non specificato
                            </c:otherwise>
                        </c:choose>

                    </span>

                </div>

            </div>

        </section>

        <section class="FM-account-card">

            <div class="FM-account-header">

                <div>

                    <h2 class="FM-section-title">
                        I miei ordini
                    </h2>

                    <p class="FM-muted">
                        Gli ultimi ordini effettuati su AthliX.
                    </p>

                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/orders">
                    Visualizza tutti
                </a>

            </div>

            <c:choose>

                <c:when test="${empty ordini}">

                    <div class="FM-empty-state">

                        <p>
                            Non hai ancora effettuato ordini.
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
