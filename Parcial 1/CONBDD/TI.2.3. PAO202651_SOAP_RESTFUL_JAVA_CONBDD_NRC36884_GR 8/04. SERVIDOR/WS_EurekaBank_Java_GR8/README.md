# WS_EurekaBank_Java_GR8 (TI.2.3 · RESTful Java)

Servidor JAX-RS de EurekaBank sobre Jakarta EE 10 con MySQL.

## Requisitos

- JDK 21 en el `PATH` (Payara 7 corre sobre Java SE 21) y Maven 3.9+.
- MySQL de `Parcial 1/CONBDD/docker-compose.yaml` (`docker compose up -d`), base
  `eurekabank_ti23`, usuario `MONSTER` / `MONSTER9`.

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
4. Abre <http://localhost:8080/WS_EurekaBank_Java_GR8/resources/cuenta/00100001>

El botón Run de NetBeans ejecuta `package`, y el arranque del servidor cuelga
justamente de esa fase — por eso funciona. El proceso queda bloqueado mientras el
servidor vive: el botón de stop del IDE (o `Ctrl+C` en consola) lo detiene, y eso
cierra el build con `Process exited with an error` (*la parada del proceso, no un
fallo de compilación*).

Payara abre el puerto unos segundos antes de terminar de desplegar: si abres el
navegador demasiado pronto vas a ver un `404`. Espera la línea `Deployed 1 archive`
/ `ready in` en Output y ahí sí consulta.

- Otro puerto: `mvn -Prun package -Dpayara.micro.port=9090`
- Runtime y logs del servidor: `target/payara-micro/`
- Versión de Payara Micro: propiedad `payara.micro.version`

Para desplegar en un Payara Server o GlassFish completo ya instalado usa
`mvn clean package` y luego `asadmin deploy target/WS_EurekaBank_Java_GR8.war`.

## Recursos

El `ApplicationPath` es `/resources`, así que el contexto completo es
`http://localhost:8080/WS_EurekaBank_Java_GR8/resources/`.

| Método | Ruta | Cuerpo / parámetro |
|---|---|---|
| POST | `/login` | `{"username":"MONSTER","password":"MONSTER9"}` → `true` |
| GET | `/cuenta/{codigoCuenta}` | `00100001` |
| POST | `/cuenta/deposito` | cuenta, monto |
| POST | `/cuenta/retiro` | cuenta, monto |
| POST | `/cuenta/transferencia` | cuenta origen, cuenta destino, monto |
| POST | `/movimiento` | `{"numcuenta":"00100001"}` |

## Prueba rápida

```bash
# login correcto -> true ; contraseña incorrecta -> false
curl -s -X POST http://localhost:8080/WS_EurekaBank_Java_GR8/resources/login \
     -H "Content-Type: application/json" -d '{"username":"MONSTER","password":"MONSTER9"}'

# cuenta 00100001 -> JSON con saldo y estado
curl http://localhost:8080/WS_EurekaBank_Java_GR8/resources/cuenta/00100001

# movimientos de la cuenta 00100001
curl -s -X POST http://localhost:8080/WS_EurekaBank_Java_GR8/resources/movimiento \
     -H "Content-Type: application/json" -d '{"numcuenta":"00100001"}'
```

Credenciales de la aplicación: `MONSTER` / `MONSTER9`.

El cliente web (`04. CLIWEB/WS_EurekaBank_Java_CLIWEB_GR8`) consume estos recursos
desde `http://localhost:8080/WS_EurekaBank_Java_GR8/resources/` y se arranca con su
propio perfil `run` en el puerto 8081.
