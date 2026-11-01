<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movimientos · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <h1>Consultar Movimientos</h1>
            <p class="subtitle">Historial de movimientos, totales y saldo de la cuenta</p>

            <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

            <form method="post" action="${pageContext.request.contextPath}/Movimiento">
                <div class="field">
                    <label for="cuenta">Número de cuenta</label>
                    <input type="text" id="cuenta" name="cuenta" placeholder="Ingrese el número de cuenta" required>
                </div>
                <div class="actions">
                    <button type="submit" class="btn btn-primary">Consultar Movimientos</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/Home">Volver al Menú</a>
                </div>
            </form>
        </div>

        <c:if test="${movimientos != null}">
            <div class="card">
                <h2>Resumen de la Cuenta</h2>

                <div class="stats">
                    <div class="stat">
                        <span>Saldo actual</span>
                        <strong>$<fmt:formatNumber value="${saldoActual}" type="number" minFractionDigits="2" maxFractionDigits="2" /></strong>
                    </div>
                    <div class="stat ingresos">
                        <span>Total ingresos</span>
                        <strong>$<fmt:formatNumber value="${totalIngresos}" type="number" minFractionDigits="2" maxFractionDigits="2" /></strong>
                    </div>
                    <div class="stat egresos">
                        <span>Total egresos</span>
                        <strong>$<fmt:formatNumber value="${totalEgresos}" type="number" minFractionDigits="2" maxFractionDigits="2" /></strong>
                    </div>
                    <div class="stat neto">
                        <span>Saldo neto</span>
                        <strong>$<fmt:formatNumber value="${saldoNeto}" type="number" minFractionDigits="2" maxFractionDigits="2" /></strong>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty movimientos}">
                        <div class="table-wrap">
                            <table class="data">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Fecha</th>
                                        <th>Tipo</th>
                                        <th>Descripción</th>
                                        <th>Importe</th>
                                        <th>Saldo</th>
                                        <th>Cuenta referencia</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="mov" items="${movimientos}">
                                        <tr>
                                            <td>${mov.numeroMovimiento}</td>
                                            <td>${mov.fechaMovimiento != null ? mov.fechaMovimiento : 'N/A'}</td>
                                            <td>${mov.codigoTipoMovimiento != null ? mov.codigoTipoMovimiento : 'N/A'}</td>
                                            <td>${mov.tipoDescripcion != null ? mov.tipoDescripcion : 'N/A'}</td>
                                            <td class="monto">$<fmt:formatNumber value="${mov.importeMovimiento}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
                                            <td class="money">$<fmt:formatNumber value="${mov.saldo}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
                                            <td>${mov.cuentaReferencia != null ? mov.cuentaReferencia : '-'}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">No se encontraron movimientos para esta cuenta.</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>
</body>
</html>
