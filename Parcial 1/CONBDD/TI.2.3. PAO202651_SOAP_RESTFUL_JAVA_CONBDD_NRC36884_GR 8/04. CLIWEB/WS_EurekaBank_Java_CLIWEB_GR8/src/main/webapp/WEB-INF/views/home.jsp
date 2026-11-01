<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menú Principal · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/comun/cabecera.jspf" %>

    <div class="container">
        <div class="card">
            <h1>Bienvenido, ${username}</h1>
            <p class="subtitle">Seleccione una operación para continuar</p>

            <div class="menu-grid">
                <a class="menu-card" href="${pageContext.request.contextPath}/Deposito">
                    <strong>Depósito</strong>
                    <span>Abonar dinero a una cuenta</span>
                </a>
                <a class="menu-card" href="${pageContext.request.contextPath}/Retiro">
                    <strong>Retiro</strong>
                    <span>Debitar dinero de una cuenta</span>
                </a>
                <a class="menu-card" href="${pageContext.request.contextPath}/Transferencia">
                    <strong>Transferencia</strong>
                    <span>Mover dinero entre cuentas</span>
                </a>
                <a class="menu-card" href="${pageContext.request.contextPath}/Cuenta">
                    <strong>Datos de Cuenta</strong>
                    <span>Saldo y movimientos registrados</span>
                </a>
                <a class="menu-card" href="${pageContext.request.contextPath}/Movimiento">
                    <strong>Movimientos</strong>
                    <span>Historial completo de la cuenta</span>
                </a>
                <a class="menu-card" href="${pageContext.request.contextPath}/Logout">
                    <strong>Cerrar Sesión</strong>
                    <span>Finalizar la sesión actual</span>
                </a>
            </div>
        </div>

        <div class="card">
            <h2>Servicio RESTful</h2>
            <p class="subtitle">
                Cliente web conectado a
                <a href="http://localhost:8080/WS_EurekaBank_Java_GR8/resources">WS_EurekaBank_Java_GR8</a>
                · Grupo 8 · Arquitectura de Software
            </p>
        </div>
    </div>
</body>
</html>
