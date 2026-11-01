<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transferencia · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <h1>Realizar Transferencia</h1>
            <p class="subtitle">Transfiera fondos entre cuentas; las cuentas deben ser distintas y la origen debe tener saldo</p>

            <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

            <form method="post" action="${pageContext.request.contextPath}/Transferencia">
                <div class="field">
                    <label for="cuentaOrigen">Cuenta origen</label>
                    <input type="text" id="cuentaOrigen" name="cuentaOrigen" placeholder="Ingrese la cuenta origen" required>
                </div>
                <div class="field">
                    <label for="cuentaDestino">Cuenta destino</label>
                    <input type="text" id="cuentaDestino" name="cuentaDestino" placeholder="Ingrese la cuenta destino" required>
                </div>
                <div class="field">
                    <label for="monto">Monto a transferir</label>
                    <input type="text" id="monto" name="monto" inputmode="decimal" placeholder="0.00" required>
                </div>
                <div class="actions">
                    <button type="submit" class="btn btn-primary">Realizar Transferencia</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/Home">Volver al Menú</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
