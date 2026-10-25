# WS_ConversionUnidades_Donet_CLIMOV_GR8

Cliente Android del servicio REST **WS_ConversionUnidades_Donet_GR8** (ASP.NET Core), escrito en Kotlin + Jetpack Compose.

```
App Android ──JSON (HttpURLConnection + org.json)──▶ servidor REST .NET (:5080)
 login / logout / catalogo / conversiones/{magnitud}   Authorization: Bearer <token>
```

Es el espejo REST de TI.1.2: misma UI/UX (Compose), mismos ViewModel y la misma identidad de la app
(`applicationId` `ec.edu.monster`, nombre `WS_ConversionUnidades_Java_CLIMOV_GR8` heredado de TI.1.2), pero sin ksoap2 ni SOAP.
El transporte usa solo APIs del SDK (`HttpURLConnection` y `org.json`), sin dependencias nuevas.

## Requisitos
- Android Studio (o el SDK de Android) con JDK 11 o superior
- El servidor REST .NET en ejecución (`04. SERVIDOR`, escucha en el puerto **5080**)
- Emulador Android o dispositivo físico

## Configuración de la URL del servidor
La app lee la base de la API de `BuildConfig`, que se alimenta desde el archivo **`.env`** en la raíz del
proyecto (copia `.env.example` a `.env`):

```
URL_BASE=http://10.0.2.2:5080/api
API_BASE=
```

| Clave | Descripción |
|---|---|
| `URL_BASE` | Base de la API REST. Por defecto, `http://10.0.2.2:5080/api` (loopback del host visto desde el emulador). |
| `API_BASE` | Override opcional. Si no está vacío, **tiene prioridad** sobre `URL_BASE`. |

Ambas se normalizan: se quita la barra final y se agrega `/api` si el valor apunta a la raíz del servidor.
El servidor también responde bajo `/WS_ConversionUnidades_Donet_GR8/api`, así que
`http://10.0.2.2:5080/WS_ConversionUnidades_Donet_GR8/api` es equivalente.

### Apuntar a un dispositivo físico / otra máquina de la LAN
`10.0.2.2` solo funciona desde el emulador. Para un teléfono real, usa la IP del host donde corre el servidor:

```
URL_BASE=http://192.168.1.50:5080/api
```

Comprueba que el teléfono y el host estén en la misma red, que el firewall de Windows permita el puerto 5080 y que
`http://192.168.1.50:5080/api/estado` responda desde el navegador del teléfono. El manifiesto ya trae
`usesCleartextTraffic="true"` para permitir HTTP plano.

Después de editar `.env`, recompila (`Build > Rebuild Project` / `./gradlew assembleDebug`) para regenerar `BuildConfig`.

## Compilar
```bash
./gradlew assembleDebug
```

## Credenciales
`MONSTER` / `MONSTER9`. Los tokens del servidor .NET **no vencen** (`expiraEn` llega vacío): solo se invalidan con
el logout o al reiniciar el servidor.

## Contrato REST consumido
Envelope: `{ "estado": "OK|ERROR", "codigo": 200, "mensaje": "texto", "resultado": {} }`.

| Método de `RestCliente` | Endpoint | Cuerpo | Token |
|---|---|---|---|
| `login(usuario, clave)` | `POST {base}/auth/login` | `{"username","password"}` | no |
| `logout(token)` | `POST {base}/auth/logout` | — | sí |
| `catalogo(token)` | `GET {base}/catalogo` | — | sí |
| `convertir(token, valor, origen, destino, tipo)` | `POST {base}/conversiones/{tipo}` | `{"valorOrigen","unidadOrigen","unidadDestino"}` | sí |

El catálogo (`GET /api/catalogo`) llena los chips de categoría y los desplegables Desde/Hacia con
etiquetas `CLAVE (Etiqueta, símbolo)`. Si la llamada falla, se usan las listas locales de `Constantes`
como último recurso y se muestra el `mensaje` del envelope.
