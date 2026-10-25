# WS_ConversionUnidades_Java_CLIWEB_GR8

Cliente web del servicio REST **WS_ConversionUnidades_Java_GR8** (Payara 7 · Jakarta EE 11).

```
Navegador ──JSON──▶ ApiServlet (este WAR) ──REST/JSON──▶ WS_ConversionUnidades_Java_GR8 (servidor)
 index.html/app.js   guarda el token en la HttpSession      /api/estado · /api/auth · /api/catalogo
                                                            /api/conversiones/{magnitud}
```

El navegador no llama al servidor REST directamente (CORS), así que un servlet hace de puente.
El token **nunca llega al navegador**: queda en la `HttpSession` del servidor (atributos
`ws.token`/`ws.usuario`).

## Requisitos
- JDK 17 o superior (Payara 7 corre con JDK 21)
- Maven 3.9+
- Payara 7 con el **servidor REST ya desplegado** (`WS_ConversionUnidades_Java_GR8.war`)

## Compilar y desplegar
```bash
mvn clean package
```
Despliega `target/WS_ConversionUnidades_Java_CLIWEB_GR8.war` en Payara
(NetBeans: *Run*, o consola de administración en `http://localhost:4848`, o
`asadmin deploy target/WS_ConversionUnidades_Java_CLIWEB_GR8.war`).

Abre: **http://localhost:8080/WS_ConversionUnidades_Java_CLIWEB_GR8/**

Credenciales del servidor: `MONSTER` / `MONSTER9`.

## Configuración (archivo `.env`)
Para apuntar a otra IP edita **`src/main/resources/.env`** y vuelve a desplegar (Run en NetBeans):

```
SERVIDOR_IP=192.168.1.50
SERVIDOR_PUERTO=8080
SERVIDOR_CONTEXTO=WS_ConversionUnidades_Java_GR8
```

Prioridad (gana el primero): propiedad de JVM (`-DSERVIDOR_IP=...`) → variable de entorno → archivo `.env` → valor por defecto.

El `.env` se busca en este orden:
1. Ruta indicada por `-DENV_FILE=/ruta/mi.env` o la variable `ENV_FILE` (se edita **sin recompilar**).
2. `.env` en el directorio de trabajo de Payara.
3. El `.env` empaquetado en el WAR.

Al arrancar, el log de Payara indica cuál se usó ("Configuración leída de: ...").

La base de la API se resuelve en este orden:
`URL_BASE` → `API_BASE` → `http://<SERVIDOR_IP>:<SERVIDOR_PUERTO>/<SERVIDOR_CONTEXTO>/api`.
Si un override no termina en `/api`, el cliente lo agrega automáticamente.

| Clave | Por defecto | Descripción |
|---|---|---|
| `SERVIDOR_IP` | `localhost` | Host del servidor REST |
| `SERVIDOR_PUERTO` | `8080` | Puerto HTTP de Payara |
| `SERVIDOR_CONTEXTO` | `WS_ConversionUnidades_Java_GR8` | Contexto del WAR del servidor |
| `API_BASE` | *(vacío)* | Override completo de la base de la API (ej. `.../WS_ConversionUnidades_Java_GR8/api`) |
| `URL_BASE` | *(vacío)* | Override completo del servidor (tiene prioridad sobre `API_BASE`) |

## Contrato del puente (navegador ↔ ApiServlet)
Forma de respuesta: siempre un objeto JSON con `mensaje`; en éxito también `resultado`
(y `usuario` en el login). Los errores de sesión añaden `sesionExpirada: true`.

| Método y ruta | Cuerpo de entrada | Éxito | Errores |
|---|---|---|---|
| `GET /api/estado` | — | `200 {mensaje, resultado, autenticado, usuario}` | `503` sin conexión, `502` respuesta inválida |
| `GET /api/catalogo` | — | `200 {mensaje, resultado}` | `401 {mensaje, sesionExpirada}` sin sesión |
| `POST /api/login` | `{"username","password"}` | `200 {usuario, mensaje}` (token en la sesión) | `400` campos faltantes, `401` mensaje del servidor |
| `POST /api/logout` | — | `200 {mensaje}` (invalida la sesión) | — |
| `POST /api/convertir` | `{magnitud, valorOrigen, unidadOrigen, unidadDestino}` | `200 {mensaje, resultado}` | `400` datos faltantes, `401` sesión, `404` magnitud, `422` validación |

Mapeo de errores del servidor: fallo de transporte → `503`, respuesta no JSON → `502`,
autenticación (HTTP 401) → `401` + `sesionExpirada`, validación → `422`, magnitud desconocida → `404`.

## Contrato REST consumido (servidor)
Envelope: `{ "estado": "OK|ERROR", "codigo": 200, "mensaje": "texto", "resultado": {} }`.

| Método y ruta | Token | Cuerpo de entrada | `resultado` (200) |
|---|---|---|---|
| `GET /api/estado` | no | — | `{servicio, version, protocolo, horaServidor}` |
| `POST /api/auth/login` | no | `{"username","password"}` | `{token, usuario, expiraEn}` |
| `POST /api/auth/logout` | sí | — | `true` |
| `GET /api/catalogo` | sí | — | `{LONGITUD:[{clave,etiqueta,simbolo}], MASA:[...], TEMPERATURA:[...]}` |
| `POST /api/conversiones/{longitud\|masa\|temperatura}` | sí | `{"valorOrigen","unidadOrigen","unidadDestino"}` | `{valorOrigen,valorResultado,unidadOrigen,unidadDestino,tipoMagnitud}` |

El token viaja en la cabecera `Authorization: Bearer <token>`. El catálogo de unidades que
alimenta los combos se carga desde `GET /api/catalogo` (puente) tras iniciar sesión; la tabla
local de `app.js` solo se usa como último recurso si esa llamada falla.

## Solución de problemas
- **"No se pudo conectar con el servidor REST"**: el servidor no está desplegado o `SERVIDOR_IP`/`SERVIDOR_PUERTO`/`SERVIDOR_CONTEXTO` no coinciden
  con su contexto. Comprueba que abra `…/WS_ConversionUnidades_Java_GR8/api/estado` en el navegador.
- **"Tu sesión terminó"**: el servidor guarda los tokens en memoria; si se redespliega o reinicia, hay que volver a iniciar sesión.
- **"El servidor REST respondió algo que no es JSON"**: URL equivocada (revisa el contexto del WAR del servidor).
- **El catálogo aparece con las etiquetas locales**: la llamada a `GET /api/catalogo` falló y se usó la tabla de respaldo de `app.js`.
