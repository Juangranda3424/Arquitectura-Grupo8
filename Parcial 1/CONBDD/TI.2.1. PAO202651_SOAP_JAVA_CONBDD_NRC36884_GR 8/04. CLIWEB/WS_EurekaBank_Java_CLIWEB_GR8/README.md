# WS_EurekaBank_Java_CLIWEB_GR8 (TI.2.1 · cliente web)

Cliente web de EurekaBank hecho con **Jakarta EE (servlets + JSP + JSTL)** y
empaquetado como **WAR**. El navegador habla solo con este WAR, y este llama por
**SOAP** al servidor `04. SERVIDOR/WS_EurekaBank_Java_GR8`:

- `http://localhost:8080/WS_EurekaBank_Java_GR8/WSLogin` → `auth(username, password)`
- `http://localhost:8080/WS_EurekaBank_Java_GR8/WSCuenta` → `obtenerCuentaPorNumero(cuenta)` y `cuenta(cuenta, monto, tipo, cd)`
- `http://localhost:8080/WS_EurekaBank_Java_GR8/WSMovimiento` → `consultarMovimientos(numcuenta)`

Namespace de los servicios: `http://ws.monster.edu.ec/`.

## Requisitos

- JDK 21 en el `PATH` (Payara 7 corre sobre Java SE 21) y Maven 3.9+.
- El **servidor SOAP corriendo** en `http://localhost:8080`
  (perfil `run` de `04. SERVIDOR/WS_EurekaBank_Java_GR8`), con su base
  `eurekabank_ti21` levantada desde `Parcial 1/CONBDD/docker-compose.yaml`.

No hace falta instalar Payara: el perfil `run` trae **Payara Micro 7.2026.10** como
`.jar` desde Maven Central y lo levanta con el WAR.

## Configurar la URL del servidor SOAP

La URL base del servidor se define en un solo lugar: la clase `ServidorConfig`, que
lee la clave `servidor.url`. Para apuntar a otro equipo (otra IP) cambie
`src/main/resources/config.properties`:

```properties
servidor.url=http://192.168.1.50:8080/WS_EurekaBank_Java_GR8
```

Orden de prioridad (gana la primera que exista):

1. Propiedad de JVM `-Dservidor.url=...` (con el perfil `run`:
   `JAVA_TOOL_OPTIONS=-Dservidor.url=... mvn -Prun exec:exec`)
2. Archivo `config.properties` en el directorio desde donde se ejecuta Payara Micro
3. `config.properties` empaquetado en el WAR (`src/main/resources`)
4. Valor por defecto: `http://localhost:8080/WS_EurekaBank_Java_GR8`

## Compilar y desplegar

```bash
mvn clean package     # compila y empaqueta el WAR
mvn test              # pruebas del modelo de movimientos
mvn -Prun package     # además arranca el cliente web en http://localhost:8081
mvn -Prun exec:exec   # arranca sin recompilar (usa el WAR ya empaquetado)
```

El build normal (`mvn clean package`) no arranca nada. El perfil `run` es opt-in:
baja Payara Micro a `target/payara-micro-7.2026.10.jar` la primera vez y lo ejecuta
con el WAR del proyecto en el puerto **8081**, para convivir con el servidor SOAP en
8080.

### Arrancar desde NetBeans

1. Arranca primero el servidor SOAP (`run` en `04. SERVIDOR`)
2. En la barra de herramientas de este proyecto elige la configuración **`run`**
   (si no aparece, `Reload Project`)
3. Presiona **Run** (▶)
4. Espera en Output: `Payara Micro 7.2026.10 (build 18) ready in ...`
5. Abre <http://localhost:8081/WS_EurekaBank_Java_CLIWEB_GR8/>

El botón Run de NetBeans ejecuta `package`, y el arranque del cliente cuelga
justamente de esa fase — por eso funciona. El proceso queda bloqueado mientras el
cliente vive: el botón de stop del IDE (o `Ctrl+C` en consola) lo detiene, y eso
cierra el build con `Process exited with an error` (*la parada del proceso, no un
fallo de compilación*).

Payara abre el puerto unos segundos antes de terminar de desplegar: si abres el
navegador demasiado pronto vas a ver un `404`. Espera la línea `Deployed 1 archive`
/ `ready in` en Output y ahí sí consulta.

