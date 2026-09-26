<!-- RUOLO DELLA PAGINA: Mostra l’elenco ordini con filtri amministrativi. -->
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

    <title>Gestione ordini - AthliX Admin</title>

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
                        Gestione ordini
                    </h1>

                    <p class="FM-muted">
                        Visualizza e filtra gli ordini effettuati dai clienti.
                    </p>
                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/admin">
                    Pannello admin
                </a>

            </div>

            <c:if test="${not empty adminError}">
                <div class="FM-message FM-message-error">
                    <c:out value="${adminError}" />
                </div>
            </c:if>

            <form id="FM-admin-orders-filter"
                  method="get"
                  action="${pageContext.request.contextPath}/admin/orders"
                  class="FM-auth-form"
                  novalidate>

                <div class="FM-form-row-three">

                    <div class="FM-form-group">
                        <label for="from">
                            Data iniziale
                        </label>

                        <input class="FM-input"
                               id="from"
                               type="date"
                               name="from"
                               value="${from}">
                    </div>

                    <div class="FM-form-group">
                        <label for="to">
                            Data finale
                        </label>

                        <input class="FM-input"
                               id="to"
                               type="date"
                               name="to"
                               value="${to}">
                    </div>

                    <div class="FM-form-group">
                        <label for="customer">
                            Cliente
                        </label>

                        <input class="FM-input"
                               id="customer"
                               type="search"
                               name="customer"
                               maxlength="100"
                               placeholder="Nome, cognome, email o ID"
                               value="<c:out value='${customer}'/>">
                    </div>

                </div>

                <div id="FM-admin-orders-filter-error"
                     class="FM-inline-error"></div>

                <div class="FM-page-actions">

                    <button class="FM-button FM-button-primary"
                            type="submit">
                        Filtra
                    </button>

                    <a class="FM-button FM-button-light"
                       href="${pageContext.request.contextPath}/admin/orders">
                        Azzera filtri
                    </a>

                </div>

            </form>

        </section>

        <section class="FM-account-card">

            <c:choose>

                <c:when test="${empty ordini}">

                    <div class="FM-empty-state">
                        Nessun ordine trovato.
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

                                <div>

                                    <strong>
                                        <c:out value="${ordine.nomeUtente}" />
                                        <c:out value="${ordine.cognomeUtente}" />
                                    </strong>

                                    <p class="FM-muted">
                                        <c:out value="${ordine.email}" />
                                    </p>

                                    <p class="FM-muted">
                                        Cliente #
                                        <c:out value="${ordine.idUtente}" />
                                    </p>

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
                                       value="/admin/orders/detail">

                                    <c:param name="id"
                                             value="${ordine.idOrdine}" />

                                </c:url>

                                <a class="FM-button FM-button-secondary"
                                   href="${detailUrl}">
                                    Gestisci
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

<script>
(() => {
    /* Valida l'intervallo di date prima di applicare i filtri. */
    const form = document.getElementById('FM-admin-orders-filter');
    const from = document.getElementById('from');
    const to = document.getElementById('to');
    const error = document.getElementById('FM-admin-orders-filter-error');

    form.addEventListener('submit',event => {
        const invalidRange = from.value && to.value && from.value > to.value;

        error.textContent = invalidRange
            ? 'La data iniziale non può essere successiva alla data finale.'
            : '';
        error.classList.toggle('FM-inline-error-visible',invalidRange);

        if (invalidRange) {
            event.preventDefault();
            from.focus();
        }
    });
})();
</script>

</body>
</html>
