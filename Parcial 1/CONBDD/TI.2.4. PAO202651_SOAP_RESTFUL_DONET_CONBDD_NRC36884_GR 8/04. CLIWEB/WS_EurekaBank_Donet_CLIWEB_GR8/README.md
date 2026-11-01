# WS_EurekaBank_Donet_CLIWEB_GR8 — Cliente web

Grupo 8 · Arquitectura de Software · EurekaBank — TI.2.4 (SOAP_RESTFUL_DONET · CONBDD)

Cliente web ASP.NET MVC 5 (.NET Framework 4.7.2) que consume el API REST de
`WS_EurekaBank_Donet_GR8`.

## Endpoint

`Web.config` → `appSettings:ApiBaseUrl` = `http://localhost:5046/api/`

## Rutas

| Controlador | Vista | Flujo |
| ----------- | ----- | ----- |
| `Login` | `Login/Index` | Usuario / contraseña (`MONSTER / MONSTER9`) y sesión |
| `Home` | `Home/Index` | Menú principal |
| `Deposito`, `Retiro`, `Transferencia` | `*/Index` | Registro de operaciones |
| `Movimiento` | `Movimiento/Index` | Historial con totales |
| `Cuenta` | `Cuenta/Index` | Datos de la cuenta |

## Build

```bash
msbuild WS_EurekaBank_Donet_CLIWEB_GR8.sln -restore -p:RestorePackagesConfig=true
```

## Ejecutar

Abrir la solución en Visual Studio (IIS Express) o publicar en IIS.
