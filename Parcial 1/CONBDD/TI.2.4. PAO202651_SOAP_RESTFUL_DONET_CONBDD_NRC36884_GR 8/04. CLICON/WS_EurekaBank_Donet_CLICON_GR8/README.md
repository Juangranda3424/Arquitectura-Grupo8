# WS_EurekaBank_Donet_CLICON_GR8 — Cliente de consola

Grupo 8 · Arquitectura de Software · EurekaBank — TI.2.4 (SOAP_RESTFUL_DONET · CONBDD)

Cliente de consola (.NET Framework 4.7.2) que consume el API REST de
`WS_EurekaBank_Donet_GR8`.

## Endpoint

`App.config` → `ApiBaseUrl` = `http://localhost:5046/api/`

## Flujo

1. Iniciar sesión (`MONSTER / MONSTER9` o `admin / admin`).
2. Menú principal: depósito, retiro, transferencia, movimientos, datos de cuenta y cerrar sesión.

## Build

```bash
msbuild WS_EurekaBank_Donet_CLICON_GR8.sln -restore -p:RestorePackagesConfig=true
```

## Ejecutar

```bash
bin\Debug\WS_EurekaBank_Donet_CLICON_GR8.exe
```
