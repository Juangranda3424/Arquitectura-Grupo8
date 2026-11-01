<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transferencia · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eureka.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <div class="page-head">
                <p class="page-eyebrow">Operación</p>
                <h1 class="page-title">Realizar transferencia</h1>
                <p class="page-help">Transfiera fondos entre cuentas; la cuenta origen debe tener saldo disponible.</p>
            </div>

            <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

            <form method="post" action="${pageContext.request.contextPath}/Transferencia">
                <div class="form-group">
                    <label class="form-label" for="cuentaOrigen">Cuenta origen</label>
                    <input type="text" id="cuentaOrigen" name="cuentaOrigen" class="form-control"
                           placeholder="Ingrese la cuenta origen" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="cuentaDestino">Cuenta destino</label>
                    <input type="text" id="cuentaDestino" name="cuentaDestino" class="form-control"
                           placeholder="Ingrese la cuenta destino" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="monto">Monto a transferir</label>
                    <input type="text" id="monto" name="monto" class="form-control"
                           inputmode="decimal" placeholder="0.00" required>
                </div>

                <button type="submit" class="btn btn-primary">Realizar transferencia</button>
            </form>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/Home" class="btn btn-secondary">Volver al menú</a>
            </div>
        </div>
    </div>
</body>
</html>
