<!-- RUOLO DELLA PAGINA: Permette all’Admin di assegnare o rimuovere sconti dai prodotti. -->
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

    <title>Assegna sconti - AthliX Admin</title>

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
                        Sconti dei prodotti
                    </h1>

                    <p class="FM-muted">
                        Assegna o rimuovi una promozione dai prodotti.
                    </p>

                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/admin/discounts">

                    Gestione sconti

                </a>

            </div>

            <c:if test="${not empty adminMessage}">

                <div class="FM-message FM-message-success"
                     role="status">

                    <c:out value="${adminMessage}" />

                </div>

            </c:if>

            <c:choose>

                <c:when test="${empty products}">

                    <div class="FM-empty-state">
                        Nessun prodotto presente.
                    </div>

                </c:when>

                <c:otherwise>

                    <div class="FM-order-list">

                        <c:forEach var="product"
                                   items="${products}">

                            <article class="FM-order-card">

                                <div>

                                    <strong>
                                        <c:out value="${product.nome}" />
                                    </strong>

                                    <p class="FM-muted">
                                        Prodotto #
                                        <c:out value="${product.idProdotto}" />
                                    </p>

                                    <p class="FM-muted">
                                        Categoria:
                                        <c:out value="${product.nomeCategoria}" />
                                    </p>

                                </div>

                                <div>

                                    <strong>
                                        Prezzo
                                    </strong>

                                    <p>

                                        <fmt:formatNumber
                                                value="${product.prezzoFinale}"
                                                type="currency"
                                                currencySymbol="€"
                                                minFractionDigits="2"
                                                maxFractionDigits="2" />

                                    </p>

                                    <c:if test="${not empty product.idSconto}">

                                        <span class="FM-order-status">

                                            Sconto #
                                            <c:out value="${product.idSconto}" />

                                        </span>

                                    </c:if>

                                </div>

                                <form method="post"
                                      action="${pageContext.request.contextPath}/admin/discounts/products"
                                      class="FM-auth-form">

                                    <input type="hidden"
                                           name="idProdotto"
                                           value="${product.idProdotto}">

                                    <div class="FM-form-group">

                                        <label for="sconto-${product.idProdotto}">
                                            Promozione
                                        </label>

                                        <select class="FM-input"
                                                id="sconto-${product.idProdotto}"
                                                name="idSconto">

                                            <option value="">
                                                Nessuno sconto
                                            </option>

                                            <c:forEach var="sconto"
                                                       items="${sconti}">

                                                <option value="${sconto.idSconto}"
                                                        ${product.idSconto == sconto.idSconto ? 'selected' : ''}>

                                                    #<c:out value="${sconto.idSconto}" />
                                                    -
                                                    <fmt:formatNumber
                                                            value="${sconto.percentuale}"
                                                            minFractionDigits="0"
                                                            maxFractionDigits="2" />
                                                    %
                                                    (
                                                    <c:out value="${sconto.stato}" />
                                                    )

                                                </option>

                                            </c:forEach>

                                        </select>

                                    </div>

                                    <button class="FM-button FM-button-primary"
                                            type="submit">
                                        Salva
                                    </button>

                                </form>

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
