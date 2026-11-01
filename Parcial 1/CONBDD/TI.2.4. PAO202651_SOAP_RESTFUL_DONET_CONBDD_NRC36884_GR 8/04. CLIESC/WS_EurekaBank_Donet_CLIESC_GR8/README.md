# WS_EurekaBank_Donet_CLIESC_GR8 — Cliente de escritorio

Grupo 8 · Arquitectura de Software · EurekaBank — TI.2.4 (SOAP_RESTFUL_DONET · CONBDD)

Cliente Windows Forms (.NET Framework 4.7.2) que consume el API REST de
`WS_EurekaBank_Donet_GR8`.

## Endpoint

`App.config` → `ApiBaseUrl` = `http://localhost:5046/api/`

## Pantallas

| Pantalla | Flujo |
| -------- | ----- |
| `FrmLogin` | Usuario / contraseña (`MONSTER / MONSTER9` o `admin / admin`) |
| `FrmMenu` | Depósito, retiro, transferencia, movimientos, datos de cuenta y cerrar sesión |
| `FrmDeposito` / `FrmRetiro` / `FrmTransferencia` | Registro de la operación y saldo resultante |
| `FrmMovimientos` | Historial con saldo acumulado y totales |
| `FrmDatos` | Datos de la cuenta consultada |

## Build

```bash
msbuild WS_EurekaBank_Donet_CLIESC_GR8.sln -restore -p:RestorePackagesConfig=true
```

## Ejecutar

```bash
bin\Debug\WS_EurekaBank_Donet_CLIESC_GR8.exe
```
