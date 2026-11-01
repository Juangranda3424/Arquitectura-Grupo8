<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión · EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body class="login-page">
    <div class="login-card">
        <div class="login-head">
            <svg width="56" height="56" viewBox="0 0 48 48" aria-hidden="true">
                <rect x="1" y="1" width="46" height="46" rx="12" fill="#0FA3B1"/>
                <path d="M16 14h16v5H22v5h9v5h-9v5h10v5H16z" fill="#FFFFFF"/>
            </svg>
            <h1>EurekaBank</h1>
            <small>Grupo 8 · Arquitectura de Software</small>
            <span class="tag">Iniciar Sesión</span>
        </div>

        <%@ include file="/WEB-INF/views/comun/alertas.jspf" %>

        <form method="post" action="${pageContext.request.contextPath}/Login">
            <div class="field">
                <label for="username">Usuario</label>
                <input type="text" id="username" name="username" placeholder="Ingrese su usuario" required autofocus>
            </div>
            <div class="field">
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" placeholder="Ingrese su contraseña" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Iniciar Sesión</button>
        </form>

        <p class="footer-note">Usuarios de prueba: MONSTER / MONSTER9 · admin / admin</p>
    </div>
</body>
</html>
