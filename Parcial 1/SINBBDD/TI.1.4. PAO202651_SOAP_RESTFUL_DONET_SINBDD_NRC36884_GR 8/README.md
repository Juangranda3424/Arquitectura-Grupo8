# TI.1.4 — Conversión de Unidades Monster (SOA con RESTful .NET · SIN BDD)

Variante **RESTful** del sistema de conversión de unidades del grupo 8 en .NET.
Replica la estructura de `TI.1.2. PAO202651_SOAP_DONET_SINBDD_NRC36884_GR 8/`
(servidor + 4 clientes) cambiando **solo la capa de exposición**: el servidor
ya no publica servicios WCF/SOAP sino una **API REST (ASP.NET Core Web API, .NET 10) con JSON**,
y los cuatro clientes consumen HTTP/JSON en lugar de XML/SOAP.

La lógica de negocio (factores, fórmulas, validaciones y mensajes) es la de TI.1.2,
y el contrato REST es el mismo de TI.1.3 (Java), de modo que los tres entregables
son comparables operación por operación.

## Inicio rápido

1. Levanta el servidor: `dotnet run` en `04. SERVIDOR/WS_ConversionUnidades_Donet_GR8/WS_ConversionUnidades_Donet_GR8` (escucha en `:5080`).
2. Comprueba que responda: `curl http://localhost:5080/api/estado`.
3. Ejecuta cualquier cliente e inicia sesión con **`MONSTER` / `MONSTER9`**.

## Estructura

```
TI.1.4. PAO202651_SOAP_RESTFUL_DONET_SINBDD_NRC36884_GR 8/
└── 04. SERVIDOR/WS_ConversionUnidades_Donet_GR8         Servidor REST (ASP.NET Core Web API · net10.0) + pruebas xUnit
└── 04. CLICON/WS_ConversionUnidades_Donet_CLICON_GR8    Cliente de consola (.NET 10)
└── 04. CLIESC/WS_ConversionUnidades_Donet_CLIESC_GR8    Cliente de escritorio (WinForms · net10.0-windows)
└── 04. CLIWEB/WS_ConversionUnidades_Donet_CLIWEB_GR8    Cliente web (ASP.NET Core MVC · .NET 10)
└── 04. CLIMOV/WS_ConversionUnidades_Donet_CLIMOV_GR8    Cliente móvil (Android · Kotlin + Compose)
```

## Arquitectura

```
CLICON (consola)  ─┐
CLIESC (WinForms) ─┤   HTTP + JSON + Bearer           ASP.NET Core (:5080/api)
CLIMOV (Android)  ─┼──────────────────────────────▶   WS_ConversionUnidades_Donet_GR8
CLIWEB (navegador)─┘   (CLIWEB es una app MVC; el      ├─ /api/estado
                    token queda en la sesión del       ├─ /api/auth/login | logout
                    servidor web, nunca en el browser) ├─ /api/catalogo
                                                       └─ /api/conversiones/{magnitud}
```

- **Sin base de datos**: usuario, tokens y catálogo viven en memoria (SINBDD).
- **Capas** en el servidor: `api/recursos` (controladores), `api/soporte` (envelope y errores),
  `seguridad` (token y validaciones), `servicio`, `modelo`, `dto`.
- El token de sesión se emite en el login y viaja en `Authorization: Bearer <token>`.
  Igual que en TI.1.2, **no vence**: solo se invalida con el logout o al reiniciar el servidor.

## Contrato REST

URL base: `http://<host>:5080/api`. La API también responde bajo
`http://<host>:5080/WS_ConversionUnidades_Donet_GR8/api` (equivalente al contexto del WAR de TI.1.3).

Toda respuesta es un envelope JSON (en éxito y en error):

```json
{ "estado": "OK | ERROR", "codigo": 200, "mensaje": "texto", "resultado": { } }
```

