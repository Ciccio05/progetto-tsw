<!-- RUOLO DELLA PAGINA: Fragment riutilizzabile che compone l’intestazione e include la barra di navigazione. -->
<header class="FM-header">

    <div class="FM-container FM-header-inner">

        <a class="FM-brand-link"
           href="${pageContext.request.contextPath}/home"
           aria-label="AthliX - Home">
            <span class="FM-brand">AthliX</span>
        </a>

        <jsp:include page="/WEB-INF/fragments/navbar.jsp" />

    </div>

</header>
