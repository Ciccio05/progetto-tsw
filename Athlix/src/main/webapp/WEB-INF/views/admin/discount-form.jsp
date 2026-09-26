<!-- RUOLO DELLA PAGINA: Gestisce inserimento e modifica degli sconti. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isEdit"
       value="${not empty sconto and not empty sconto.idSconto}" />

<c:set var="formAction"
       value="${isEdit
               ? pageContext.request.contextPath.concat('/admin/discounts/edit')
               : pageContext.request.contextPath.concat('/admin/discounts/new')}" />

<!DOCTYPE html>
<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        <c:choose>
            <c:when test="${isEdit}">
                Modifica sconto
            </c:when>
            <c:otherwise>
                Nuovo sconto
            </c:otherwise>
        </c:choose>
        - AthliX Admin
    </title>

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

                        <c:choose>
                            <c:when test="${isEdit}">
                                Modifica sconto
                            </c:when>
                            <c:otherwise>
                                Nuovo sconto
                            </c:otherwise>
                        </c:choose>

                    </h1>

                    <p class="FM-muted">
                        Imposta percentuale e periodo di validità.
                    </p>

                </div>

                <a class="FM-button FM-button-light"
                   href="${pageContext.request.contextPath}/admin/discounts">
                    Torna agli sconti
                </a>

            </div>

            <c:if test="${not empty adminError}">

                <div class="FM-message FM-message-error"
                     role="alert">

                    <c:out value="${adminError}" />

                </div>

            </c:if>

            <form method="post"
                  action="${formAction}"
                  class="FM-auth-form"
                  id="FM-discount-form"
                  novalidate>

                <c:if test="${isEdit}">

                    <input type="hidden"
                           name="id"
                           value="${sconto.idSconto}">

                </c:if>

                <div class="FM-form-group">

                    <label for="percentuale">
                        Percentuale sconto
                    </label>

                    <input class="FM-input"
                           id="percentuale"
                           type="number"
                           name="percentuale"
                           min="0.01"
                           max="100"
                           step="0.01"
                           required
                           placeholder="Es. 20"
                           value="<c:choose><c:when test='${not empty oldPercentuale}'><c:out value='${oldPercentuale}'/></c:when><c:otherwise><c:out value='${sconto.percentuale}'/></c:otherwise></c:choose>">

                    <span class="FM-inline-error"
                          id="percentualeError"></span>

                </div>

                <div class="FM-form-row">

                    <div class="FM-form-group">

                        <label for="dataInizio">
                            Data inizio
                        </label>

                        <fmt:formatDate
                                value="${sconto.dataInizio}"
                                pattern="yyyy-MM-dd"
                                var="formattedStart" />

                        <input class="FM-input"
                               id="dataInizio"
                               type="date"
                               name="dataInizio"
                               required
                               value="${not empty oldDataInizio ? oldDataInizio : formattedStart}">

                        <span class="FM-inline-error"
                              id="dataInizioError"></span>

                    </div>

                    <div class="FM-form-group">

                        <label for="dataFine">
                            Data fine
                        </label>

                        <fmt:formatDate
                                value="${sconto.dataFine}"
                                pattern="yyyy-MM-dd"
                                var="formattedEnd" />

                        <input class="FM-input"
                               id="dataFine"
                               type="date"
                               name="dataFine"
                               required
                               value="${not empty oldDataFine ? oldDataFine : formattedEnd}">

                        <span class="FM-inline-error"
                              id="dataFineError"></span>

                    </div>

                </div>

                <button class="FM-button FM-button-primary"
                        type="submit">

                    <c:choose>
                        <c:when test="${isEdit}">
                            Salva modifiche
                        </c:when>
                        <c:otherwise>
                            Crea sconto
                        </c:otherwise>
                    </c:choose>

                </button>

            </form>

        </section>

    </div>

</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />

<script>
(() => {
    const form = document.getElementById("FM-discount-form");
    const percentuale = document.getElementById("percentuale");
    const dataInizio = document.getElementById("dataInizio");
    const dataFine = document.getElementById("dataFine");

    const setError = (input, message) => {
        const error = document.getElementById(input.id + "Error");

        input.classList.toggle("FM-input-error", Boolean(message));
        error.textContent = message || "";
        error.classList.toggle("FM-inline-error-visible", Boolean(message));
    };

    form.addEventListener("submit", event => {
        let valid = true;
        let firstInvalid = null;

        const value = Number(percentuale.value);

        if (!percentuale.value || Number.isNaN(value) || value <= 0 || value > 100) {
            setError(percentuale, "Inserisci una percentuale tra 0,01 e 100.");
            valid = false;
            firstInvalid ??= percentuale;
        } else {
            setError(percentuale, "");
        }

        if (!dataInizio.value) {
            setError(dataInizio, "Seleziona la data di inizio.");
            valid = false;
            firstInvalid ??= dataInizio;
        } else {
            setError(dataInizio, "");
        }

        if (!dataFine.value) {
            setError(dataFine, "Seleziona la data di fine.");
            valid = false;
            firstInvalid ??= dataFine;
        } else if (dataInizio.value && dataFine.value < dataInizio.value) {
            setError(dataFine, "La data di fine non può precedere la data di inizio.");
            valid = false;
            firstInvalid ??= dataFine;
        } else {
            setError(dataFine, "");
        }

        if (!valid) {
            event.preventDefault();
            firstInvalid?.focus();
        }
    });
})();
</script>

</body>
</html>
