// ec.edu.monster.controlador/script.js
// Controlador del cliente web: navegacion entre pantallas y llamadas al modelo SOAP.

(function () {
    'use strict';

    const pantallaLogin = document.getElementById('pantallaLogin');
    const pantallaMenu = document.getElementById('pantallaMenu');
    const pantallaMovimiento = document.getElementById('pantallaMovimiento');
    const pantallaMovimientos = document.getElementById('pantallaMovimientos');
    const tablaMovimientos = document.getElementById('tablaMovimientos');

    function mostrar(pantalla) {
        [pantallaLogin, pantallaMenu, pantallaMovimiento, pantallaMovimientos].forEach(function (seccion) {
            seccion.classList.add('oculto');
        });
        pantalla.classList.remove('oculto');
    }

    function mensaje(elemento, texto, tipo) {
        elemento.textContent = texto;
        elemento.classList.remove('exito', 'error');
        if (tipo) {
            elemento.classList.add(tipo);
        }
    }

    // ------------------------------------------------------------- login
    document.getElementById('formLogin').addEventListener('submit', function (evento) {
        evento.preventDefault();

        const usuario = document.getElementById('usuario').value.trim();
        const contrasena = document.getElementById('contrasena').value;
        const respuesta = document.getElementById('loginResponse');

        mensaje(respuesta, 'Validando credenciales...', null);

        Modelo.login(usuario, contrasena,
            function (resultado) {
                if (resultado === 1) {
                    mensaje(respuesta, 'Bienvenido ' + usuario + ' a EurekaBank.', 'exito');
                    mostrar(pantallaMenu);
                } else {
                    mensaje(respuesta, 'Usuario o contraseña incorrectos.', 'error');
                }
            },
            function (error) {
                mensaje(respuesta, error, 'error');
            });
    });

    // ------------------------------------------------------------- menu
    document.getElementById('irMovimiento').addEventListener('click', function () {
        mensaje(document.getElementById('movimientoResponse'), '', null);
        document.getElementById('formMovimiento').reset();
        document.getElementById('grupoCuentaDestino').classList.add('oculto');
        mostrar(pantallaMovimiento);
    });

    document.getElementById('irMovimientos').addEventListener('click', function () {
        mensaje(document.getElementById('movimientosResponse'), '', null);
        tablaMovimientos.classList.add('oculto');
        mostrar(pantallaMovimientos);
    });

    document.getElementById('cerrarSesion').addEventListener('click', function () {
        document.getElementById('formLogin').reset();
        mensaje(document.getElementById('loginResponse'), '', null);
        mostrar(pantallaLogin);
    });

    document.querySelectorAll('.volverAlMenu').forEach(function (boton) {
        boton.addEventListener('click', function () {
            mostrar(pantallaMenu);
        });
    });

    // ------------------------------------------------------------- movimientos: alta
    document.getElementById('tipoMovimiento').addEventListener('change', function () {
        const esTransferencia = this.value === Modelo.TRANSFERENCIA;
        const grupo = document.getElementById('grupoCuentaDestino');
        const campo = document.getElementById('cuentaDestino');

        grupo.classList.toggle('oculto', !esTransferencia);
        campo.required = esTransferencia;
        if (!esTransferencia) {
            campo.value = '';
        }
    });

    document.getElementById('formMovimiento').addEventListener('submit', function (evento) {
        evento.preventDefault();

        const cuenta = document.getElementById('cuenta').value.trim();
        const importe = document.getElementById('importe').value.trim();
        const tipoMovimiento = document.getElementById('tipoMovimiento').value;
        const cuentaDestino = document.getElementById('cuentaDestino').value.trim();
        const respuesta = document.getElementById('movimientoResponse');

        if (tipoMovimiento === Modelo.TRANSFERENCIA && cuentaDestino === '') {
            mensaje(respuesta, 'La transferencia requiere una cuenta destino.', 'error');
            return;
        }

        mensaje(respuesta, 'Enviando movimiento...', null);

        Modelo.regDeposito(cuenta, importe, tipoMovimiento, cuentaDestino,
            function (resultado) {
                if (resultado === 1) {
                    mensaje(respuesta, 'Movimiento registrado con exito.', 'exito');
                    document.getElementById('formMovimiento').reset();
                    document.getElementById('grupoCuentaDestino').classList.add('oculto');
                } else if (resultado === -1) {
                    mensaje(respuesta, 'El servidor rechazo el movimiento (cuenta, saldo o datos invalidos).', 'error');
                } else {
                    mensaje(respuesta, 'Respuesta inesperada del servidor.', 'error');
                }
            },
            function (error) {
                mensaje(respuesta, error, 'error');
            });
    });

    // ------------------------------------------------------------- movimientos: consulta
    document.getElementById('formMovimientos').addEventListener('submit', function (evento) {
        evento.preventDefault();

        const cuenta = document.getElementById('cuentaMov').value.trim();
        const filtroTipo = document.getElementById('filtroTipo').value;
        const respuesta = document.getElementById('movimientosResponse');
        const cuerpo = tablaMovimientos.getElementsByTagName('tbody')[0];

        mensaje(respuesta, 'Consultando movimientos...', null);
        cuerpo.innerHTML = '';

        Modelo.traerMovimientos(cuenta,
            function (movimientos) {
                const visibles = movimientos.filter(function (movimiento) {
                    return filtroTipo === '' || movimiento.tipo.indexOf(filtroTipo) >= 0;
                });

                if (visibles.length === 0) {
                    tablaMovimientos.classList.add('oculto');
                    mensaje(respuesta, 'No se encontraron movimientos para la cuenta ' + cuenta + '.', null);
                    return;
                }

                visibles.forEach(function (movimiento) {
                    const fila = document.createElement('tr');
                    fila.innerHTML = '<td>' + movimiento.nromov + '</td>'
                        + '<td>' + movimiento.fecha + '</td>'
                        + '<td>' + movimiento.tipo + '</td>'
                        + '<td>' + movimiento.accion + '</td>'
                        + '<td>' + movimiento.importe + '</td>'
                        + '<td>' + movimiento.cuenta + '</td>';
                    cuerpo.appendChild(fila);
                });

                tablaMovimientos.classList.remove('oculto');
                mensaje(respuesta, 'Se encontraron ' + visibles.length + ' movimientos.', 'exito');
            },
            function (error) {
                tablaMovimientos.classList.add('oculto');
                mensaje(respuesta, error, 'error');
            });
    });
})();
