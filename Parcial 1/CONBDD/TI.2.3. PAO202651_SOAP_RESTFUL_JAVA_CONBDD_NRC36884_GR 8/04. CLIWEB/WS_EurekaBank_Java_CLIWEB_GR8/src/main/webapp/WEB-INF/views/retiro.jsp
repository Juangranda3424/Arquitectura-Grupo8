<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Retiro · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <h1>Realizar Retiro</h1>
            <p class="subtitle">Registre un retiro indicando la cuenta y el monto; se valida el saldo disponible</p>

            <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

            <form method="post" action="${pageContext.request.contextPath}/Retiro">
                <div class="field">
                    <label for="cuenta">Número de cuenta</label>
                    <input type="text" id="cuenta" name="cuenta" placeholder="Ingrese el número de cuenta" required>
                </div>
                <div class="field">
                    <label for="monto">Monto a retirar</label>
                    <input type="text" id="monto" name="monto" inputmode="decimal" placeholder="0.00" required>
                </div>
                <div class="actions">
                    <button type="submit" class="btn btn-primary">Realizar Retiro</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/Home">Volver al Menú</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
