<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="es-EC" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movimientos · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eureka.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <div class="page-head">
                <p class="page-eyebrow">Consulta</p>
                <h1 class="page-title">Movimientos</h1>
                <p class="page-help">Historial de movimientos y resumen de totales de una cuenta.</p>
            </div>

            <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

            <form method="post" action="${pageContext.request.contextPath}/Movimiento">
                <div class="form-group">
                    <label class="form-label" for="cuenta">Número de cuenta</label>
                    <input type="text" id="cuenta" name="cuenta" class="form-control"
                           placeholder="Ingrese el número de cuenta" required>
                </div>

                <button type="submit" class="btn btn-primary">Consultar movimientos</button>
            </form>
        </div>

        <c:if test="${resumen != null}">
            <div class="stats-grid">
                <div class="stat-card">
                    <h4>Saldo actual</h4>
                    <div class="value">$<fmt:formatNumber value="${resumen.saldoActual}" pattern="#,##0.00" /></div>
                </div>
                <div class="stat-card ingresos">
                    <h4>Total ingresos</h4>
                    <div class="value">$<fmt:formatNumber value="${resumen.totalIngresos}" pattern="#,##0.00" /></div>
                </div>
                <div class="stat-card egresos">
                    <h4>Total egresos</h4>
                    <div class="value">$<fmt:formatNumber value="${resumen.totalEgresos}" pattern="#,##0.00" /></div>
                </div>
                <div class="stat-card neto">
                    <h4>Saldo neto</h4>
                    <div class="value">$<fmt:formatNumber value="${resumen.saldoNeto}" pattern="#,##0.00" /></div>
                </div>
            </div>

            <c:choose>
                <c:when test="${not resumen.vacio}">
                    <div class="card">
                        <div class="card-title">
                            <span class="dot"></span> Historial de movimientos · cuenta <c:out value="${cuentaConsultada}"/>
                        </div>
                        <div class="table-wrap">
                            <table class="table">
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
                                    <c:forEach var="mov" items="${resumen.movimientos}">
                                        <tr>
                                            <td>${mov.numero}</td>
                                            <td><c:out value="${empty mov.fecha ? 'N/A' : mov.fecha}"/></td>
                                            <td><c:out value="${empty mov.tipoCodigo ? 'N/A' : mov.tipoCodigo}"/></td>
                                            <td><c:out value="${empty mov.tipoDescripcion ? 'N/A' : mov.tipoDescripcion}"/></td>
                                            <td class="amount ${mov.ingreso ? 'amount-credito' : 'amount-debito'}">
                                                ${mov.ingreso ? '+' : '-'}$<fmt:formatNumber value="${mov.importeAbsoluto}" pattern="#,##0.00" />
                                            </td>
                                            <td class="money">$<fmt:formatNumber value="${mov.saldo}" pattern="#,##0.00" /></td>
                                            <td><c:out value="${empty mov.cuentaReferencia ? '-' : mov.cuentaReferencia}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">No se encontraron movimientos para esta cuenta.</div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/Home" class="btn btn-secondary">Volver al menú</a>
        </div>
    </div>
</body>
</html>
