<!-- RUOLO DELLA PAGINA: Mostra l’elenco degli sconti e le relative azioni amministrative. -->
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

    <title>Sconti e promozioni - AthliX Admin</title>

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
                        Sconti e promozioni
                    </h1>

                    <p class="FM-muted">
                        Crea, modifica e assegna le promozioni ai prodotti.
                    </p>

                </div>

                <div class="FM-page-actions">

                    <a class="FM-button FM-button-light"
                       href="${pageContext.request.contextPath}/admin">
                        Pannello admin
                    </a>

                    <a class="FM-button FM-button-secondary"
                       href="${pageContext.request.contextPath}/admin/discounts/products">
                        Assegna ai prodotti
                    </a>

                    <a class="FM-button FM-button-primary"
                       href="${pageContext.request.contextPath}/admin/discounts/new">
                        Nuovo sconto
                    </a>

                </div>

            </div>

            <c:if test="${not empty adminMessage}">

                <div class="FM-message FM-message-success"
                     role="status">

                    <c:out value="${adminMessage}" />

                </div>

            </c:if>

            <c:choose>

                <c:when test="${empty sconti}">

                    <div class="FM-empty-state">

                        <h2>
                            Nessuno sconto presente
                        </h2>

                        <p>
                            Crea la prima promozione per AthliX.
                        </p>

                    </div>

                </c:when>

                <c:otherwise>

                    <div class="FM-order-list">

                        <c:forEach var="sconto"
                                   items="${sconti}">

                            <article class="FM-order-card">

                                <div>

                                    <strong>
                                        Sconto #
                                        <c:out value="${sconto.idSconto}" />
                                    </strong>

                                    <p class="FM-muted">

                                        Dal

                                        <fmt:formatDate
                                                value="${sconto.dataInizio}"
                                                pattern="dd/MM/yyyy" />

                                        al

                                        <fmt:formatDate
                                                value="${sconto.dataFine}"
                                                pattern="dd/MM/yyyy" />

                                    </p>

                                    <span class="FM-order-status">
                                        <c:out value="${sconto.stato}" />
                                    </span>

                                </div>

                                <div class="FM-order-total">

                                    <fmt:formatNumber
                                            value="${sconto.percentuale}"
                                            minFractionDigits="0"
                                            maxFractionDigits="2" />

                                    %

                                </div>

                                <div class="FM-page-actions">

                                    <c:url var="editUrl"
                                           value="/admin/discounts/edit">

                                        <c:param
                                                name="id"
                                                value="${sconto.idSconto}" />

                                    </c:url>

                                    <a class="FM-button FM-button-secondary"
                                       href="${editUrl}">
                                        Modifica
                                    </a>

                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/discounts/delete"
                                          class="FM-inline-form"
                                          onsubmit="return confirm('Eliminare questo sconto? Verrà rimosso anche dai prodotti associati.');">

                                        <input type="hidden"
                                               name="id"
                                               value="${sconto.idSconto}">

                                        <button class="FM-button FM-button-light"
                                                type="submit">
                                            Elimina
                                        </button>

                                    </form>

                                </div>

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
