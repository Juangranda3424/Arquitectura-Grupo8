# WS_ConversionUnidades_Java_CLIWEB_GR8

Cliente web del servicio SOAP **WS_ConversionUnidades_Java_GR8** (Payara 7 · Jakarta EE 11).

```
Navegador ──JSON──▶ ApiServlet (este WAR) ──SOAP 1.1──▶ WS_ConUni_Java_GR8 (servidor)
 index.html/app.js   guarda el token en la sesión          WSAutenticador / WSConversorUnidades
```

El navegador no llama al SOAP directo (CORS), así que un servlet hace de puente.
El token **nunca llega al navegador**: queda en la `HttpSession` del servidor.

## Requisitos
- JDK 17 o superior (Payara 7 corre con JDK 21)
- Maven 3.9+
- Payara 7 con el **servidor ya desplegado** (`WS_ConUni_Java_GR8-1.0-SNAPSHOT.war`)

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
WS_HOST=192.168.1.50
WS_PORT=8080
WS_CONTEXT=WS_ConUni_Java_GR8-1.0-SNAPSHOT
```

Prioridad (gana el primero): propiedad de JVM (`-DWS_HOST=...`) → variable de entorno → archivo `.env` → valor por defecto.

El `.env` se busca en este orden:
1. Ruta indicada por `-DENV_FILE=/ruta/mi.env` o la variable `ENV_FILE` (se edita **sin recompilar**).
2. `.env` en el directorio de trabajo de Payara.
3. El `.env` empaquetado en el WAR.

Al arrancar, el log de Payara indica cuál se usó ("Configuración leída de: ...").

`WS_TOKEN_NAMESPACE` **no es una IP real**: es un identificador XML y debe coincidir con `Constantes.NAMESPACE`
del servidor. No lo cambies al cambiar `WS_HOST`.

Endpoints consumidos: `http://WS_HOST:WS_PORT/WS_CONTEXT/WSAutenticador` y `.../WSConversorUnidades`.
El namespace de las operaciones se lee solo del `?wsdl`.

## Contrato SOAP usado
| Operación            | Servicio            | Cuerpo                                                                 | Header `Token` |
|----------------------|---------------------|------------------------------------------------------------------------|----------------|
| `login`              | WSAutenticador      | `<login><username/><password/></login>` → token (o `Usuario invalido`) | no             |
| `logout`             | WSAutenticador      | `<logout><token/></logout>` → boolean                                  | sí             |
| `convertir-unidades` | WSConversorUnidades | `<peticionConversion>` con valorOrigen, unidadOrigen, unidadDestino, tipoMagnitud → double | sí |

## Solución de problemas
- **"No se pudo conectar con el servidor SOAP"**: el servidor no está desplegado o `WS_HOST`/`WS_PORT`/`WS_CONTEXT` no coinciden
  con su contexto. Comprueba que abra `…/WSAutenticador?wsdl` en el navegador.
- **"Tu sesión terminó"**: el servidor guarda los tokens en memoria; si se redespliega o reinicia, hay que volver a iniciar sesión.
- **"El servidor respondió algo que no es SOAP"**: URL equivocada (revisa el contexto del WAR del servidor).
