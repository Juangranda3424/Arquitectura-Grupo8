# TI.1.3 — Conversión de Unidades Monster (SOA con RESTful Java · SIN BDD)

Variante **RESTful** del sistema de conversión de unidades del grupo 8.
Replica la estructura de `TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR 8/`
(servidor + 4 clientes) cambiando **solo la capa de exposición**: el servidor
ya no publica servicios SOAP/JAX-WS sino una **API REST (JAX-RS) con JSON**,
y los cuatro clientes consumen HTTP/JSON en lugar de XML/SOAP.

La lógica de negocio (factores, fórmulas, validaciones y mensajes) es idéntica
a la de TI.1.1, de modo que ambos entregables son comparables operación por
operación.

## Estructura

```
TI.1.3. PAO202651_SOAP_RESTFUL_JAVA_SINBDD_NRC36884_GR 8/
└── 04. SERVIDOR/WS_ConversionUnidades_Java_GR8        Servidor REST (Jakarta EE 11 · JAX-RS)
└── 04. CLICON/WS_ConversionUnidades_Java_CLICON_GR8   Cliente de consola (Java · NetBeans)
└── 04. CLIESC/WS_ConversionUnidades_Java_CLIESC_GR8   Cliente de escritorio (Java Swing · NetBeans)
└── 04. CLIWEB/WS_ConversionUnidades_Java_CLIWEB_GR8   Cliente web (WAR puente + HTML/JS)
└── 04. CLIMOV/WS_ConversionUnidades_Java_CLIMOV_GR8   Cliente móvil (Android · Kotlin + Compose)
```

## Arquitectura

```
CLICON (consola)  ─┐
CLIESC (Swing)    ─┤   HTTP + JSON + Bearer          JAX-RS (/api)
CLIMOV (Android)  ─┼──────────────────────────────▶  WS_ConversionUnidades_Java_GR8
CLIWEB (navegador)─┘   (CLIWEB pasa por su servlet      ├─ /api/estado
                    puente; el token queda en sesión     ├─ /api/auth/login | logout
                    del servidor, nunca en el browser)   ├─ /api/catalogo
                                                         └─ /api/conversiones/{magnitud}
```

- **Sin base de datos**: usuarios, tokens y catálogo viven en memoria (SINBDD).
- **Patrón MVC** en todos los clientes (`vista` / `controlador` / `modelo` / `servicio` / `util`).
- El token de sesión se emite en el login y viaja en `Authorization: Bearer <token>`
  (vence a los 30 minutos; el servidor lo conserva en memoria).

## Contrato REST

URL base: `http://<host>:8080/WS_ConversionUnidades_Java_GR8/api`

Toda respuesta es un envelope JSON (en éxito y en error):

```json
{ "estado": "OK | ERROR", "codigo": 200, "mensaje": "texto", "resultado": { } }
```

| Método y ruta | Token | Cuerpo | `resultado` (200) | Errores |
| --- | --- | --- | --- | --- |
| `GET /api/estado` | no | — | `{servicio, version, protocolo, horaServidor}` | — |
| `POST /api/auth/login` | no | `{"username","password"}` | `{token, usuario, expiraEn}` | 400 campos faltantes · 401 `"Usuario invalido"` |
| `POST /api/auth/logout` | sí | — | `true` | 401 token ausente/inválido |
| `GET /api/catalogo` | sí | — | `{LONGITUD:[{clave,etiqueta,simbolo}], MASA:[...], TEMPERATURA:[...]}` | 401 |
| `POST /api/conversiones/{longitud\|masa\|temperatura}` | sí | `{"valorOrigen","unidadOrigen","unidadDestino"}` | `{valorOrigen, valorResultado, unidadOrigen, unidadDestino, tipoMagnitud}` | 400 datos/unidad inválidos · 401 · 404 magnitud desconocida |

Reglas:

