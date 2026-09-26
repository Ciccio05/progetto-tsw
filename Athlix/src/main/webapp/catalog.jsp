<!-- RUOLO DELLA PAGINA: Mostra il catalogo pubblico, i filtri e la ricerca AJAX dei prodotti. -->
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

    <title>Catalogo - AthliX</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="FM-page">

<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp"/>

<main class="FM-main">
    <div class="FM-container">

        <section class="FM-section">

            <h2 class="FM-section-title">
                Catalogo prodotti
            </h2>

            <!-- FILTRI -->
            <div class="FM-filter-panel">

                <form class="FM-filter-form"
                      method="get"
                      action="${pageContext.request.contextPath}/catalog">

            <!--DAI FILTRI MI VIENE RESTITUITO UN URL-->
                    <!-- RICERCA -->
                    <div class="FM-field FM-field-search">

                        <label class="FM-label"
                               for="searchInput">
                            Cerca un prodotto:
                        </label>

                        <div class="FM-search-wrapper">

                            <input class="FM-input"
                                   type="text"
                                   id="searchInput"
                                   name="q"
                                   placeholder="Cerca per nome..."
                                   autocomplete="off"
                                   value="${fn:escapeXml(selectedSearch)}">

                            <div id="searchSuggestions"
                                 class="FM-search-suggestions"
                                 aria-live="polite"></div>
                                 <!--aria-live="polite" comunica alle tecnologie assistive che il contenuto può cambiare dinamicamente-->

                        </div>
                    </div>

                    <!-- CATEGORIA -->
                    <div class="FM-field FM-field-category">

                        <label class="FM-label"
                               for="category">
                            Categoria:
                        </label>

                        <select class="FM-select"
                                id="category"
                                name="category">

                            <option value="">
                                Tutte le categorie
                            </option>

                            <c:forEach var="category"
                                       items="${categories}">

                                <option value="${category.idCategoria}"
                                    ${category.idCategoria == selectedCategory ? 'selected' : ''}>
                                    <c:out value="${category.nomeCategoria}"/>
                                </option>

                            </c:forEach>

                        </select>
                    </div>

                    <!-- PREZZO MINIMO -->
                    <div class="FM-field FM-field-price">

                        <label class="FM-label"
                               for="minPrice">
                            Prezzo minimo:
                        </label>

                        <input class="FM-input"
                               type="number"
                               id="minPrice"
                               name="minPrice"
                               min="0"
                               step="0.01"
                               placeholder="0.00"
                               value="${selectedMinPrice}">

                    </div>

                    <!-- PREZZO MASSIMO -->
                    <div class="FM-field FM-field-price">

                        <label class="FM-label"
                               for="maxPrice">
                            Prezzo massimo:
                        </label>

                        <input class="FM-input"
                               type="number"
                               id="maxPrice"
                               name="maxPrice"
                               min="0"
                               step="0.01"
                               placeholder="200.00"
                               value="${selectedMaxPrice}">

                    </div>

                    <!-- DISPONIBILITÀ -->
                    <div class="FM-field FM-field-availability">

                        <div class="FM-checkbox-row">

                            <input class="FM-checkbox"
                                   type="checkbox"
                                   id="available"
                                   name="available"
                                   value="true"
                                   ${onlyAvailable ? 'checked' : ''}>

                            <label class="FM-label"
                                   for="available">
                                Solo disponibili
                            </label>

                        </div>
                    </div>

                    <!-- ORDINAMENTO -->
                    <div class="FM-field FM-field-sort">

                        <label class="FM-label"
                               for="sort">
                            Ordina per:
                        </label>

                        <select class="FM-select"
                                id="sort"
                                name="sort">

                            <option value="nameAsc"
                                ${selectedSort == 'nameAsc' ? 'selected' : ''}>
                                Nome A-Z
                            </option>

                            <option value="nameDesc"
                                ${selectedSort == 'nameDesc' ? 'selected' : ''}>
                                Nome Z-A
                            </option>

                            <option value="priceAsc"
                                ${selectedSort == 'priceAsc' ? 'selected' : ''}>
                                Prezzo crescente
                            </option>

                            <option value="priceDesc"
                                ${selectedSort == 'priceDesc' ? 'selected' : ''}>
                                Prezzo decrescente
                            </option>

                        </select>
                    </div>

                    <!-- AZIONI FILTRI -->
                    <div class="FM-filter-actions">

                        <button class="FM-button FM-button-primary"
                                type="submit">
                            Cerca / Applica filtri
                        </button>

                        <a class="FM-button FM-button-light"
                           href="${pageContext.request.contextPath}/catalog">
                            Rimuovi filtri
                        </a>

                    </div>

                </form>
            </div>

            <!-- ERRORE FILTRI -->
            <c:if test="${not empty filterError}">
                <p class="FM-message FM-message-error"
                   role="alert">
                    <c:out value="${filterError}"/>
                </p>
            </c:if>

        </section>

        <!-- NESSUN PRODOTTO -->
        <c:if test="${empty products && empty filterError}">
            <div class="FM-empty-state">
                Nessun prodotto trovato.
            </div>
        </c:if>

        <!-- PRODOTTI -->
        <div class="FM-product-grid">

            <c:forEach var="product"
                       items="${products}">

                <article class="FM-product-card">

                    <!-- IMMAGINE -->
                    <a class="FM-product-card-image FM-product-card-image-link"
                       href="${pageContext.request.contextPath}/product?id=${product.idProdotto}"
                       aria-label="Apri ${fn:escapeXml(product.nome)}">

                        <img src="${pageContext.request.contextPath}${empty product.immagine
                                ? '/images/products/default-product.svg'
                                : fn:escapeXml(product.immagine)}"
                             alt="${fn:escapeXml(product.nome)}"
                             loading="lazy">

                    </a>

                    <div class="FM-product-card-body">

                        <!-- CATEGORIA -->
                        <p class="FM-product-category">
                            <c:out value="${product.nomeCategoria}"/>
                        </p>

                        <!-- NOME -->
                        <h3 class="FM-product-name">
                            <a class="FM-product-title-link"
                               href="${pageContext.request.contextPath}/product?id=${product.idProdotto}">
                                <c:out value="${product.nome}"/>
                            </a>
                        </h3>

                        <!-- RECENSIONI -->
                        <div class="FM-product-rating"
                             aria-label="Valutazione prodotto">

                            <span aria-hidden="true">★</span>

                            <fmt:formatNumber
                                value="${product.mediaRecensioni}"
                                minFractionDigits="1"
                                maxFractionDigits="1"/>

                            <span class="FM-muted">
                                (<c:out value="${product.numeroRecensioni}"/>)
                            </span>

                        </div>

                        <!-- DESCRIZIONE -->
                        <p class="FM-product-description">
                            <c:out value="${product.descrizione}"/>
                        </p>

                        <!-- PREZZO E SCONTO -->
                        <c:choose>

                            <c:when test="${product.disponibilita
                                            && product.quantita > 0
                                            && product.percentualeSconto > 0}">

                                <span class="FM-discount-badge">
                                    -<fmt:formatNumber
                                        value="${product.percentualeSconto}"
                                        maxFractionDigits="0"/>%
                                </span>

                                <div class="FM-product-price-block">

                                    <span class="FM-price-old">
                                        €
                                        <fmt:formatNumber
                                            value="${product.prezzo}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>
                                    </span>

                                    <span class="FM-price">
                                        €
                                        <fmt:formatNumber
                                            value="${product.prezzoFinale}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>
                                    </span>

                                </div>

                            </c:when>

                            <c:otherwise>

                                <div class="FM-product-price-block">
                                    <span class="FM-price">
                                        €
                                        <fmt:formatNumber
                                            value="${product.prezzo}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>
                                    </span>
                                </div>

                            </c:otherwise>

                        </c:choose>

                        <!-- DISPONIBILITÀ -->
                        <c:choose>

                            <c:when test="${product.disponibilita
                                            && product.quantita > 0}">
                                <p class="FM-stock FM-stock-available">
                                    Disponibile
                                </p>
                            </c:when>

                            <c:otherwise>
                                <p class="FM-stock FM-stock-unavailable">
                                    Non disponibile
                                </p>
                            </c:otherwise>

                        </c:choose>

                        <!-- AZIONI -->
                        <div class="FM-product-card-actions">

                            <a class="FM-button FM-button-secondary"
                               href="${pageContext.request.contextPath}/product?id=${product.idProdotto}">
                                Visualizza dettagli
                            </a>

                        </div>

                    </div>
                </article>

            </c:forEach>

        </div>

    </div>
