# WS_EurekaBank_Donet_CLIWEB_GR8 — Cliente web

Grupo 8 · Arquitectura de Software · EurekaBank — TI.2.4 (SOAP_RESTFUL_DONET · CONBDD)

Cliente web ASP.NET Core MVC (`net10.0`) que consume el API REST del servidor
`04. SERVIDOR/WS_EurekaBank_Donet_GR8`. Reemplaza al cliente ASP.NET MVC 5
(.NET Framework 4.7.2) manteniendo las mismas pantallas y el mismo flujo.

## Endpoint

`WS_EurekaBank_Donet_CLIWEB_GR8/appsettings.json` → `ServicioRest:UrlBase`

```
http://localhost:5046/api
```

| Método | Ruta | Cuerpo | Uso |
| ------ | ---- | ------ | --- |
| POST | `/api/Login` | `{ "username": "...", "password": "..." }` | Inicio de sesión |
| GET | `/api/Cuenta/{codigo}` | — | Datos de la cuenta |
| POST | `/api/Cuenta/deposito` | `{ "cuenta": "...", "monto": 100 }` | Depósito |
| POST | `/api/Cuenta/retiro` | `{ "cuenta": "...", "monto": 100 }` | Retiro |
| POST | `/api/Cuenta/transferencia` | `{ "cuenta": "...", "monto": 100, "cuentaDestino": "..." }` | Transferencia |
| POST | `/api/Movimiento` | `{ "numcuenta": "..." }` | Historial de movimientos |

Usuarios de prueba: `MONSTER / MONSTER9`.

## Rutas del cliente

| Controlador | Vista | Flujo |
| ----------- | ----- | ----- |
| `Login` | `Login/Index` | Usuario / contraseña y cierre de sesión |
| `Home` | `Home/Index` | Menú principal |
| `Cuenta` | `Cuenta/Index` | Consulta de los datos de una cuenta |
| `Deposito` | `Deposito/Index` | Registro de depósitos |
| `Retiro` | `Retiro/Index` | Registro de retiros |
| `Transferencia` | `Transferencia/Index` | Transferencia entre cuentas |
| `Movimiento` | `Movimiento/Index` | Historial con totales |

## Puerto

El cliente se ejecuta en el puerto **5047** (libre y distinto del 5046 del
servidor REST). Se configura en `Properties/launchSettings.json`.

## Requisitos

- .NET SDK 10

## Compilar

```bash
cd "04. CLIWEB/WS_EurekaBank_Donet_CLIWEB_GR8"
dotnet build
```

## Ejecutar

1. Iniciar el servidor REST (en otra terminal):

```bash
cd "04. SERVIDOR/WS_EurekaBank_Donet_GR8"
dotnet run --launch-profile http
```

2. Iniciar el cliente web (el perfil se toma de `Properties/launchSettings.json`):

```bash
cd "04. CLIWEB/WS_EurekaBank_Donet_CLIWEB_GR8/WS_EurekaBank_Donet_CLIWEB_GR8"
dotnet run --launch-profile http
```

3. Abrir `http://localhost:5047` e ingresar con `MONSTER / MONSTER9`.