- Otro puerto: `mvn -Prun package -Dpayara.micro.port=9091` (el cliente sigue
  apuntando al servidor en 8080)
- Runtime y logs: `target/payara-micro/`
- Versión de Payara Micro: propiedad `payara.micro.version`

Para desplegar en un Payara Server o GlassFish completo ya instalado usa
`mvn clean package` y luego
`asadmin deploy target/WS_EurekaBank_Java_CLIWEB_GR8.war`.

## Cómo está organizado

```
src/main/java/ec/edu/monster/
  controller/   servlets de pantalla (Login, Home, Logout, Cuenta, Deposito,
                Retiro, Transferencia, Movimiento)
  model/        modelos de vista (CuentaModel, MovimientoModel, ResumenMovimientos,
                TipoMovimiento)
  servicio/     EurekaBankSoapClient: puente con los servicios SOAP
  ws/           stubs JAX-WS generados desde el WSDL del servidor
src/main/webapp/
  index.jsp, css/eureka.css
  WEB-INF/web.xml, WEB-INF/beans.xml
  WEB-INF/views/  login, home, cuenta, deposito, retiro, transferencia,
                  movimiento y error (+ comun/cabecera.jspf y comun/alertas.jspf)
```

El runtime **JAX-WS RI (`com.sun.xml.ws:jaxws-rt`) viaja dentro del WAR**, en
`WEB-INF/lib`, junto con `jaxb-impl`, `saaj-impl` y `woodstox`. Payara Micro no trae
runtime de JAX-WS, así que sin esas librerías el cliente falla al crear el port.
Sobre un Payara Server o GlassFish completo el servidor impone su propio runtime y
esas librerías quedan sin usar.

Los stubs de `ws/` se generaron con `jaxws-maven-plugin` a partir del WSDL del
servidor y ya traen la URL del servicio; si cambia el host o el puerto del servidor
hay que regenerarlos (el comando está documentado en el `pom.xml` del CLICON).

## Pantallas

| URL | Descripción |
|---|---|
| `/` | redirige a `/Login` |
| `/Login` | formulario de acceso |
| `/Home` | menú principal (requiere sesión) |
| `/Cuenta` | datos de la cuenta: saldo, contador de movimientos y estado |
| `/Deposito`, `/Retiro`, `/Transferencia` | operaciones |
| `/Movimiento` | historial de movimientos con totales de ingresos y egresos |
| `/Logout` | cierra la sesión |
| (cualquier otra ruta) | pantalla de error |

Credenciales del servidor: `MONSTER` / `MONSTER9` (también `admin` / `admin`).

## Prueba rápida con el servidor SOAP arriba

```bash
curl -s -o /dev/null -w '%{http_code}\n' http://localhost:8081/WS_EurekaBank_Java_CLIWEB_GR8/Login
curl -s -c /tmp/ck.txt -o /dev/null -w '%{http_code} %{redirect_url}\n' \
  -X POST http://localhost:8081/WS_EurekaBank_Java_CLIWEB_GR8/Login \
  -d 'username=MONSTER&password=MONSTER9'
curl -s -b /tmp/ck.txt -o /dev/null -w '%{http_code}\n' \
  http://localhost:8081/WS_EurekaBank_Java_CLIWEB_GR8/Home
curl -s -b /tmp/ck.txt -o /dev/null -w '%{http_code}\n' \
  -X POST http://localhost:8081/WS_EurekaBank_Java_CLIWEB_GR8/Cuenta -d 'cuenta=00100001'
```

## Notas

- La pantalla de cuenta muestra la fecha de creación como **No disponible**: el
  WSDL del servidor expone `dttCuenFechaCreacion` como un tipo `date` sin campos,
  así que el dato no viaja por SOAP.
- El sentido de cada movimiento (ingreso o egreso) se toma de la tabla
  `tipomovimiento` del servidor, no de la descripción: el servicio entrega el importe
  siempre en positivo.
- Los errores de conexión con el servidor SOAP se muestran en pantalla con el
  mensaje "No se pudo conectar con los servicios SOAP de EurekaBank…".
