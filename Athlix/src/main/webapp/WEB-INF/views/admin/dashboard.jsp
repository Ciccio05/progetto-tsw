<!-- RUOLO DELLA PAGINA: Mostra il pannello principale dell’area amministratore. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pannello amministratore - AthliX</title>
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
                    <h1 class="FM-section-title">Pannello amministratore</h1>
                    <p class="FM-muted">
                        Gestisci catalogo, ordini, promozioni e recensioni di AthliX.
                    </p>
                </div>
            </div>

            <div class="FM-account-grid">
                <div class="FM-account-info">
                    <strong>Prodotti</strong>
                    <span>Inserisci, modifica, disabilita o elimina i prodotti.</span>
                    <a class="FM-button FM-button-primary"
                       href="${pageContext.request.contextPath}/admin/products">
                        Gestisci prodotti
                    </a>
                </div>

                <div class="FM-account-info">
                    <strong>Ordini</strong>
                    <span>Consulta gli ordini, filtra e aggiorna lo stato di spedizione.</span>
                    <a class="FM-button FM-button-primary"
                       href="${pageContext.request.contextPath}/admin/orders">
                        Gestisci ordini
                    </a>
                </div>

                <div class="FM-account-info">
                    <strong>Sconti e promozioni</strong>
                    <span>Crea le promozioni e assegnale ai prodotti.</span>
                    <a class="FM-button FM-button-secondary"
                       href="${pageContext.request.contextPath}/admin/discounts">
                        Gestisci sconti
                    </a>
                </div>

                <div class="FM-account-info">
                    <strong>Recensioni</strong>
                    <span>Consulta e, quando necessario, elimina le recensioni.</span>
                    <a class="FM-button FM-button-secondary"
                       href="${pageContext.request.contextPath}/admin/reviews">
                        Gestisci recensioni
                    </a>
                </div>
            </div>
        </section>
    </div>
</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />

</body>
</html>
