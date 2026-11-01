// ec.edu.monster.modelo/modelo.js
// Modelo SOAP del cliente web: construye el sobre, lo envia al servidor WCF
// WS_EurekaBank_Donet_GR8 y traduce la respuesta XML.

const Modelo = {
    // Endpoint del servidor WCF de TI.2.2 (IIS Express)
    URL: 'http://localhost:8090/WS_EurekaBank_Donet_GR8/webservicio/WSEureka.svc',
    NAMESPACE: 'http://ws.monster.edu.ec/',

    // Codigos de tipo de movimiento que entiende el servidor
    DEPOSITO: '003',
    RETIRO: '004',
    TRANSFERENCIA: '008',

    accion: function (operacion) {
        return this.NAMESPACE + 'WSEureka/' + operacion;
    },

    escapar: function (valor) {
        return String(valor === null || valor === undefined ? '' : valor)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;');
    },

    sobre: function (operacion, contenido) {
        // El envoltorio y los parametros viajan en el namespace del contrato WCF
        // (el esquema del servicio es elementFormDefault="qualified").
        return '<?xml version="1.0" encoding="utf-8"?>'
            + '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">'
            + '<soapenv:Header/>'
            + '<soapenv:Body><' + operacion + ' xmlns="' + this.NAMESPACE + '">' + contenido + '</' + operacion + '></soapenv:Body>'
            + '</soapenv:Envelope>';
    },

    /**
     * Envia un sobre SOAP 1.1 al servicio. alRecibir(xmlDoc) o alFallar(mensaje).
     */
    enviarSolicitudSOAP: function (operacion, contenido, alRecibir, alFallar) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', this.URL, true);
        xhr.setRequestHeader('Content-Type', 'text/xml; charset=utf-8');
        xhr.setRequestHeader('SOAPAction', '"' + this.accion(operacion) + '"');

        xhr.onreadystatechange = function () {
            if (xhr.readyState !== 4) {
                return;
            }

            if (xhr.status === 200) {
                const documento = new DOMParser().parseFromString(xhr.responseText, 'application/xml');
                if (documento.getElementsByTagName('parsererror').length > 0) {
                    alFallar('Respuesta SOAP invalida: ' + xhr.responseText);
                    return;
                }

                alRecibir(documento);
            } else {
                alFallar('Error HTTP ' + xhr.status + ' al invocar ' + operacion + '.');
            }
        };

        xhr.onerror = function () {
            alFallar('No se pudo contactar al servidor SOAP en ' + Modelo.URL + '.');
        };

        xhr.send(this.sobre(operacion, contenido));
    },

    // ---------------------------------------------------------------- operaciones

    login: function (usuario, contrasena, alRecibir, alFallar) {
        const contenido = '<usuario>' + this.escapar(usuario) + '</usuario>'
            + '<contrasena>' + this.escapar(contrasena) + '</contrasena>';

        this.enviarSolicitudSOAP('login', contenido, function (documento) {
            alRecibir(Modelo.numero(documento, 'loginResult'));
        }, alFallar);
    },

    regDeposito: function (cuenta, importe, tipoMovimiento, cuentaDestino, alRecibir, alFallar) {
        const contenido = '<cuenta>' + this.escapar(cuenta) + '</cuenta>'
            + '<importe>' + this.escapar(importe) + '</importe>'
            + '<tipoMovimiento>' + this.escapar(tipoMovimiento) + '</tipoMovimiento>'
            + '<cuentaDestino>' + this.escapar(cuentaDestino) + '</cuentaDestino>';

        this.enviarSolicitudSOAP('regDeposito', contenido, function (documento) {
            alRecibir(Modelo.numero(documento, 'regDepositoResult'));
        }, alFallar);
    },

    traerMovimientos: function (cuenta, alRecibir, alFallar) {
        const contenido = '<cuenta>' + this.escapar(cuenta) + '</cuenta>';

        this.enviarSolicitudSOAP('traerMovimientos', contenido, function (documento) {
            alRecibir(Modelo.movimientos(documento));
        }, alFallar);
    },

    // ---------------------------------------------------------------- parseo

    numero: function (documento, etiqueta) {
        const nodos = documento.getElementsByTagName(etiqueta);
        if (nodos.length === 0) {
            return -1;
        }

        const valor = parseInt(nodos[0].textContent, 10);
        return isNaN(valor) ? -1 : valor;
    },

    textoHijo: function (nodo, etiqueta) {
        const hijos = nodo.getElementsByTagName(etiqueta);
        return hijos.length > 0 ? hijos[0].textContent : '';
    },

    movimientos: function (documento) {
        const lista = [];
        const nodos = documento.getElementsByTagName('movimiento');

        for (let i = 0; i < nodos.length; i++) {
            lista.push({
                nromov: this.textoHijo(nodos[i], 'nromov'),
                fecha: this.textoHijo(nodos[i], 'fecha'),
                tipo: this.textoHijo(nodos[i], 'tipo'),
                accion: this.textoHijo(nodos[i], 'accion'),
                importe: this.textoHijo(nodos[i], 'importe'),
                cuenta: this.textoHijo(nodos[i], 'cuenta')
            });
        }

        return lista;
    }
};
