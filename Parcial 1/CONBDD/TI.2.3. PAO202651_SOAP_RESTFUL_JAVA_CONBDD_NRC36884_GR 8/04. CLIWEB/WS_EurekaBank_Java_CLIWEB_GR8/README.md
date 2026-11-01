# WS_EurekaBank_Java_CLIWEB_GR8 (TI.2.3 · cliente web)

Cliente web (JSP + servlets) de EurekaBank. Es un puente sobre el servidor RESTful:
el navegador habla solo con este WAR, y este llama a
`http://localhost:8080/WS_EurekaBank_Java_GR8/resources/` con `RestApiClient`.

## Requisitos

- JDK 21 en el `PATH` (Payara 7 corre sobre Java SE 21) y Maven 3.9+.
- El **servidor REST corriendo** en `http://localhost:8080`
  (perfil `run` de `04. SERVIDOR/WS_EurekaBank_Java_GR8`), con su base
  `eurekabank_ti23` levantada desde `Parcial 1/CONBDD/docker-compose.yaml`.

No hace falta instalar Payara: el perfil `run` trae **Payara Micro 7.2026.10** como
`.jar` desde Maven Central y lo levanta con el WAR.

## Compilar y desplegar

```bash
mvn clean package     # compila y empaqueta el WAR
mvn -Prun package     # además arranca el cliente web en http://localhost:8081
mvn -Prun exec:exec   # arranca sin recompilar (usa el WAR ya empaquetado)
```

El build normal (`mvn clean package`) no arranca nada. El perfil `run` es opt-in:
baja Payara Micro a `target/payara-micro-7.2026.10.jar` la primera vez y lo ejecuta
con el WAR del proyecto en el puerto **8081**, para convivir con el servidor REST en
8080.

### Arrancar desde NetBeans

1. Arranca primero el servidor REST (`run` en `04. SERVIDOR`)
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
`mvn clean package` y luego `asadmin deploy target/WS_EurekaBank_Java_CLIWEB_GR8.war`.

## Pantallas

| URL | Descripción |
|---|---|
| `/` | redirige a `/Login` |
| `/Login` | formulario de acceso |
| `/Home` | menú principal (requiere sesión) |
| `/Cuenta`, `/Movimiento`, `/Deposito`, `/Retiro`, `/Transferencia` | operaciones |
| `/Logout` | cierra la sesión |

Credenciales del servidor: `MONSTER` / `MONSTER9`.

Prueba rápida con el servidor arriba:

```bash
curl -s -o /dev/null -w '%{http_code}\n' http://localhost:8081/WS_EurekaBank_Java_CLIWEB_GR8/Login
curl -s -c /tmp/ck.txt -o /dev/null -w '%{http_code} %{redirect_url}\n' \
  -X POST http://localhost:8081/WS_EurekaBank_Java_CLIWEB_GR8/Login \
  -d 'username=MONSTER&password=MONSTER9'
curl -s -b /tmp/ck.txt -o /dev/null -w '%{http_code}\n' \
  http://localhost:8081/WS_EurekaBank_Java_CLIWEB_GR8/Home
```
