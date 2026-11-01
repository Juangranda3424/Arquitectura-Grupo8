# WS_EurekaBank_Java_GR8 (TI.2.1 · SOAP Java)

Servidor JAX-WS de EurekaBank sobre Jakarta EE 10 con MySQL.

## Requisitos

- JDK 21 en el `PATH` (Payara 7 corre sobre Java SE 21) y Maven 3.9+.
- MySQL de `Parcial 1/CONBDD/docker-compose.yaml` (`docker compose up -d`), base
  `eurekabank_ti21`, usuario `MONSTER` / `MONSTER9`.

No hace falta instalar Payara: el perfil `run` trae **Payara Micro 7.2026.10** como
`.jar` desde Maven Central y lo levanta con el WAR.

## Compilar y desplegar

```bash
mvn clean package     # compila y empaqueta el WAR
mvn -Prun package     # además arranca el servidor en http://localhost:8080
mvn -Prun exec:exec   # arranca sin recompilar (usa el WAR ya empaquetado)
```

El build normal (`mvn clean package`) no arranca nada y genera el WAR para un
servidor completo. El perfil `run` es opt-in: baja Payara Micro a
`target/payara-micro-7.2026.10.jar` la primera vez y lo ejecuta con el WAR del
proyecto.

### Arrancar desde NetBeans

1. En la barra de herramientas elige la configuración **`run`** (el combo de al
   lado de los botones; si no aparece, `Reload Project`)
2. Presiona **Run** (▶)
3. Espera en Output: `Payara Micro 7.2026.10 (build 18) ready in ...`
4. Abre <http://localhost:8080/WS_EurekaBank_Java_GR8/WSLogin?wsdl>

El botón Run de NetBeans ejecuta `package`, y el arranque del servidor cuelga
justamente de esa fase — por eso funciona. El proceso queda bloqueado mientras el
servidor vive: el botón de stop del IDE (o `Ctrl+C` en consola) lo detiene, y eso
cierra el build con `Process exited with an error` (*la parada del proceso, no un
fallo de compilación*).

Payara publica el puerto unos segundos antes de terminar de desplegar: si abres el
navegador demasiado pronto vas a ver un `404`. Espera la línea `Deployed 1 archive`
y unos segundos más (el runtime SOAP se publica después) y ahí sí consulta.

- Otro puerto: `mvn -Prun package -Dpayara.micro.port=9090`
- Runtime y logs del servidor: `target/payara-micro/`
- Versión de Payara Micro: propiedad `payara.micro.version`

Para desplegar en un Payara Server o GlassFish completo ya instalado usa
`mvn clean package` y luego `asadmin deploy target/WS_EurekaBank_Java_GR8.war`.
Conviene `clean`: sin él Maven puede reutilizar el contenido del último build `run`,
incluido `WEB-INF/sun-jaxws.xml`.

## Servicios

Los servicios conservan la misma URL en Payara Micro y en un servidor completo:

| Servicio | WSDL |
|---|---|
| WSLogin | http://localhost:8080/WS_EurekaBank_Java_GR8/WSLogin?wsdl |
| WSCuenta | http://localhost:8080/WS_EurekaBank_Java_GR8/WSCuenta?wsdl |
| WSMovimiento | http://localhost:8080/WS_EurekaBank_Java_GR8/WSMovimiento?wsdl |

### JAX-WS en Payara Micro

Payara Micro no incluye runtime JAX-WS: despliega el WAR pero los servicios
responden `404`. Por eso el perfil `run` agrega al WAR, y solo con el perfil activo:

- `com.sun.xml.ws:jaxws-rt` (Metro) como dependencia, así que el runtime viaja en
  `WEB-INF/lib`;
- `src/run/webapp/WEB-INF/sun-jaxws.xml`, el descriptor que le indica al RI que
  publique cada `@WebService` en `/WSLogin`, `/WSCuenta` y `/WSMovimiento`.

El `ServletContainerInitializer` del RI detecta ese descriptor y registra los
endpoints. Sin el perfil no se empaqueta nada de esto, así que un WAR desplegado en
un Payara Server o GlassFish completo sigue usando el runtime JAX-WS del servidor.

## Prueba rápida

```bash
# login correcto -> true ; contraseña incorrecta -> false
curl -s -X POST http://localhost:8080/WS_EurekaBank_Java_GR8/WSLogin \
  -H 'Content-Type: text/xml;charset=UTF-8' -H 'SOAPAction: ""' \
  --data-binary '<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:w="http://ws.monster.edu.ec/"><S:Body><w:auth><username>MONSTER</username><password>MONSTER9</password></w:auth></S:Body></S:Envelope>'

# cuenta 00100001
curl -s -X POST http://localhost:8080/WS_EurekaBank_Java_GR8/WSCuenta \
  -H 'Content-Type: text/xml;charset=UTF-8' -H 'SOAPAction: ""' \
  --data-binary '<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:w="http://ws.monster.edu.ec/"><S:Body><w:obtenerCuentaPorNumero><cuenta>00100001</cuenta></w:obtenerCuentaPorNumero></S:Body></S:Envelope>'

# movimientos de la cuenta 00100001
curl -s -X POST http://localhost:8080/WS_EurekaBank_Java_GR8/WSMovimiento \
  -H 'Content-Type: text/xml;charset=UTF-8' -H 'SOAPAction: ""' \
  --data-binary '<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:w="http://ws.monster.edu.ec/"><S:Body><w:consultarMovimientos><numcuenta>00100001</numcuenta></w:consultarMovimientos></S:Body></S:Envelope>'
```

Credenciales de la aplicación: `MONSTER` / `MONSTER9` o `admin` / `admin`.