</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp"/>

<script>
(() => {

    /* ELEMENTI RICERCA */
    const searchInput = document.getElementById('searchInput');
    const searchSuggestions = document.getElementById('searchSuggestions');
    const contextPath = '${pageContext.request.contextPath}';

    /*Identifica l'ultima richiesta effettuata e Serve a ignorare eventuali risposte AJAX vecchie. */
    let latestRequest = 0;

    /* Formatta i prezzi in euro secondo il formato italiano. */
    const priceFormatter = new Intl.NumberFormat('it-IT', {
        style: 'currency',
        currency: 'EUR'
    });

    /* RICERCA AJAX */
    searchInput.addEventListener('input',async () => {

        const query = searchInput.value.trim();
        const requestId = ++latestRequest;

        /* La ricerca parte da almeno 2 caratteri. */
        if (query.length < 2) {
            searchSuggestions.innerHTML = '';
            return;
        }

        try {
            const response = await fetch(
                contextPath
                + '/search-products?q='
                + encodeURIComponent(query),
                {
                    headers: {
                        'Accept': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw new Error(
                    'Errore HTTP: ' + response.status
                );
            }

            const products = await response.json();

            /* Se nel frattempo è partita una ricerca più recente,ignoriamo questa risposta.*/
            if (requestId !== latestRequest) {
                return;
            }

            searchSuggestions.innerHTML = '';

            /* Nessun risultato. */
            if (products.length === 0) {
                const message = document.createElement('p');

                message.className = 'FM-message';
                message.textContent = 'Nessun prodotto trovato.';

                searchSuggestions.appendChild(message);
                return;
            }

            /* Creiamo un suggerimento per ogni prodotto ricevuto. */
            products.forEach(product => {

                const link = document.createElement('a');
                link.className = 'FM-suggestion-item';
                link.href =
                    contextPath
                    + '/product?id='
                    + encodeURIComponent(product.id);

                const info = document.createElement('span');
                info.className = 'FM-suggestion-info';

                const name = document.createElement('span');
                name.className = 'FM-suggestion-name';
                name.textContent = product.nome;

                const category = document.createElement('span');
                category.className = 'FM-suggestion-category';
                category.textContent = product.categoria;

                const price = document.createElement('span');
                price.className = 'FM-suggestion-price';
                price.textContent =
                    priceFormatter.format(
                        Number(product.prezzo)
                    );

                info.appendChild(name);
                info.appendChild(category);

                link.appendChild(info);
                link.appendChild(price);

                searchSuggestions.appendChild(link);
            });

        } catch (error) {
            console.error(
                'Errore ricerca prodotti:',
                error
            );

            /* Non eliminiamo risultati relativi a una ricerca più recente.*/
            if (requestId === latestRequest) {
                searchSuggestions.innerHTML = '';
            }
        }
    });

})();
</script>

</body>
</html>
