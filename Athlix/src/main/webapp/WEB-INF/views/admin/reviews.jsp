<!-- RUOLO DELLA PAGINA: Mostra e gestisce le recensioni nell’area Admin. -->
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

    <title>Gestione recensioni - AthliX Admin</title>

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
                        Gestione recensioni
                    </h1>

                    <p class="FM-muted">
                        Controlla le recensioni pubblicate dagli utenti.
                    </p>

                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/admin">
                    Pannello admin
                </a>

            </div>

            <c:if test="${not empty adminMessage}">

                <div class="FM-message FM-message-success">
                    <c:out value="${adminMessage}" />
                </div>

            </c:if>

            <form id="FM-admin-reviews-filter"
                  method="get"
                  action="${pageContext.request.contextPath}/admin/reviews"
                  class="FM-auth-form"
                  novalidate>

                <div class="FM-form-group">

                    <label for="search">
                        Cerca
                    </label>

                    <input class="FM-input"
                           id="search"
                           name="search"
                           type="search"
                           maxlength="120"
                           placeholder="Prodotto, nome cliente o email"
                           value="<c:out value='${search}'/>">

                </div>

                <div id="FM-admin-reviews-filter-error"
                     class="FM-inline-error"></div>

                <div class="FM-page-actions">

                    <button class="FM-button FM-button-primary"
                            type="submit">
                        Cerca
                    </button>

                    <a class="FM-button FM-button-light"
                       href="${pageContext.request.contextPath}/admin/reviews">
                        Azzera
                    </a>

                </div>

            </form>

        </section>

        <section class="FM-account-card">

            <c:choose>

                <c:when test="${empty recensioni}">

                    <div class="FM-empty-state">
                        Nessuna recensione trovata.
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
                                            <c:out value="${recensione.nomeProdotto}" />
                                        </strong>

                                        <p class="FM-muted">

                                            Recensione #
                                            <c:out value="${recensione.idRecensione}" />

                                            ·

                                            <fmt:formatDate
                                                    value="${recensione.dataRecensione}"
                                                    pattern="dd/MM/yyyy" />

                                        </p>

                                    </div>

                                    <span class="FM-order-status">
                                        ${recensione.voto} / 5
                                    </span>

                                </div>

                                <p>
                                    <c:out value="${recensione.commento}" />
                                </p>

                                <p class="FM-muted">

                                    Autore:

                                    <c:out value="${recensione.nomeUtente}" />
                                    <c:out value="${recensione.cognomeUtente}" />

                                    -

                                    <c:out value="${recensione.emailUtente}" />

                                </p>

                                <div class="FM-page-actions">

                                    <c:url var="productUrl"
                                           value="/product">

                                        <c:param
                                                name="id"
                                                value="${recensione.idProdotto}" />

                                    </c:url>

                                    <a class="FM-button FM-button-secondary"
                                       href="${productUrl}">
                                        Apri prodotto
                                    </a>

                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/reviews"
                                          onsubmit="return confirm('Eliminare definitivamente questa recensione?');">

                                        <input type="hidden"
                                               name="idRecensione"
                                               value="${recensione.idRecensione}">

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

<script>
(() => {
    /* Valida e normalizza il testo di ricerca prima di applicare il filtro. */
    const form = document.getElementById('FM-admin-reviews-filter');
    const search = document.getElementById('search');
    const error = document.getElementById('FM-admin-reviews-filter-error');

    form.addEventListener('submit',event => {
        const value = search.value.trim();
        const invalid = value.length > 120;

        error.textContent = invalid
            ? 'La ricerca non può superare 120 caratteri.'
            : '';
        error.classList.toggle('FM-inline-error-visible',invalid);

        if (invalid) {
            event.preventDefault();
            search.focus();
            return;
        }

        search.value = value;
    });
})();
</script>

</body>
</html>
