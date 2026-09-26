<!-- RUOLO DELLA PAGINA: Mostra e gestisce il dettaglio di un ordine. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ordine #${ordine.idOrdine} - AthliX Admin</title>
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
                    <h1 class="FM-section-title">
                        Ordine #<c:out value="${ordine.idOrdine}" />
                    </h1>
                    <p class="FM-muted">
                        Creato il
                        <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy" />
                    </p>
                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/admin/orders">
                    Torna agli ordini
                </a>
            </div>

            <c:if test="${not empty adminMessage}">
                <div class="FM-message FM-message-success" role="status">
                    <c:out value="${adminMessage}" />
                </div>
            </c:if>

            <c:if test="${not empty adminError}">
                <div class="FM-message FM-message-error" role="alert">
                    <c:out value="${adminError}" />
                </div>
            </c:if>

            <div class="FM-account-grid">
                <div class="FM-account-info">
                    <strong>Cliente</strong>
                    <span>
                        <c:out value="${ordine.nomeUtente}" />
                        <c:out value="${ordine.cognomeUtente}" />
                    </span>
                </div>

                <div class="FM-account-info">
                    <strong>Email</strong>
                    <span><c:out value="${ordine.email}" /></span>
                </div>

                <div class="FM-account-info">
                    <strong>Totale</strong>
                    <span class="FM-order-total">
                        <fmt:formatNumber value="${ordine.totale}"
                                          type="currency"
                                          currencySymbol="€"
                                          minFractionDigits="2"
                                          maxFractionDigits="2" />
                    </span>
                </div>

                <div class="FM-account-info">
                    <strong>Stato ordine</strong>
                    <span class="FM-order-status"><c:out value="${ordine.stato}" /></span>
                </div>
            </div>
        </section>

        <section class="FM-account-card">
            <h2 class="FM-section-title">Pagamento</h2>
            <div class="FM-account-grid">
                <div class="FM-account-info">
                    <strong>ID pagamento</strong>
                    <span>#<c:out value="${ordine.idPagamento}" /></span>
                </div>

                <div class="FM-account-info">
                    <strong>Stato</strong>
                    <span><c:out value="${ordine.statoPagamento}" /></span>
                </div>

                <div class="FM-account-info">
                    <strong>Data pagamento</strong>
                    <span><fmt:formatDate value="${ordine.dataPagamento}" pattern="dd/MM/yyyy" /></span>
                </div>

                <div class="FM-account-info">
                    <strong>Metodo</strong>
                    <span>ID metodo <c:out value="${ordine.idMetodoPagamento}" /></span>
                </div>
            </div>
        </section>

        <section class="FM-account-card">
            <h2 class="FM-section-title">Spedizione</h2>
            <div class="FM-account-grid">
                <div class="FM-account-info">
                    <strong>Indirizzo</strong>
                    <span><c:out value="${ordine.indirizzoSpedizione}" /></span>
                </div>

                <div class="FM-account-info">
                    <strong>Stato spedizione</strong>
                    <span><c:out value="${ordine.statoSpedizione}" /></span>
                </div>

                <div class="FM-account-info">
                    <strong>Data partenza</strong>
                    <span>
                        <c:choose>
                            <c:when test="${not empty ordine.dataPartenza}">
                                <fmt:formatDate value="${ordine.dataPartenza}" pattern="dd/MM/yyyy" />
                            </c:when>
                            <c:otherwise>Non ancora partita</c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <div class="FM-account-info">
                    <strong>Data consegna</strong>
                    <span>
                        <c:choose>
                            <c:when test="${not empty ordine.dataConsegna}">
                                <fmt:formatDate value="${ordine.dataConsegna}" pattern="dd/MM/yyyy" />
                            </c:when>
                            <c:otherwise>Non ancora consegnata</c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </div>
        </section>

        <c:if test="${ordine.stato != 'ANNULLATO' && ordine.stato != 'CONSEGNATO'}">
            <section class="FM-account-card">
                <h2 class="FM-section-title">Aggiorna stato</h2>
                <p class="FM-muted">
                    È consentito procedere solo allo stato successivo del flusso.
                    La spedizione viene aggiornata automaticamente.
                </p>

                <form method="post"
                      action="${pageContext.request.contextPath}/admin/orders/detail"
                      class="FM-auth-form"
                      id="FM-order-status-form">

                    <input type="hidden" name="id" value="${ordine.idOrdine}">
                    <input type="hidden" name="action" value="update">

                    <div class="FM-form-group">
                        <label for="stato">Nuovo stato</label>
                        <select class="FM-input" id="stato" name="stato" required>
                            <option value="${ordine.stato}">Mantieni: ${ordine.stato}</option>

                            <c:if test="${ordine.stato == 'CONFERMATO'}">
                                <option value="IN LAVORAZIONE">In lavorazione</option>
                            </c:if>

                            <c:if test="${ordine.stato == 'IN LAVORAZIONE'}">
                                <option value="SPEDITO">Spedito</option>
                            </c:if>

                            <c:if test="${ordine.stato == 'SPEDITO'}">
                                <option value="CONSEGNATO">Consegnato</option>
                            </c:if>
                        </select>
                    </div>

                    <button class="FM-button FM-button-primary" type="submit">
                        Salva stato
                    </button>
                </form>
            </section>
        </c:if>

        <c:if test="${ordine.stato == 'CONFERMATO' || ordine.stato == 'IN LAVORAZIONE'}">
            <section class="FM-account-card FM-danger-zone">
                <h2 class="FM-section-title">Annulla ordine</h2>
                <p class="FM-muted">
                    L'annullamento è definitivo: lo stock viene ripristinato,
                    la spedizione viene annullata e il pagamento viene segnato
                    come rimborsato o annullato.
                </p>

                <form method="post"
                      action="${pageContext.request.contextPath}/admin/orders/detail"
                      class="FM-auth-form"
                      id="FM-cancel-order-form">

                    <input type="hidden" name="id" value="${ordine.idOrdine}">
                    <input type="hidden" name="action" value="cancel">

                    <label class="FM-checkbox-inline" for="confirmCancel">
                        <input id="confirmCancel"
                               type="checkbox"
                               name="confirmCancel"
                               value="true"
                               required>
                        Confermo di voler annullare definitivamente l'ordine
                        #<c:out value="${ordine.idOrdine}" />.
                    </label>

                    <span class="FM-inline-error" id="FM-cancel-error">
                        Conferma l'annullamento prima di procedere.
                    </span>

                    <button class="FM-button FM-button-danger" type="submit">
                        Annulla ordine
                    </button>
                </form>
            </section>
        </c:if>

        <c:if test="${ordine.stato == 'ANNULLATO'}">
            <section class="FM-account-card">
                <div class="FM-message FM-message-error" role="status">
                    Ordine annullato. Lo stock è stato ripristinato e non sono
                    consentiti ulteriori cambi di stato.
                </div>
            </section>
        </c:if>

        <section class="FM-account-card">
            <h2 class="FM-section-title">Prodotti dell'ordine</h2>

            <c:choose>
                <c:when test="${empty items}">
                    <div class="FM-empty-state">Nessun prodotto associato all'ordine.</div>
                </c:when>

                <c:otherwise>
                    <div class="FM-order-list">
                        <c:forEach var="item" items="${items}">
                            <article class="FM-order-card">
                                <div>
                                    <strong><c:out value="${item.nomeProdotto}" /></strong>
                                    <p class="FM-muted">Prodotto #<c:out value="${item.idProdotto}" /></p>
                                    <p class="FM-muted">Quantità: <c:out value="${item.quantita}" /></p>
                                </div>

                                <div>
                                    <strong>Prezzo storico</strong>
                                    <p>
                                        <fmt:formatNumber value="${item.prezzoAcquisto}"
                                                          type="currency"
                                                          currencySymbol="€"
                                                          minFractionDigits="2"
                                                          maxFractionDigits="2" />
                                    </p>
                                    <p class="FM-muted">
                                        IVA:
                                        <fmt:formatNumber value="${item.ivaAcquisto}"
                                                          minFractionDigits="0"
                                                          maxFractionDigits="2" />%
                                    </p>
                                </div>

                                <div class="FM-order-total">
                                    <fmt:formatNumber value="${item.subtotale}"
                                                      type="currency"
                                                      currencySymbol="€"
                                                      minFractionDigits="2"
                                                      maxFractionDigits="2" />
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
    const cancelForm = document.getElementById('FM-cancel-order-form');
    if (!cancelForm) {
        return;
    }

    const checkbox = document.getElementById('confirmCancel');
    const error = document.getElementById('FM-cancel-error');

    cancelForm.addEventListener('submit', event => {
        const valid = checkbox.checked;
        error.classList.toggle('FM-inline-error-visible', !valid);
        if (!valid) {
            event.preventDefault();
            checkbox.focus();
        }
    });

    checkbox.addEventListener('change', () => {
        error.classList.remove('FM-inline-error-visible');
    });
})();
</script>

</body>
</html>
