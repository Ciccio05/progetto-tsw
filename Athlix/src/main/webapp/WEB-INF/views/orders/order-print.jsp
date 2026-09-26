<!-- RUOLO DELLA PAGINA: Versione stampabile del dettaglio ordine/fattura. -->
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

    <title>
        Fattura ordine #${ordine.idOrdine} - AthliX
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page FM-print-page">

<header class="FM-print-header">

    <div>

        <h1>
            AthliX
        </h1>

        <p>
            Fattura / riepilogo d'acquisto
        </p>

    </div>

    <div class="FM-print-no-print">

        <button class="FM-button FM-button-primary"
                type="button"
                onclick="window.print()">

            Stampa

        </button>

        <c:url var="detailUrl"
               value="/order-detail">

            <c:param
                    name="id"
                    value="${ordine.idOrdine}" />

        </c:url>

        <a class="FM-button FM-button-light"
           href="${detailUrl}">

            Torna all'ordine

        </a>

    </div>

</header>

<main class="FM-print-document">

    <section class="FM-print-section">

        <div class="FM-print-title-row">

            <div>

                <h2>
                    Ordine #
                    <c:out value="${ordine.idOrdine}" />
                </h2>

                <p>

                    Data:

                    <fmt:formatDate
                            value="${ordine.dataOrdine}"
                            pattern="dd/MM/yyyy" />

                </p>

            </div>

            <div>

                <strong>
                    Stato
                </strong>

                <p>
                    <c:out value="${ordine.stato}" />
                </p>

            </div>

        </div>

    </section>

    <section class="FM-print-section">

        <h3>
            Cliente
        </h3>

        <div class="FM-print-grid">

            <div>

                <strong>
                    Nome
                </strong>

                <p>
                    <c:out value="${cliente.nomeUtente}" />
                    <c:out value="${cliente.cognomeUtente}" />
                </p>

            </div>

            <div>

                <strong>
                    Email
                </strong>

                <p>
                    <c:out value="${cliente.email}" />
                </p>

            </div>

            <div>

                <strong>
                    Telefono
                </strong>

                <p>

                    <c:choose>

                        <c:when test="${not empty cliente.numeroTelefono}">
                            <c:out value="${cliente.numeroTelefono}" />
                        </c:when>

                        <c:otherwise>
                            Non specificato
                        </c:otherwise>

                    </c:choose>

                </p>

            </div>

        </div>

    </section>

    <section class="FM-print-section">

        <h3>
            Spedizione
        </h3>

        <div class="FM-print-grid">

            <div>

                <strong>
                    Indirizzo
                </strong>

                <p>
                    <c:out value="${ordine.indirizzoSpedizione}" />
                </p>

            </div>

            <div>

                <strong>
                    Stato spedizione
                </strong>

                <p>
                    <c:out value="${ordine.statoSpedizione}" />
                </p>

            </div>

            <div>

                <strong>
                    Data partenza
                </strong>

                <p>

                    <c:choose>

                        <c:when test="${not empty ordine.dataPartenza}">

                            <fmt:formatDate
                                    value="${ordine.dataPartenza}"
                                    pattern="dd/MM/yyyy" />

                        </c:when>

                        <c:otherwise>
                            Non ancora disponibile
                        </c:otherwise>

                    </c:choose>

                </p>

            </div>

            <div>

                <strong>
                    Data consegna
                </strong>

                <p>

                    <c:choose>

                        <c:when test="${not empty ordine.dataConsegna}">

                            <fmt:formatDate
                                    value="${ordine.dataConsegna}"
                                    pattern="dd/MM/yyyy" />

                        </c:when>

                        <c:otherwise>
                            Non ancora disponibile
                        </c:otherwise>

                    </c:choose>

                </p>

            </div>

        </div>

    </section>

    <section class="FM-print-section">

        <h3>
            Prodotti
        </h3>

        <div class="FM-print-table-wrapper">

            <table class="FM-print-table">

                <thead>

                <tr>

                    <th>
                        Prodotto
                    </th>

                    <th>
                        Quantità
                    </th>

                    <th>
                        Prezzo unitario
                    </th>

                    <th>
                        IVA
                    </th>

                    <th>
                        Subtotale
                    </th>

                </tr>

                </thead>

                <tbody>

                <c:forEach var="item"
                           items="${dettaglio}">

                    <tr>

                        <td>
                            <c:out value="${item.nomeProdotto}" />
                        </td>

                        <td>
                            <c:out value="${item.quantita}" />
                        </td>

                        <td>

                            <fmt:formatNumber
                                    value="${item.prezzo}"
                                    type="currency"
                                    currencySymbol="€"
                                    minFractionDigits="2"
                                    maxFractionDigits="2" />

                        </td>

                        <td>

                            <fmt:formatNumber
                                    value="${item.iva}"
                                    minFractionDigits="0"
                                    maxFractionDigits="2" />

                            %

                        </td>

                        <td>

                            <fmt:formatNumber
                                    value="${item.subtotale}"
                                    type="currency"
                                    currencySymbol="€"
                                    minFractionDigits="2"
                                    maxFractionDigits="2" />

                        </td>

                    </tr>

                </c:forEach>

                </tbody>

            </table>

        </div>

    </section>

    <section class="FM-print-section FM-print-total">

        <span>
            Totale ordine
        </span>

        <strong>

            <fmt:formatNumber
                    value="${ordine.totale}"
                    type="currency"
                    currencySymbol="€"
                    minFractionDigits="2"
                    maxFractionDigits="2" />

        </strong>

    </section>

    <p class="FM-print-note">

        Documento stampabile realizzato per il progetto didattico AthliX.
        La stampa è ottimizzata tramite media query CSS; non sostituisce un documento fiscale ufficiale.

    </p>

</main>

</body>
</html>