- `{magnitud}` es *case-insensitive*; un `tipoMagnitud` en el cuerpo se ignora (manda la ruta).
- Unidades admitidas (nombres exactos, se aceptan en cualquier combinación de mayúsculas):
  - **LONGITUD**: `CENTIMETRO`, `METRO`, `PIE`, `YARDA`, `MILLA`
  - **MASA**: `GRAMO`, `KILOGRAMO`, `ONZA`, `LIBRA`, `QUINTAL`
  - **TEMPERATURA**: `CELSIUS`, `FAHRENHEIT`, `KELVIN`, `NEWTON`, `REAUMUR`
- El servidor devuelve precisión `double` completa (sin redondear); los clientes formatean a 4 decimales.
- Errores 401: `"Acceso denegado: falta el encabezado Authorization con el esquema Bearer."` /
  `"Acceso denegado: Token inválido o sesión expirada."`

## Servidor

Requisitos: JDK 17+ para compilar (el WAR apunta a Java 17), JDK 21 para correr el
servidor (Payara 7 corre sobre Java SE 21), Maven 3.9+.

```bash
cd "04. SERVIDOR/WS_ConversionUnidades_Java_GR8"
mvn clean package     # compila, corre las 127 pruebas y empaqueta el WAR
mvn -Prun package     # además arranca el servidor en http://localhost:8080
mvn -Prun exec:exec   # arranca sin recompilar (usa el WAR ya empaquetado)
```

El perfil `run` del `pom.xml` baja **Payara Micro 7.2026.10** (Jakarta EE 11) desde
Maven Central a `target/payara-micro-7.2026.10.jar` la primera vez, y lo levanta con
el WAR. Es opt-in: el build normal nunca arranca servidores.

### Arrancar desde NetBeans

1. En la barra de herramientas elegí la configuración **`run`** (el combo de al
   lado de los botones; si no aparece, `Reload Project`)
2. Apretá **Run** (▶)
3. Esperá en Output: `Payara Micro 7.2026.10 (build 18) ready in ...`
4. Abrí <http://localhost:8080/WS_ConversionUnidades_Java_GR8/api/estado>

El botón Run de NetBeans ejecuta `package`, y el arranque del servidor cuelga
justamente de esa fase — por eso funciona. Si alguna prueba falla, el servidor no
arranca. El proceso queda bloqueado mientras el servidor vive: el botón de stop del
IDE (o `Ctrl+C` en consola) lo detiene, y eso cierra el build con
`Process exited with an error` (*la parada del proceso, no un fallo de compilación*).

Payara abre el puerto unos segundos antes de terminar de desplegar: si abrís el
navegador demasiado pronto vas a ver un `404`. Esperá la línea `Deployed 1 archive`
/ `ready in` en Output y ahí sí consultá.

- Otro puerto: `mvn -Prun package -Dpayara.micro.port=9090`
- Runtime y logs del servidor: `target/payara-micro/`
- Versión de Payara Micro: propiedad `payara.micro.version`

Para desplegar en un Payara Server completo ya instalado sigue valiendo
`asadmin deploy target/WS_ConversionUnidades_Java_GR8.war`.

Prueba rápida:

```bash
curl http://localhost:8080/WS_ConversionUnidades_Java_GR8/api/estado
curl -X POST http://localhost:8080/WS_ConversionUnidades_Java_GR8/api/auth/login \
     -H "Content-Type: application/json" -d '{"username":"MONSTER","password":"MONSTER9"}'
```

Credenciales (en memoria, SINBDD): **`MONSTER` / `MONSTER9`**.

Pruebas del servidor: `mvn test` — 127 pruebas JUnit (núcleo de negocio + capa REST).

## Clientes

Los cuatro clientes comparten las mismas claves de configuración de red:

