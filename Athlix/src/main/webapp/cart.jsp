<!-- RUOLO DELLA PAGINA: Mostra il carrello del cliente e le azioni per modificare quantità o rimuovere prodotti. -->
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

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Carrello - AthliX</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page">

<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp"/>

<main class="FM-main">

    <div class="FM-container">

        <h1 class="FM-section-title">
            Il tuo carrello
        </h1>

        <!-- MESSAGGIO DI SUCCESSO -->
        <c:if test="${not empty sessionScope.cartMessage}">

            <div class="FM-message FM-message-success"
                 role="status">

                <c:out value="${sessionScope.cartMessage}"/>

            </div>

            <c:remove var="cartMessage"
                      scope="session"/>

        </c:if>

        <!-- MESSAGGIO DI ERRORE -->
        <c:if test="${not empty sessionScope.cartError}">

            <div class="FM-message FM-message-error"
                 role="alert">

                <c:out value="${sessionScope.cartError}"/>

            </div>

            <c:remove var="cartError"
                      scope="session"/>

        </c:if>

        <c:choose>

            <c:when test="${empty cartItems}">

                <section class="FM-empty-state">

                    <h2>
                        Il carrello è vuoto
                    </h2>

                    <p>
                        Non hai ancora aggiunto prodotti al carrello.
                    </p>

                    <a class="FM-button FM-button-primary"
                       href="${pageContext.request.contextPath}/catalog">

                        Torna al catalogo

                    </a>

                </section>

            </c:when>

            <c:otherwise>

                <div class="FM-account-container">

                    <!-- PRODOTTI -->
                    <section class="FM-account-card">

                        <h2 class="FM-section-title">
                            Prodotti nel carrello
                        </h2>

                        <div class="FM-order-list">

                            <c:forEach var="item"
                                       items="${cartItems}">

                                <article class="FM-order-card">

                                    <!-- INFORMAZIONI PRODOTTO -->
                                    <div class="FM-cart-product-info">

                                        <a class="FM-cart-product-image-link"
                                           href="${pageContext.request.contextPath}/product?id=${item.idProdotto}"
                                           aria-label="Apri ${fn:escapeXml(item.nomeProdotto)}">
                                            <img class="FM-cart-product-image"
                                                 src="${pageContext.request.contextPath}${empty item.immagine ? '/images/products/default-product.svg' : fn:escapeXml(item.immagine)}"
                                                 alt="${fn:escapeXml(item.nomeProdotto)}"
                                                 loading="lazy">
                                        </a>

                                        <h3 class="FM-product-name">

                                            <c:out value="${item.nomeProdotto}"/>

                                        </h3>

                                        <p class="FM-muted">

                                            ID prodotto:
                                            <c:out value="${item.idProdotto}"/>

                                        </p>

                                        <c:if test="${item.percentualeSconto != null
                                                    && item.percentualeSconto > 0}">

                                            <span class="FM-discount-badge">

                                                -
                                                <fmt:formatNumber
                                                        value="${item.percentualeSconto}"
                                                        maxFractionDigits="0"/>%

                                            </span>

                                        </c:if>

                                    </div>

                                    <!-- PREZZO E QUANTITÀ -->
                                    <div>

                                        <p>

                                            Prezzo:

                                            <strong>

                                                €
                                                <fmt:formatNumber
                                                        value="${item.prezzoScontato}"
                                                        minFractionDigits="2"
                                                        maxFractionDigits="2"/>

                                            </strong>

                                        </p>

                                        <p>

                                            Subtotale:

                                            <strong class="FM-order-total">

                                                €
                                                <fmt:formatNumber
                                                        value="${item.subtotale}"
                                                        minFractionDigits="2"
                                                        maxFractionDigits="2"/>

                                            </strong>

                                        </p>

                                        <!-- QUANTITÀ -->
                                        <div class="FM-filter-actions">

                                            <!-- DIMINUISCI -->
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/cart">

                                                <input type="hidden"
                                                       name="action"
                                                       value="decrease">

                                                <input type="hidden"
                                                       name="idProdotto"
                                                       value="${item.idProdotto}">

                                                <button class="FM-button FM-button-light"
                                                        type="submit"
                                                        aria-label="Diminuisci quantità">

                                                    −

                                                </button>

                                            </form>

                                            <strong>

                                                Quantità:
                                                <c:out value="${item.quantita}"/>

                                            </strong>

                                            <!-- AUMENTA -->
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/cart">

                                                <input type="hidden"
                                                       name="action"
                                                       value="increase">

                                                <input type="hidden"
                                                       name="idProdotto"
                                                       value="${item.idProdotto}">

                                                <button class="FM-button FM-button-light"
                                                        type="submit"
                                                        aria-label="Aumenta quantità">

                                                    +

                                                </button>

                                            </form>

                                        </div>

                                    </div>

                                    <!-- RIMOZIONE -->
                                    <div>

                                        <form method="post"
                                              action="${pageContext.request.contextPath}/cart">

                                            <input type="hidden"
                                                   name="action"
                                                   value="remove">

                                            <input type="hidden"
                                                   name="idProdotto"
                                                   value="${item.idProdotto}">

                                            <button class="FM-button FM-button-light"
                                                    type="submit">

                                                Rimuovi

                                            </button>

                                        </form>

                                    </div>

                                </article>

                            </c:forEach>

                        </div>

                    </section>

                    <!-- RIEPILOGO ORDINE -->
                    <aside class="FM-account-card">

                        <h2 class="FM-section-title">
                            Riepilogo ordine
                        </h2>

                        <div class="FM-account-grid">

                            <!-- NUMERO PRODOTTI -->
                            <div class="FM-account-info">

                                <span class="FM-muted">
                                    Prodotti
                                </span>

                                <strong>
                                    <c:out value="${itemCount}"/>
                                </strong>

                            </div>

                            <!-- SUBTOTALE -->
                            <div class="FM-account-info">

                                <span class="FM-muted">
                                    Subtotale
                                </span>

                                <strong>

                                    €
                                    <fmt:formatNumber
                                            value="${subtotal}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>

                                </strong>

                            </div>

                            <!-- SPEDIZIONE -->
                            <div class="FM-account-info">

                                <span class="FM-muted">
                                    Spedizione
                                </span>

                                <strong>

                                    €
                                    <fmt:formatNumber
                                            value="${shipping}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>

                                </strong>

                            </div>

                            <!-- TOTALE -->
                            <div class="FM-account-info">

                                <span class="FM-muted">
                                    Totale
                                </span>

                                <strong class="FM-order-total">

                                    €
                                    <fmt:formatNumber
                                            value="${total}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>

                                </strong>

                            </div>

                        </div>

                        <div class="FM-filter-actions">

                            <!-- CHECKOUT -->
                            <a class="FM-button FM-button-primary"
                               href="${pageContext.request.contextPath}/checkout">

                                Procedi al checkout

                            </a>

                            <!-- CATALOGO -->
                            <a class="FM-button FM-button-light"
                               href="${pageContext.request.contextPath}/catalog">

                                Continua gli acquisti

                            </a>

                        </div>

                    </aside>

                </div>

            </c:otherwise>

        </c:choose>

    </div>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp"/>

</body>

</html>
