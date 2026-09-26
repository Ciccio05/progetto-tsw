<!-- RUOLO DELLA PAGINA: Fragment riutilizzabile con i collegamenti di navigazione in base al ruolo dell’utente. -->
<!-- DIRETTIVE -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<nav class="FM-navbar" aria-label="Navigazione principale">
    <a class="FM-nav-link" href="${ctx}/home">Home</a>
    <a class="FM-nav-link" href="${ctx}/catalog">Catalogo</a>
    <a class="FM-nav-link" href="${ctx}/assistance">Assistenza</a>

    <c:choose>
        <c:when test="${not empty sessionScope.utente and sessionScope.utente.admin}">
            <a class="FM-nav-link" href="${ctx}/admin">Pannello admin</a>
            <a class="FM-nav-link" href="${ctx}/logout">Esci</a>
        </c:when>

        <c:when test="${not empty sessionScope.utente}">
            <a class="FM-nav-link" href="${ctx}/cart">Carrello</a>
            <a class="FM-nav-link" href="${ctx}/account">Area personale</a>
            <a class="FM-nav-link" href="${ctx}/orders">I miei ordini</a>
            <a class="FM-nav-link" href="${ctx}/logout">Esci</a>
        </c:when>

        <c:otherwise>
            <a class="FM-nav-link" href="${ctx}/cart">Carrello</a>
            <a class="FM-nav-link" href="${ctx}/login">Accedi</a>
            <a class="FM-nav-link" href="${ctx}/register">Registrati</a>
        </c:otherwise>
    </c:choose>
</nav>
