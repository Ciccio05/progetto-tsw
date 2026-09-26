<!-- RUOLO DELLA PAGINA: Home page con prodotti in evidenza e accesso alle principali funzioni. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fn"
           uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>

<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>AthliX - Home</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page">

<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp"/>

<main class="FM-main">

    <!-- HERO -->
    <section class="FM-home-hero">

        <div class="FM-container">

            <p class="FM-home-kicker">
                Lo sport per tutti
            </p>

            <h1>
                Trova l'attrezzatura per il tuo sport
            </h1>

            <p>
                Scopri il catalogo AthliX e filtra i prodotti
                in base alla categoria che preferisci.
            </p>

            <a class="FM-button FM-button-primary"
               href="${pageContext.request.contextPath}/catalog">

                Vai al catalogo

            </a>

        </div>

    </section>

    <!-- CATEGORIE -->
    <section class="FM-container FM-home-categories">

        <h2 class="FM-section-title">
            Categorie
        </h2>

        <c:choose>

            <c:when test="${empty categories}">

                <div class="FM-empty-state">

                    <p>
                        Nessuna categoria disponibile.
                    </p>

                </div>

            </c:when>

            <c:otherwise>

                <div class="FM-category-grid">

                    <c:forEach var="category"
                               items="${categories}">

                        <a class="FM-category-card FM-category-theme FM-category-icon-${fn:toLowerCase(category.nomeCategoria)}"
                           href="${pageContext.request.contextPath}/catalog?category=${category.idCategoria}">

                            <span class="FM-category-card-label">
                                <c:out value="${category.nomeCategoria}"/>
                            </span>

                        </a>

                    </c:forEach>

                </div>

            </c:otherwise>

        </c:choose>

    </section>

    <section class="FM-container FM-home-featured">

        <div class="FM-section-heading-row">
            <div>
                <p class="FM-home-kicker">Scelti per te</p>
                <h2 class="FM-section-title">Prodotti in evidenza</h2>
            </div>

            <a class="FM-button FM-button-light"
               href="${pageContext.request.contextPath}/catalog">
                Vedi tutto il catalogo
            </a>
        </div>

        <c:choose>
            <c:when test="${empty featuredProducts}">
                <div class="FM-empty-state">Nessun prodotto disponibile.</div>
            </c:when>

            <c:otherwise>
                <div class="FM-product-grid FM-home-product-grid">
                    <c:forEach var="product" items="${featuredProducts}">
                        <article class="FM-product-card">
                            <a class="FM-product-card-image FM-product-card-image-link"
                               href="${pageContext.request.contextPath}/product?id=${product.idProdotto}">
                                <img src="${pageContext.request.contextPath}${empty product.immagine ? '/images/products/default-product.svg' : fn:escapeXml(product.immagine)}"
                                     alt="${fn:escapeXml(product.nome)}"
                                     loading="lazy">
                            </a>

                            <div class="FM-product-card-body">
                                <p class="FM-product-category"><c:out value="${product.nomeCategoria}" /></p>

                                <h3 class="FM-product-name">
                                    <a class="FM-product-title-link"
                                       href="${pageContext.request.contextPath}/product?id=${product.idProdotto}">
                                        <c:out value="${product.nome}" />
                                    </a>
                                </h3>

                                <div class="FM-product-rating" aria-label="Valutazione prodotto">
                                    <span aria-hidden="true">★</span>
                                    <fmt:formatNumber value="${product.mediaRecensioni}"
                                                      minFractionDigits="1"
                                                      maxFractionDigits="1" />
                                    <span class="FM-muted">
                                        (<c:out value="${product.numeroRecensioni}" /> recensioni)
                                    </span>
                                </div>

                                <div class="FM-product-price-block">
                                    <c:if test="${product.disponibilita && product.quantita > 0 && product.percentualeSconto > 0}">
                                        <span class="FM-price-old">
                                            € <fmt:formatNumber value="${product.prezzo}" minFractionDigits="2" maxFractionDigits="2" />
                                        </span>
                                    </c:if>

                                    <span class="FM-price">
                                        € <fmt:formatNumber value="${product.prezzoFinale}" minFractionDigits="2" maxFractionDigits="2" />
                                    </span>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </section>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp"/>

</body>

</html>