| Método y ruta | Token | Cuerpo | `resultado` (200) | Errores |
| --- | --- | --- | --- | --- |
| `GET /api/estado` | no | — | `{servicio, version, protocolo, horaServidor}` | — |
| `POST /api/auth/login` | no | `{"username","password"}` | `{token, usuario, expiraEn}` (`expiraEn` siempre `""`) | 400 campos faltantes · 401 `"Usuario invalido"` |
| `POST /api/auth/logout` | sí | — | `true` | 401 token ausente/inválido |
| `GET /api/catalogo` | sí | — | `{LONGITUD:[{clave,etiqueta,simbolo}], MASA:[...], TEMPERATURA:[...]}` | 401 |
| `POST /api/conversiones/{longitud\|masa\|temperatura}` | sí | `{"valorOrigen","unidadOrigen","unidadDestino"}` | `{valorOrigen, valorResultado, unidadOrigen, unidadDestino, tipoMagnitud}` | 400 datos/unidad inválidos · 401 · 404 magnitud desconocida |

Reglas:

- `{magnitud}` no distingue mayúsculas; un `tipoMagnitud` en el cuerpo se ignora (manda la ruta).
- Unidades admitidas:
  - **LONGITUD**: `CENTIMETRO`, `METRO`, `PIE`, `YARDA`, `MILLA`
  - **MASA**: `GRAMO`, `KILOGRAMO`, `ONZA`, `LIBRA`, `QUINTAL`
  - **TEMPERATURA**: `CELSIUS`, `FAHRENHEIT`, `KELVIN`, `NEWTON`, `REAUMUR`
- Los cuerpos deben enviarse como `application/json` (otro tipo → 415); un cuerpo que no es un objeto JSON → 400.
- El servidor devuelve precisión `double` completa (sin redondear).
- Errores 401: `"Acceso denegado: falta el encabezado Authorization con el esquema Bearer."` /
  `"Acceso denegado: Token inválido o sesión expirada."`
- Rutas inexistentes (404), métodos no permitidos (405) y errores internos (500) también llegan con el envelope.
- No hay CORS habilitado (igual que TI.1.3): los clientes no son páginas que llamen a la API desde el navegador.

## Servidor

Requisitos: SDK de **.NET 10**.

```bash
cd "04. SERVIDOR/WS_ConversionUnidades_Donet_GR8/WS_ConversionUnidades_Donet_GR8"
dotnet run                 # perfil "http" de launchSettings.json → http://*:5080
```

Alternativa con la DLL compilada (sin `launchSettings.json`, por eso se indica el puerto):

```bash
dotnet build
dotnet bin/Debug/net10.0/WS_ConversionUnidades_Donet_GR8.dll --urls "http://*:5080"
```

La solución incluye `nuget.config`, que agrega `nuget.org` como origen de paquetes para que la
restauración funcione aunque la máquina solo tenga orígenes offline.

Prueba rápida:

```bash
curl http://localhost:5080/api/estado
curl -X POST http://localhost:5080/api/auth/login \
     -H "Content-Type: application/json" -d '{"username":"MONSTER","password":"MONSTER9"}'
```

Credenciales (en memoria, SINBDD): **`MONSTER` / `MONSTER9`**.

Pruebas del servidor (desde `04. SERVIDOR/WS_ConversionUnidades_Donet_GR8`): `dotnet test` —
115 pruebas xUnit (modelo, servicios, validadores y capa REST con `WebApplicationFactory`).

## Clientes

CLICON, CLIESC y CLIWEB comparten la misma sección de configuración en su `appsettings.json`:

| Clave | Default | Significado |
| --- | --- | --- |
| `ServicioRest:UrlBase` | `http://localhost:5080/api` | URL base de la API (los recursos cuelgan de aquí) |
| `ServicioRest:TiempoEsperaSegundos` | `15` | tiempo máximo de espera por petición |

Tras el login, los tres cargan las unidades desde `GET /api/catalogo` (no tienen lista local de respaldo).

### CLICON — consola
```bash
cd "04. CLICON/WS_ConversionUnidades_Donet_CLICON_GR8/WS_ConversionUnidades_Donet_CLICON_GR8"
dotnet run
```
Lee `appsettings.json` junto al ejecutable (se copia al compilar).

