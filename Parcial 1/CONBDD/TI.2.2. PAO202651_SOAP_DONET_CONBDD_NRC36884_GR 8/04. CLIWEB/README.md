# CLIWEB · EurekaBank (TI.2.2 · SOAP .NET)

Cliente web de EurekaBank del proyecto **TI.2.2 (SOAP sobre .NET)**. Es una aplicación
ASP.NET Core MVC (net10.0) que consume el servicio WCF `WS_EurekaBank_Donet_GR8`
por SOAP 1.1, igual que el resto de clientes de la actividad.

## Servicio consumido

| Dato | Valor |
|---|---|
| Endpoint | `http://localhost:8090/WS_EurekaBank_Donet_GR8/webservicio/WSEureka.svc` |
| Namespace del contrato | `http://ws.monster.edu.ec/` |
| SOAPAction | `http://ws.monster.edu.ec/WSEureka/{login,regDeposito,traerMovimientos}` |
| Operaciones | `login`, `regDeposito`, `traerMovimientos` |

La URL y los namespaces se configuran en `WS_EurekaBank_Donet_CLIWEB_GR8/appsettings.json`
(sección `ServicioSoap`). Los parámetros viajan calificados en el namespace del contrato,
igual que en los demás clientes de esta actividad.

## Requisitos

- .NET SDK 10
- Servidor `WS_EurekaBank_Donet_GR8` en ejecución y su base de datos MySQL
  `eurekabank_ti22` (`Parcial 1/CONBDD/docker-compose.yaml` + `03. BDD/Crear_Base.sql`)

## Ejecución

```bash
cd "04. CLIWEB/WS_EurekaBank_Donet_CLIWEB_GR8"
dotnet run --project WS_EurekaBank_Donet_CLIWEB_GR8
```

La aplicación queda disponible en **http://localhost:5501** (puerto definido en
`Properties/launchSettings.json`). Si el puerto estuviera ocupado, se puede cambiar ahí
o iniciar con `--urls http://localhost:<puerto>`.

Para compilar sin ejecutar:

```bash
dotnet build WS_EurekaBank_Donet_CLIWEB_GR8.sln
```

## Pantallas

1. **Inicio de sesión**: valida el usuario y la contraseña con la operación `login`.
2. **Menú principal**: accesos a registrar movimiento y consultar movimientos.
3. **Registrar movimiento**: depósito (`003`), retiro (`004`) y transferencia (`008`,
   que exige cuenta destino) mediante `regDeposito`.
4. **Consultar movimientos**: historial de una cuenta mediante `traerMovimientos`,
   con filtro por tipo de movimiento.

Las pantallas 2 a 4 requieren una sesión iniciada; si no existe, la aplicación
redirige al inicio de sesión. La sesión se guarda en el servidor (30 minutos de
inactividad) y se cierra con el botón **Cerrar sesión**.

Credenciales de prueba: `MONSTER` / `MONSTER9`.

## Estructura

```text
WS_EurekaBank_Donet_CLIWEB_GR8/
├── WS_EurekaBank_Donet_CLIWEB_GR8.sln
└── WS_EurekaBank_Donet_CLIWEB_GR8/
    ├── Program.cs               configuración, sesión y ruta por defecto
    ├── appsettings.json         opciones del servicio SOAP
    ├── Controllers/             Autenticacion, Menu, Movimiento y Movimientos
    ├── Models/                  view models y catálogos
    ├── Services/                SoapClienteServicio, opciones y excepción
    ├── Views/                   _Layout, login, menú, movimiento y movimientos
    └── wwwroot/                 css/site.css e img/logo-eureka.svg
```
