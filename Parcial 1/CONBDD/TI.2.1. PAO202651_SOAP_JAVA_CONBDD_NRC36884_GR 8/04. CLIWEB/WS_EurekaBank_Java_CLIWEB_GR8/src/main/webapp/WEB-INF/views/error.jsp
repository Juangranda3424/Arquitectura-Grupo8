<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    Pantalla de error compartida (web.xml manda aquí los 404, los 500 y cualquier
    excepción no controlada).
--%>
<c:set var="estado" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<c:set var="mensaje" value="${requestScope['jakarta.servlet.error.message']}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eureka.css">
</head>
<body class="error-body">
    <div class="container">
        <div class="card">
            <div class="page-head">
                <p class="page-eyebrow">EurekaBank · GR8</p>
                <h1 class="page-title">
                    <c:choose>
                        <c:when test="${estado == 404}">Página no encontrada</c:when>
                        <c:otherwise>Ocurrió un error inesperado</c:otherwise>
                    </c:choose>
                </h1>
                <p class="page-help">
                    <c:choose>
                        <c:when test="${not empty mensaje}"><c:out value="${mensaje}"/></c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${estado == 404}">La ruta solicitada no existe en el cliente web.</c:when>
                                <c:otherwise>Vuelva a intentarlo; si el problema continúa, revise la conexión con el servidor SOAP.</c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </p>
                <c:if test="${not empty estado}">
                    <p class="page-help">Código de estado: <c:out value="${estado}"/></p>
                </c:if>
            </div>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/Home" class="btn btn-primary">Volver al menú</a>
                <a href="${pageContext.request.contextPath}/Login" class="btn btn-secondary">Ir al acceso</a>
            </div>
        </div>
    </div>
</body>
</html>