### CLIESC — escritorio (WinForms)
```bash
cd "04. CLIESC/WS_ConversionUnidades_Donet_CLIESC_GR8/WS_ConversionUnidades_Donet_CLIESC_GR8"
dotnet run
```
Ventanas `FrmLogin` y `FrmConversor`. Requiere Windows. Lee `appsettings.json` junto al ejecutable.

### CLIWEB — web (ASP.NET Core MVC)
```bash
cd "04. CLIWEB/WS_ConversionUnidades_Donet_CLIWEB_GR8/WS_ConversionUnidades_Donet_CLIWEB_GR8"
dotnet run
```
Abrir `http://localhost:5081/` (el puerto 5080 lo usa el servidor REST). El token queda en la
sesión del servidor web (cookie `HttpOnly`); el navegador nunca lo ve.

### CLIMOV — Android (Kotlin + Compose)
`HttpURLConnection` + `org.json` (sin dependencias de terceros). La URL base se toma de `.env`
en la raíz del proyecto (copiar `.env.example`):

```
URL_BASE=http://10.0.2.2:5080/api
API_BASE=
```

- `10.0.2.2` es el loopback del host visto desde el emulador (es el valor por defecto).
- Para un dispositivo físico, usa la IP LAN del host: `URL_BASE=http://192.168.1.50:5080/api`.
- `API_BASE`, si no está vacío, tiene prioridad sobre `URL_BASE`.
- Tras editar `.env`, recompilar para regenerar `BuildConfig`.

Abrir en Android Studio y *Run*. El catálogo llena los desplegables tras el login; si falla, usa las
listas locales de `Constantes`. Ver el `README.md` del proyecto.

## Diferencias con TI.1.2 (SOAP)

| Área | TI.1.2 | TI.1.4 |
| --- | --- | --- |
| Servidor | WCF en .NET Framework 4.7.2 (`WSAutenticador.svc`, `WSConversorUnidades.svc` + WSDL) | ASP.NET Core Web API en .NET 10 (`/api`, JSON) |
| Contrato | XML/SOAP | JSON + envelope `{estado,codigo,mensaje,resultado}` |
| Autenticación | encabezado SOAP `<Token>` (namespace `http://localhost:8080/`) | `Authorization: Bearer <token>` |
| Token | `UUID+usuario`, sin vencimiento | igual, sin vencimiento, en un registro seguro ante concurrencia |
| Catálogo | fijo en cada cliente | `GET /api/catalogo` alimenta los menús de los clientes |
| Errores | SOAP faults / literales | códigos HTTP + `mensaje` en el envelope |
| Respuesta de conversión | solo el `double` | DTO completo (valores, unidades y magnitud) |
| Configuración de clientes | URLs de los `.svc` | `ServicioRest:UrlBase` (CLIMOV: `URL_BASE`) |

## Solución de problemas

- **"No se pudo conectar con el servidor REST"**: el servidor no está levantado o `UrlBase`/`URL_BASE`
  no apunta a él. Comprueba que abra `http://<host>:5080/api/estado`.
- **El servidor arranca en otro puerto**: al ejecutar la DLL directamente no se aplica `launchSettings.json`;
  agrega `--urls "http://*:5080"`.
- **CLIWEB no arranca ("address already in use")**: el puerto 5081 está ocupado; CLIWEB no debe usar el 5080.
- **401 en todo**: el servidor se reinició (los tokens viven en memoria) o se hizo logout: vuelve a iniciar sesión.
- **404 en una conversión**: la magnitud de la ruta debe ser `longitud`, `masa` o `temperatura`.
- **CLIMOV en un teléfono real no conecta**: `10.0.2.2` solo sirve en el emulador; usa la IP LAN del host
  y permite el puerto 5080 en el firewall de Windows.
- **`dotnet restore` falla sin internet de NuGet**: verifica que `nuget.org` sea alcanzable (lo agrega `nuget.config`).
