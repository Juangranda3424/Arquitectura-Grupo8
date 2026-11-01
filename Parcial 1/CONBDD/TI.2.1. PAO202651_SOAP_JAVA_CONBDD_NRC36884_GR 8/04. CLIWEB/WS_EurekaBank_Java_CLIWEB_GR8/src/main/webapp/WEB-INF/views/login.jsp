<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acceso · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eureka.css">
</head>
<body class="login-body">
    <div class="login-wrapper">
        <section class="login-panel">
            <div class="login-card">
                <h1>Iniciar sesión</h1>
                <p class="login-sub">Ingrese sus credenciales para operar en EurekaBank.</p>

                <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

                <form method="post" action="${pageContext.request.contextPath}/Login">
                    <div class="form-group">
                        <label class="form-label" for="username">Usuario</label>
                        <input type="text" id="username" name="username" class="form-control"
                               placeholder="Ingrese su usuario" required autofocus>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="password">Contraseña</label>
                        <input type="password" id="password" name="password" class="form-control"
                               placeholder="Ingrese su contraseña" required>
                    </div>

                    <button type="submit" class="btn btn-primary">Entrar</button>
                </form>

                <p class="page-help" style="margin-top: 1.25rem;">
                    Usuario de prueba: MONSTER / MONSTER9
                </p>
            </div>
        </section>

        <aside class="brand-panel">
            <svg class="brand-panel-logo" viewBox="0 0 48 48" role="img" aria-label="EurekaBank">
                <rect x="1.5" y="1.5" width="45" height="45" rx="13" fill="#0FA3B1" stroke="#F2A541" stroke-width="3"/>
                <path d="M17 13h16v5H22v4h10v5H22v6h11v5H17z" fill="#FFFFFF"/>
            </svg>
            <h2>EurekaBank</h2>
            <p class="brand-panel-sub">Grupo 8 · Arquitectura de Software</p>
            <ul>
                <li>Depósitos y retiros en línea</li>
                <li>Transferencias entre cuentas</li>
                <li>Consulta de saldos y movimientos</li>
            </ul>
        </aside>
    </div>
</body>
</html>
