# WS_EurekaBank_Donet_GR8 — Servidor REST .NET

Grupo 8 · Arquitectura de Software · EurekaBank

Servidor REST (ASP.NET Core + SQL Server) del proyecto **TI.2.4 · SOAP_RESTFUL_DONET (CONBDD)**.

## Requisitos

- .NET SDK 8 o superior
- SQL Server 2022 con la base `eurekabank_ti24` creada con los scripts de `03. BDD/`
  (en este curso el SQL Server corre en Docker, puerto `1433`, usuario `sa`)

## Configuración

`appsettings.json` → `ConnectionStrings:EurekaBankDB`

```
Server=localhost,1433;Database=eurekabank_ti24;User Id=sa;Password=EurekaGr8.2026;TrustServerCertificate=True
```

Scripts de base de datos (ejecutar con `sqlcmd`, en este orden):

```bash
sqlcmd -S localhost,1433 -U sa -P 'EurekaGr8.2026' -C -i "03. BDD/Crear_Base.sql"
sqlcmd -S localhost,1433 -U sa -P 'EurekaGr8.2026' -C -i "03. BDD/Insertar_Datos.sql"
```

## Ejecutar

```bash
dotnet run --project WS_EurekaBank_Donet_GR8.csproj --launch-profile http
```

- API: `http://localhost:5046/api`
- Swagger: `http://localhost:5046/swagger`

## Endpoints

| Método | Ruta | Cuerpo |
| ------ | ---- | ------ |
| POST | `/api/Login` | `{ "username": "...", "password": "..." }` |
| POST | `/api/Cuenta/deposito` | `{ "cuenta": "00200001", "monto": 100 }` |
| POST | `/api/Cuenta/retiro` | `{ "cuenta": "00200001", "monto": 100 }` |
| POST | `/api/Cuenta/transferencia` | `{ "cuenta": "...", "monto": 100, "cuentaDestino": "..." }` |
| GET | `/api/Cuenta/{codigoCuenta}` | — |
| POST | `/api/Movimiento` | `{ "numcuenta": "00200001" }` |

Usuarios de prueba: `MONSTER / MONSTER9` y `admin / admin`.

## Clientes de este proyecto

| Cliente | Carpeta | URL configurada |
| ------- | ------- | --------------- |
| Consola | `04. CLICON/WS_EurekaBank_Donet_CLICON_GR8` | `http://localhost:5046/api/` |
| Escritorio | `04. CLIESC/WS_EurekaBank_Donet_CLIESC_GR8` | `http://localhost:5046/api/` |
| Móvil (Android) | `04. CLIMOV/WS_EurekaBank_Donet_CLIMOV_GR8` | `http://10.0.2.2:5046/api/` (emulador) |
| Web | `04. CLIWEB/WS_EurekaBank_Donet_CLIWEB_GR8` | `http://localhost:5046/api/` |
