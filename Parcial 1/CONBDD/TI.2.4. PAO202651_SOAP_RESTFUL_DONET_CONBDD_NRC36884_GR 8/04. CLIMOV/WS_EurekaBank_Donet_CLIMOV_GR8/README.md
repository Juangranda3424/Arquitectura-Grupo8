# WS_EurekaBank_Donet_CLIMOV_GR8 — Cliente móvil (Android)

Grupo 8 · Arquitectura de Software · EurekaBank — TI.2.4 (SOAP_RESTFUL_DONET · CONBDD)

Cliente Android (Java, AGP 8.13, minSdk 24) que consume el API REST de
`WS_EurekaBank_Donet_GR8` con OkHttp + Gson.

## Endpoint

`app/src/main/java/ec/edu/monster/climov/util/ApiConfig.java`:

- Emulador: `http://10.0.2.2:5046/api/`
- Dispositivo físico: cambiar a la IP de la PC, por ejemplo `http://192.168.1.25:5046/api/`

## Pantallas

| Activity | Función |
| -------- | ------- |
| `LoginActivity` | Usuario / contraseña (`MONSTER / MONSTER9` o `admin / admin`) |
| `MainActivity` | Menú: depósito, retiro, transferencia, movimientos, datos de cuenta, salir |
| `DepositoActivity` / `RetiroActivity` / `TransferenciaActivity` | Registro de operaciones |
| `MovimientosActivity` | Historial con saldo acumulado y totales |
| `DatosCuentaActivity` | Datos de la cuenta |

## Build

```bash
./gradlew assembleDebug
```

Requiere Android SDK (`local.properties` con `sdk.dir`) y JDK 21.
