<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="es-EC" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Datos de cuenta · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eureka.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <div class="page-head">
                <p class="page-eyebrow">Consulta</p>
                <h1 class="page-title">Datos de cuenta</h1>
                <p class="page-help">Consulte saldo, contador de movimientos y estado de una cuenta.</p>
            </div>

            <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

            <form method="post" action="${pageContext.request.contextPath}/Cuenta">
                <div class="form-group">
                    <label class="form-label" for="cuenta">Número de cuenta</label>
                    <input type="text" id="cuenta" name="cuenta" class="form-control"
                           placeholder="Ingrese el número de cuenta" required>
                </div>

                <button type="submit" class="btn btn-primary">Consultar</button>
            </form>

            <c:if test="${cuenta != null}">
                <div class="info-card">
                    <h2>Información de la cuenta</h2>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="label">Código de cuenta</span>
                            <span class="value"><c:out value="${cuenta.codigo}"/></span>
                        </div>
                        <div class="info-item">
                            <span class="label">Saldo</span>
                            <span class="value highlight">$<fmt:formatNumber value="${cuenta.saldo}" pattern="#,##0.00" /></span>
                        </div>
                        <div class="info-item">
                            <span class="label">Contador de movimientos</span>
                            <span class="value">${cuenta.contadorMovimientos}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">Fecha de creación</span>
                            <span class="value"><c:out value="${cuenta.fechaCreacion}"/></span>
                        </div>
                        <div class="info-item">
                            <span class="label">Estado</span>
                            <span class="value"><c:out value="${cuenta.estado}"/></span>
                        </div>
                        <div class="info-item">
                            <span class="label">Sucursal</span>
                            <span class="value"><c:out value="${cuenta.sucursal}"/></span>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/Home" class="btn btn-secondary">Volver al menú</a>
            </div>
        </div>
    </div>
</body>
</html>
