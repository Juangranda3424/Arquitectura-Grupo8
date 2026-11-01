<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Retiro · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eureka.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <div class="page-head">
                <p class="page-eyebrow">Operación</p>
                <h1 class="page-title">Realizar retiro</h1>
                <p class="page-help">Registre un retiro indicando la cuenta y el monto; el servidor valida el saldo disponible.</p>
            </div>

            <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

            <form method="post" action="${pageContext.request.contextPath}/Retiro">
                <div class="form-group">
                    <label class="form-label" for="cuenta">Número de cuenta</label>
                    <input type="text" id="cuenta" name="cuenta" class="form-control"
                           placeholder="Ingrese el número de cuenta" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="monto">Monto a retirar</label>
                    <input type="text" id="monto" name="monto" class="form-control"
                           inputmode="decimal" placeholder="0.00" required>
                </div>

                <button type="submit" class="btn btn-primary">Realizar retiro</button>
            </form>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/Home" class="btn btn-secondary">Volver al menú</a>
            </div>
        </div>
    </div>
</body>
</html>
