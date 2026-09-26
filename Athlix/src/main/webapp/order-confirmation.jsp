<!-- RUOLO DELLA PAGINA: Mostra la conferma e il riepilogo essenziale dell’ordine appena creato. -->
<!--DIRETTIVE-->
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

        <!--per il responsive sui mobile-->
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Ordine confermato - AthliX</title>

    <!--collego il CSS non in maniera fissa cosi se cambia il context root
    JSP costruisce automaticamente il perrcorso corretto-->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page">

<!--header-->
<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main class="FM-main">

    <div class="FM-container FM-account-container">

        <section class="FM-account-card">

            <h1 class="FM-section-title">
                Ordine confermato
            </h1>

            <div class="FM-message FM-message-success"
                 role="status">

                Il tuo ordine è stato registrato correttamente.

            </div>

            <div class="FM-account-grid">
                <!--mostra l'ID dell'ordine-->
                <div class="FM-account-info">
                    <strong>
                        Numero ordine
                    </strong>
                    <span>
                        #
                        <c:out value="${idOrdine}" />
                    </span>
                </div>

                <div class="FM-account-info">
                    <strong>
                        Totale
                    </strong>
                    <span class="FM-order-total">
                        <fmt:formatNumber
                                value="${totale}"
                                type="currency"
                                currencySymbol="€"
                                minFractionDigits="2"
                                maxFractionDigits="2" />
                    </span>
                </div>
            </div>
            <p class="FM-muted">
                Puoi consultare in qualsiasi momento
                lo stato e il dettaglio dei tuoi ordini.
            </p>

            <div class="FM-page-actions">
                <a class="FM-button FM-button-secondary"
                   href="${pageContext.request.contextPath}/orders">
                    I miei ordini
                </a>
                <a class="FM-button FM-button-primary"
                   href="${pageContext.request.contextPath}/home">
                    Torna alla Home
                </a>
            </div>
        </section>
    </div>
</main>
<!--footer-->
<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>
