<!-- RUOLO DELLA PAGINA: Pagina di errore mostrata quando l’utente non possiede i permessi necessari. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html;charset=UTF-8"
         language="java"
         isErrorPage="true" %>

<!DOCTYPE html>

<html lang="it">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Errore 403 - AthliX</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="FM-page">

<main class="FM-error-page">

    <section class="FM-error-card">

        <a class="FM-brand-link"
           href="${pageContext.request.contextPath}/home">

            <span class="FM-brand">
                AthliX
            </span>

        </a>

        <p class="FM-error-code">
            403
        </p>

        <h1 class="FM-error-title">
            Accesso negato
        </h1>

        <p class="FM-error-text">
            Non hai i permessi necessari per visualizzare questa pagina.
        </p>

        <div class="FM-page-actions">

            <a class="FM-button FM-button-primary"
               href="${pageContext.request.contextPath}/home">

                Torna alla Home

            </a>

            <a class="FM-button FM-button-light"
               href="${pageContext.request.contextPath}/login">

                Vai al login

            </a>

        </div>

    </section>

</main>

</body>

</html>
