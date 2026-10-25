(() => {
    'use strict';

    // Respaldo local: solo se usa si GET /api/catalogo (puente) falla.
    // Los nombres (clave) deben coincidir con los enums del servidor.
    const UNIDADES = {
        LONGITUD: [
            ['CENTIMETRO', 'Centímetro (cm)', 'cm'],
            ['METRO', 'Metro (m)', 'm'],
            ['PIE', 'Pie (ft)', 'ft'],
            ['YARDA', 'Yarda (yd)', 'yd'],
            ['MILLA', 'Milla (mi)', 'mi']
        ],
        MASA: [
            ['GRAMO', 'Gramo (g)', 'g'],
            ['KILOGRAMO', 'Kilogramo (kg)', 'kg'],
            ['ONZA', 'Onza (oz)', 'oz'],
            ['LIBRA', 'Libra (lb)', 'lb'],
            ['QUINTAL', 'Quintal (qq)', 'qq']
        ],
        TEMPERATURA: [
            ['CELSIUS', 'Celsius (°C)', '°C'],
            ['FAHRENHEIT', 'Fahrenheit (°F)', '°F'],
            ['KELVIN', 'Kelvin (K)', 'K'],
            ['NEWTON', 'Newton (°N)', '°N'],
            ['REAUMUR', 'Réaumur (°Ré)', '°Ré']
        ]
    };

    // Catálogo vigente cargado desde GET /api/catalogo: magnitud -> [[clave, texto, simbolo], ...]
    let CATALOGO = null;

    const $ = (id) => document.getElementById(id);
    const el = {
        sesion: $('sesion'), sesionUsuario: $('sesionUsuario'), btnSalir: $('btnSalir'),
        vistaLogin: $('vistaLogin'), formLogin: $('formLogin'), errorLogin: $('errorLogin'),
        usuario: $('usuario'), contrasena: $('contrasena'), btnEntrar: $('btnEntrar'),
        vistaConversor: $('vistaConversor'), formConversion: $('formConversion'),
        valor: $('valor'), origen: $('unidadOrigen'), destino: $('unidadDestino'),
        btnIntercambiar: $('btnIntercambiar'), btnConvertir: $('btnConvertir'),
        errorConversion: $('errorConversion'),
        resultadoVacio: $('resultadoVacio'), resultadoDatos: $('resultadoDatos'),
        resNumero: $('resNumero'), resUnidad: $('resUnidad'), resDetalle: $('resDetalle'),
        historialBloque: $('historialBloque'), historial: $('historial')
    };

    const fmt = new Intl.NumberFormat('es-EC', { maximumSignificantDigits: 10 });
    const historial = [];

    // ---------------------------------------------------------------- API

    async function api(ruta, cuerpo) {
        let resp;
        try {
            resp = await fetch('api/' + ruta, {
                method: cuerpo === undefined ? 'GET' : 'POST',
                headers: cuerpo === undefined ? {} : { 'Content-Type': 'application/json' },
                body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo),
                credentials: 'same-origin'
            });
        } catch {
            throw { mensaje: 'No hay conexión con la aplicación web.', estado: 0 };
        }
        let datos = {};
        try { datos = await resp.json(); } catch { /* respuesta sin JSON */ }
        if (!resp.ok) {
            throw {
                mensaje: datos.mensaje || 'Error inesperado (HTTP ' + resp.status + ').',
                estado: resp.status,
                sesionExpirada: !!datos.sesionExpirada
            };
        }
        return datos;
    }

    // ---------------------------------------------------------------- vistas

    function mostrarLogin() {
        el.vistaConversor.hidden = true;
        el.historialBloque.hidden = true;
        el.sesion.hidden = true;
        el.vistaLogin.hidden = false;
        el.contrasena.value = '';
        el.usuario.focus();
    }

    async function mostrarConversor(usuario) {
        el.vistaLogin.hidden = true;
        el.vistaConversor.hidden = false;
        el.sesionUsuario.textContent = usuario;
        el.sesion.hidden = false;
        try {
            await cargarCatalogo();
        } catch (err) {
            if (err && err.sesionExpirada) {
                mostrarLogin();
                mostrarError(el.errorLogin, err.mensaje);
                return;
            }
        }
        cargarUnidades();
        el.valor.focus();
    }

    function ocultar(nodo) { nodo.hidden = true; nodo.textContent = ''; }
    function mostrarError(nodo, texto) { nodo.textContent = texto; nodo.hidden = false; }

    // ---------------------------------------------------------------- catálogo

    async function cargarCatalogo() {
        try {
            const r = await api('catalogo');
            const datos = r.resultado;
            if (!datos || typeof datos !== 'object') {
                throw { mensaje: 'El catálogo recibido está vacío.' };
            }
            const mapa = {};
            for (const [magnitud, unidades] of Object.entries(datos)) {
                if (!Array.isArray(unidades) || unidades.length === 0) continue;
                mapa[magnitud] = unidades.map((u) => [
                    u.clave,
                    u.clave + ' (' + u.etiqueta + ', ' + u.simbolo + ')',
                    u.simbolo
                ]);
            }
            if (Object.keys(mapa).length === 0) {
                throw { mensaje: 'El catálogo recibido está vacío.' };
            }
            CATALOGO = mapa;
        } catch (err) {
            if (err && err.sesionExpirada) {
                throw err;
            }
            // Último recurso: la tabla local empaquetada.
            CATALOGO = UNIDADES;
        }
    }

    function unidadesDe(magnitud) {
        const tabla = CATALOGO || UNIDADES;
        const lista = tabla[magnitud];
        return lista && lista.length ? lista : (UNIDADES[magnitud] || []);
    }

    function magnitudActual() {
        return document.querySelector('input[name="magnitud"]:checked').value;
    }

    function cargarUnidades() {
        const lista = unidadesDe(magnitudActual());
        for (const sel of [el.origen, el.destino]) {
            sel.replaceChildren(...lista.map(([clave, texto]) => new Option(texto, clave)));
        }
        el.destino.selectedIndex = lista.length > 1 ? 1 : 0;
        limpiarResultado();
        ocultar(el.errorConversion);
    }

    function simbolo(magnitud, clave) {
        const fila = unidadesDe(magnitud).find((u) => u[0] === clave);
        return fila ? fila[2] : clave;
    }

    function limpiarResultado() {
        el.resultadoDatos.hidden = true;
        el.resultadoVacio.hidden = false;
    }

    // ---------------------------------------------------------------- eventos

    el.formLogin.addEventListener('submit', async (e) => {
        e.preventDefault();
        ocultar(el.errorLogin);
        if (!el.usuario.value.trim() || !el.contrasena.value) {
            mostrarError(el.errorLogin, 'Ingresa usuario y contraseña.');
            return;
        }
        el.btnEntrar.disabled = true;
        try {
            const r = await api('login', {
                username: el.usuario.value.trim(),
                password: el.contrasena.value
            });
            await mostrarConversor(r.usuario);
        } catch (err) {
            mostrarError(el.errorLogin, err.mensaje);
            el.contrasena.select();
        } finally {
            el.btnEntrar.disabled = false;
        }
    });

    el.btnSalir.addEventListener('click', async () => {
        try { await api('logout', {}); } catch { /* la sesión local se descarta igual */ }
        historial.length = 0;
        el.historial.replaceChildren();
        CATALOGO = null;
        mostrarLogin();
    });

    document.querySelectorAll('input[name="magnitud"]').forEach((r) =>
        r.addEventListener('change', cargarUnidades));

    el.btnIntercambiar.addEventListener('click', () => {
        [el.origen.value, el.destino.value] = [el.destino.value, el.origen.value];
        limpiarResultado();
    });

    [el.origen, el.destino, el.valor].forEach((c) => c.addEventListener('input', limpiarResultado));

    el.formConversion.addEventListener('submit', async (e) => {
        e.preventDefault();
        ocultar(el.errorConversion);

        const texto = el.valor.value.trim().replace(',', '.');
        const valor = Number(texto);
        if (texto === '' || !Number.isFinite(valor)) {
            mostrarError(el.errorConversion, 'Ingresa un valor numérico, por ejemplo 12,5.');
            el.valor.focus();
            return;
        }

        const magnitud = magnitudActual();
        el.btnConvertir.disabled = true;
        try {
            const r = await api('convertir', {
                magnitud: magnitud,
                unidadOrigen: el.origen.value,
                unidadDestino: el.destino.value,
                valorOrigen: valor
            });
            mostrarResultado(r);
        } catch (err) {
            if (err.sesionExpirada) {
                mostrarLogin();
                mostrarError(el.errorLogin, err.mensaje);
            } else {
                mostrarError(el.errorConversion, err.mensaje);
                limpiarResultado();
            }
        } finally {
            el.btnConvertir.disabled = false;
        }
    });

    function mostrarResultado(r) {
        const d = r.resultado;
        if (!d) {
            mostrarError(el.errorConversion, r.mensaje || 'El servidor no devolvió resultado.');
            limpiarResultado();
            return;
        }

        const magnitud = d.tipoMagnitud || magnitudActual();
        const uOrigen = simbolo(magnitud, d.unidadOrigen);
        const uDestino = simbolo(magnitud, d.unidadDestino);
        const origenTxt = fmt.format(d.valorOrigen) + ' ' + uOrigen;
        const destinoTxt = fmt.format(d.valorResultado) + ' ' + uDestino;

        el.resNumero.textContent = fmt.format(d.valorResultado);
        el.resUnidad.textContent = uDestino;
        el.resDetalle.textContent = origenTxt + ' = ' + destinoTxt;
        el.resultadoVacio.hidden = true;
        el.resultadoDatos.hidden = false;

        historial.unshift([origenTxt, destinoTxt]);
        historial.splice(5);
        el.historial.replaceChildren(...historial.map(([a, b]) => {
            const li = document.createElement('li');
            const izq = document.createElement('span');
            const der = document.createElement('span');
            izq.textContent = a;
            der.textContent = b;
            li.append(izq, der);
            return li;
        }));
        el.historialBloque.hidden = false;
    }

    // ---------------------------------------------------------------- inicio

    (async () => {
        try {
            const estado = await api('estado');
            if (estado.autenticado) {
                await mostrarConversor(estado.usuario);
            } else {
                mostrarLogin();
            }
        } catch {
            mostrarLogin();
        }
    })();
})();
