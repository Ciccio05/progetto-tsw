<!-- RUOLO DELLA PAGINA: Mostra l’elenco completo dei prodotti nell’area Admin. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione prodotti - AthliX</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="FM-page">

<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main class="FM-main">
    <div class="FM-container FM-account-container">
        <section class="FM-account-card">
            <div class="FM-account-header">
                <div>
                    <h1 class="FM-section-title">Gestione prodotti</h1>
                    <p class="FM-muted">Catalogo amministrativo AthliX.</p>
                </div>

                <div>
                    <a class="FM-button FM-button-light"
                       href="${pageContext.request.contextPath}/admin">
                        Pannello admin
                    </a>
                    <a class="FM-button FM-button-primary"
                       href="${pageContext.request.contextPath}/admin/products/new">
                        Nuovo prodotto
                    </a>
                </div>
            </div>

            <c:if test="${not empty sessionScope.adminMessage}">
                <div class="FM-message FM-message-success" role="status">
                    <c:out value="${sessionScope.adminMessage}" />
                </div>
                <c:remove var="adminMessage" scope="session" />
            </c:if>

            <c:if test="${not empty sessionScope.adminError}">
                <div class="FM-message FM-message-error" role="alert">
                    <c:out value="${sessionScope.adminError}" />
                </div>
                <c:remove var="adminError" scope="session" />
            </c:if>

            <c:choose>
                <c:when test="${empty products}">
                    <div class="FM-empty-state">Nessun prodotto presente.</div>
                </c:when>

                <c:otherwise>
                    <div class="FM-order-list">
                        <c:forEach var="product" items="${products}">
                            <article class="FM-order-card">
                                <div>
                                    <img class="FM-admin-product-thumb"
                                         src="${pageContext.request.contextPath}${empty product.immagine
                                                ? '/images/products/default-product.svg'
                                                : fn:escapeXml(product.immagine)}"
                                         alt="Foto di ${fn:escapeXml(product.nome)}">

                                    <strong><c:out value="${product.nome}" /></strong>
                                    <p class="FM-muted">
                                        Categoria: <c:out value="${product.nomeCategoria}" />
                                    </p>
                                    <p class="FM-muted">
                                        Stock: <c:out value="${product.quantita}" />
                                        —
                                        <c:choose>
                                            <c:when test="${not product.visibile}">Nascosto</c:when>
                                            <c:when test="${product.disponibilita && product.quantita > 0}">Disponibile</c:when>
                                            <c:otherwise>Non disponibile</c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>

                                <div class="FM-order-total">
                                    <fmt:formatNumber value="${product.prezzo}"
                                                      type="currency"
                                                      currencySymbol="€"
                                                      minFractionDigits="2"
                                                      maxFractionDigits="2" />
                                </div>

                                <div>
                                    <c:url var="editUrl" value="/admin/products/edit">
                                        <c:param name="id" value="${product.idProdotto}" />
                                    </c:url>

                                    <a class="FM-button FM-button-light" href="${editUrl}">
                                        Modifica
                                    </a>

                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/products/delete"
                                          onsubmit="return confirm('Confermi la rimozione di questo prodotto?');">
                                        <input type="hidden"
                                               name="idProdotto"
                                               value="${product.idProdotto}">
                                        <button class="FM-button FM-button-light" type="submit">
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
