<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Datos de Cuenta · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <h1>Consultar Datos de Cuenta</h1>
            <p class="subtitle">Saldo, contador de movimientos y datos generales de la cuenta</p>

            <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

            <form method="post" action="${pageContext.request.contextPath}/Cuenta">
                <div class="field">
                    <label for="cuenta">Número de cuenta</label>
                    <input type="text" id="cuenta" name="cuenta" placeholder="Ingrese el número de cuenta" required>
                </div>
                <div class="actions">
                    <button type="submit" class="btn btn-primary">Consultar</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/Home">Volver al Menú</a>
                </div>
            </form>
        </div>

        <c:if test="${cuenta != null}">
            <div class="card">
                <h2>Información de la Cuenta</h2>
                <p class="subtitle">${cuenta.chrCuenCodigo}</p>

                <div class="info-grid">
                    <div class="info-item">
                        <span>Código de cuenta</span>
                        <strong>${cuenta.chrCuenCodigo}</strong>
                    </div>
                    <div class="info-item">
                        <span>Saldo</span>
                        <strong class="money">$<fmt:formatNumber value="${cuenta.decCuenSaldo}" type="number" minFractionDigits="2" maxFractionDigits="2" /></strong>
                    </div>
                    <div class="info-item">
                        <span>Contador de movimientos</span>
                        <strong>${cuenta.intCuenContMov}</strong>
                    </div>
                    <div class="info-item">
                        <span>Fecha de creación</span>
                        <strong>
                            <c:choose>
                                <c:when test="${not empty cuenta.dttCuenFechaCreacion}">${cuenta.dttCuenFechaCreacion}</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </strong>
                    </div>
                    <div class="info-item">
                        <span>Estado</span>
                        <strong>${cuenta.vchCuenEstado}</strong>
                    </div>
                    <div class="info-item">
                        <span>Sucursal</span>
                        <strong>${cuenta.chrSucucodigo}</strong>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</body>
</html>
