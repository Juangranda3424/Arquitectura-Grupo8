<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menú principal · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eureka.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <div class="page-head">
                <p class="page-eyebrow">Menú principal</p>
                <h1 class="page-title">Bienvenido, <c:out value="${username}"/></h1>
                <p class="page-help">Seleccione una operación para continuar.</p>
            </div>

            <div class="menu-grid">
                <a href="${pageContext.request.contextPath}/Deposito" class="menu-card">
                    <h3>Depósito</h3>
                    <p>Realizar un depósito a una cuenta</p>
                </a>

                <a href="${pageContext.request.contextPath}/Retiro" class="menu-card">
                    <h3>Retiro</h3>
                    <p>Realizar un retiro de una cuenta</p>
                </a>

                <a href="${pageContext.request.contextPath}/Transferencia" class="menu-card">
                    <h3>Transferencia</h3>
                    <p>Transferir dinero entre cuentas</p>
                </a>

                <a href="${pageContext.request.contextPath}/Cuenta" class="menu-card">
                    <h3>Datos de Cuenta</h3>
                    <p>Consultar información de una cuenta</p>
                </a>

                <a href="${pageContext.request.contextPath}/Movimiento" class="menu-card">
                    <h3>Movimientos</h3>
                    <p>Ver historial de movimientos</p>
                </a>
            </div>
        </div>

        <div class="card">
            <div class="info-card" style="margin-top: 0;">
                <h2>EurekaBank · Servicios web SOAP</h2>
                <div class="info-grid">
                    <div class="info-item">
                        <span class="label">Servidor</span>
                        <span class="value">WS_EurekaBank_Java_GR8</span>
                    </div>
                    <div class="info-item">
                        <span class="label">Servicios</span>
                        <span class="value">WSLogin · WSCuenta · WSMovimiento</span>
                    </div>
                    <div class="info-item">
                        <span class="label">Grupo</span>
                        <span class="value highlight">GR8</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