| Clave | Default | Significado |
| --- | --- | --- |
| `SERVIDOR_IP` | `localhost` | host de Payara |
| `SERVIDOR_PUERTO` | `8080` | puerto HTTP |
| `SERVIDOR_CONTEXTO` | `WS_ConversionUnidades_Java_GR8` | contexto del WAR del servidor |
| `API_BASE` | (vacío) | si se define, URL base del servidor (sin `/api` o con él) |
| `URL_BASE` | (vacío) | idem, tiene prioridad sobre `API_BASE` |

Resolución: `URL_BASE` > `API_BASE` > `http://<SERVIDOR_IP>:<SERVIDOR_PUERTO>/<SERVIDOR_CONTEXTO>/api`.
Prioridad de orígenes: propiedad de JVM (`-D...`) → variable de entorno → archivo de configuración → default.

### CLICON — consola
NetBeans: abrir el proyecto y *Run*, o por consola:
```bash
cd "04. CLICON/WS_ConversionUnidades_Java_CLICON_GR8"
javac --release 21 -encoding UTF-8 -cp "lib/*" -d build/classes $(find src -name "*.java")
java -cp "build/classes;lib/*" ec.edu.monster.cliente.Cliente
```
Config: `config.properties` en la raíz del proyecto. Menús de unidades cargados
desde `GET /api/catalogo`.

### CLIESC — escritorio (Swing)
Misma estructura que CLICON con ventanas `Login` y `ConversorVista`
(`ec.edu.monster.cliente.Cliente` es el `main`). Config: `config.properties`.

### CLIWEB — web
WAR puente (el navegador nunca ve el token: queda en la `HttpSession` del servlet):
```bash
cd "04. CLIWEB/WS_ConversionUnidades_Java_CLIWEB_GR8"
mvn clean package    # despliega target/WS_ConversionUnidades_Java_CLIWEB_GR8.war
```
Abrir `http://localhost:8080/WS_ConversionUnidades_Java_CLIWEB_GR8/`.
Config: `src/main/resources/.env` (plantilla sin secretos, claves de la tabla anterior).
Detalle completo en el `README.md` del propio proyecto.

### CLIMOV — Android (Kotlin + Compose)
`HttpURLConnection` + `org.json` (sin dependencias de terceros). El default es
`http://10.0.2.2:8080/...` (loopback del emulador al host); para un dispositivo
físico se cambia `URL_BASE` en el `.env` del proyecto a la IP de la máquina del
servidor. Abrir en Android Studio y *Run*. Ver el `README.md` del proyecto.

## Diferencias con TI.1.1 (SOAP)

| Área | TI.1.1 | TI.1.3 |
| --- | --- | --- |
| Servidor | JAX-WS (`WSAutenticador`, `WSConversorUnidades` + WSDL) | JAX-RS `/api` (JSON) |
| Contrato | XML/SOAP 1.1 (namespace `http://webservicio.monster.edu.ec/`) | JSON + envelope `{estado,codigo,mensaje,resultado}` |
| Autenticación | header SOAP `<Token>` | `Authorization: Bearer <token>` |
| Token | `UUID+usuario`, sin vencimiento | igual, con vencimiento de 30 min y registro thread-safe |
| Catálogo | hardcoded en cada cliente | `GET /api/catalogo` alimenta los menús de los 4 clientes |
| Errores | SOAP faults / literales | códigos HTTP + `mensaje` en el envelope |
| Respuesta de conversión | solo el `double` | DTO completo (valor, unidades y magnitud) |

## Solución de problemas

- **"No se pudo conectar con el servidor REST"**: el WAR del servidor no está
  desplegado o `SERVIDOR_IP`/`SERVIDOR_PUERTO`/`SERVIDOR_CONTEXTO` no coinciden
  con su contexto. Comprueba que abra `http://.../WS_ConversionUnidades_Java_GR8/api/estado`.
- **401 en todo**: el token venció (30 min) o el servidor se reinició (los tokens
  viven en memoria): vuelve a iniciar sesión.
- **404 en una conversión**: la magnitud de la ruta debe ser `longitud`, `masa`
  o `temperatura` (no `peso`, `volumen`, etc.).
