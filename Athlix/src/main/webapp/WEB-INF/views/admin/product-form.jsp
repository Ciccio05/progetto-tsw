<!-- RUOLO DELLA PAGINA: Gestisce inserimento e modifica dei prodotti, compreso l’upload dell’immagine. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.idProdotto > 0 ? 'Modifica prodotto' : 'Nuovo prodotto'} - AthliX</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="FM-page">

<!-- HEADER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main class="FM-main">
    <div class="FM-container FM-auth-container-wide">
        <section class="FM-auth-card FM-auth-card-wide">
            <h1 class="FM-section-title">
                <c:choose>
                    <c:when test="${product.idProdotto > 0}">Modifica prodotto</c:when>
                    <c:otherwise>Nuovo prodotto</c:otherwise>
                </c:choose>
            </h1>

            <c:if test="${not empty formError}">
                <div class="FM-message FM-message-error" role="alert">
                    <c:out value="${formError}" />
                </div>
            </c:if>

            <form id="adminProductForm"
                  class="FM-auth-form"
                  method="post"
                  action="${pageContext.request.contextPath}/admin/products/save"
                  enctype="multipart/form-data"
                  novalidate>

                <input type="hidden" name="idProdotto" value="${product.idProdotto > 0 ? product.idProdotto : ''}">

                <div class="FM-form-group">
                    <label for="nome">Nome prodotto</label>
                    <input class="FM-input"
                           id="nome"
                           name="nome"
                           type="text"
                           minlength="2"
                           maxlength="120"
                           placeholder="Es. Scarpe Running Pro"
                           required
                           value="${fn:escapeXml(product.nome)}">
                    <div id="nomeError" class="FM-inline-error"></div>
                </div>

                <div class="FM-form-row-three">
                    <div class="FM-form-group">
                        <label for="prezzo">Prezzo</label>
                        <input class="FM-input"
                               id="prezzo"
                               name="prezzo"
                               type="number"
                               min="0.01"
                               step="0.01"
                               required
                               value="${product.prezzo}">
                        <div id="prezzoError" class="FM-inline-error"></div>
                    </div>

                    <div class="FM-form-group">
                        <label for="iva">IVA %</label>
                        <input class="FM-input"
                               id="iva"
                               name="iva"
                               type="number"
                               min="0"
                               max="100"
                               step="0.01"
                               required
                               value="${empty product.iva ? 22 : product.iva}">
                        <div id="ivaError" class="FM-inline-error"></div>
                    </div>

                    <div class="FM-form-group">
                        <label for="quantita">Quantità</label>
                        <input class="FM-input"
                               id="quantita"
                               name="quantita"
                               type="number"
                               min="0"
                               step="1"
                               required
                               value="${product.quantita}">
                        <div id="quantitaError" class="FM-inline-error"></div>
                    </div>
                </div>

                <div class="FM-form-row">
                    <div class="FM-form-group">
                        <label for="idCategoria">Categoria</label>
                        <select class="FM-input" id="idCategoria" name="idCategoria" required>
                            <option value="">Seleziona una categoria</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.idCategoria}"
                                    <c:if test="${category.idCategoria == product.idCategoria}">selected</c:if>>
                                    <c:out value="${category.nomeCategoria}" />
                                </option>
                            </c:forEach>
                        </select>
                        <div id="categoriaError" class="FM-inline-error"></div>
                    </div>

                    <div class="FM-form-group">
                        <label for="idSconto">Sconto (opzionale)</label>
                        <select class="FM-input"
                                id="idSconto"
                                name="idSconto">
                            <option value="">Nessuno sconto</option>
                            <c:forEach var="discount" items="${discounts}">
                                <option value="${discount.idSconto}"
                                    <c:if test="${discount.idSconto == product.idSconto}">selected</c:if>>
                                    #<c:out value="${discount.idSconto}" />
                                    - <c:out value="${discount.percentuale}" />%
                                    (<c:out value="${discount.stato}" />)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="FM-form-group">
                    <label for="descrizione">Descrizione</label>
                    <textarea class="FM-input"
                              id="descrizione"
                              name="descrizione"
                              maxlength="2000"
                              rows="6">${fn:escapeXml(product.descrizione)}</textarea>
                </div>

                <div class="FM-form-group">
                    <label for="immagine">Foto del prodotto</label>

                    <img class="FM-product-form-preview"
                         src="${pageContext.request.contextPath}${empty product.immagine
                                ? '/images/products/default-product.svg'
                                : fn:escapeXml(product.immagine)}"
                         alt="Anteprima immagine prodotto">

                    <c:if test="${not empty product.immagine}">
                        <label class="FM-checkbox-inline">
                            <input type="checkbox" name="rimuoviImmagine" value="true">
                            Rimuovi la foto attuale
                        </label>
                    </c:if>

                    <input class="FM-input"
                           id="immagine"
                           name="immagine"
                           type="file"
                           accept="image/jpeg,image/png,image/webp,image/gif">
                    <div id="immagineError" class="FM-inline-error"></div>

                    <p class="FM-muted">
                        <c:choose>
                            <c:when test="${not empty product.immagine}">
                                Carica un file per sostituire la foto attuale, oppure lasciala così com'è.
                            </c:when>
                            <c:otherwise>
                                Scegli una foto dal tuo computer (JPG, PNG...). È facoltativa.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>

                <div class="FM-form-group">
                    <label>
                        <input type="checkbox"
                               name="disponibilita"
                               value="true"
                               <c:if test="${product.idProdotto == 0 or product.disponibilita}">checked</c:if>>
                        Prodotto disponibile
                    </label>
                </div>

                <div class="FM-inline-error" id="formClientError"></div>

                <div class="FM-page-actions">
                    <button class="FM-button FM-button-primary" type="submit">
                        Salva prodotto
                    </button>

                    <a class="FM-button FM-button-light"
                       href="${pageContext.request.contextPath}/admin/products">
                        Annulla
                    </a>
                </div>
            </form>
        </section>
    </div>
</main>

<!-- FOOTER RIUTILIZZABILE -->
<jsp:include page="/WEB-INF/fragments/footer.jsp" />

<script>
(() => {
    const form = document.getElementById('adminProductForm');
    const nome = document.getElementById('nome');
    const prezzo = document.getElementById('prezzo');
    const iva = document.getElementById('iva');
    const quantita = document.getElementById('quantita');
    const categoria = document.getElementById('idCategoria');
    const immagine = document.getElementById('immagine');
    const formError = document.getElementById('formClientError');

    function setError(element, message) {
        element.textContent = message;
        element.classList.toggle('FM-inline-error-visible', Boolean(message));
    }

    form.addEventListener('submit', event => {
        setError(document.getElementById('nomeError'), '');
        setError(document.getElementById('prezzoError'), '');
        setError(document.getElementById('ivaError'), '');
        setError(document.getElementById('quantitaError'), '');
        setError(document.getElementById('categoriaError'), '');
        setError(document.getElementById('immagineError'), '');
        setError(formError, '');

        let firstInvalid = null;

        if (nome.value.trim().length < 2 || nome.value.trim().length > 120) {
            setError(document.getElementById('nomeError'), 'Inserisci un nome tra 2 e 120 caratteri.');
            firstInvalid = firstInvalid || nome;
        }

        if (!prezzo.value || Number(prezzo.value) <= 0) {
            setError(document.getElementById('prezzoError'), 'Il prezzo deve essere maggiore di zero.');
            firstInvalid = firstInvalid || prezzo;
        }

        if (iva.value === '' || Number(iva.value) < 0 || Number(iva.value) > 100) {
            setError(document.getElementById('ivaError'), "L'IVA deve essere compresa tra 0 e 100.");
            firstInvalid = firstInvalid || iva;
        }

        if (quantita.value === '' || !Number.isInteger(Number(quantita.value)) || Number(quantita.value) < 0) {
            setError(document.getElementById('quantitaError'), 'La quantità deve essere un intero non negativo.');
            firstInvalid = firstInvalid || quantita;
        }

        if (!categoria.value) {
            setError(document.getElementById('categoriaError'), 'Seleziona una categoria.');
            firstInvalid = firstInvalid || categoria;
        }

        if (immagine.files.length > 0) {
            const file = immagine.files[0];
            const allowedTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];

            if (!allowedTypes.includes(file.type)) {
                setError(document.getElementById('immagineError'), 'Usa un file JPG, PNG, WEBP o GIF.');
                firstInvalid = firstInvalid || immagine;
            } else if (file.size > 10 * 1024 * 1024) {
                setError(document.getElementById('immagineError'), 'L’immagine non può superare 10 MB.');
                firstInvalid = firstInvalid || immagine;
            }
        }

        if (firstInvalid) {
            event.preventDefault();
            setError(formError, 'Correggi i campi evidenziati prima di continuare.');
            firstInvalid.focus();
        }
    });
})();
</script>

</body>
</html>
