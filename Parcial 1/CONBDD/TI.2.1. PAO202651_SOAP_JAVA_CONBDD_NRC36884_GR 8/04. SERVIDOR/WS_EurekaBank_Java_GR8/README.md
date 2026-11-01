# WS_EurekaBank_Java_GR8 (TI.2.1 · SOAP Java)

JAX-WS server for EurekaBank on Jakarta EE 10 with MySQL.

## Requirements

- JDK 21 and Maven.
- A **full** Jakarta EE server with JAX-WS support: Payara Server or GlassFish (run from NetBeans).
  Payara Micro is not enough: it deploys the WAR but does not publish the SOAP services (404 on `?wsdl`).
- MySQL from `Parcial 1/CONBDD/docker-compose.yaml` (`docker compose up -d`), database `eurekabank_ti21`,
  user `MONSTER` / `MONSTER9`.

## Build and deploy

```bash
mvn clean package          # target/WS_EurekaBank_Java_GR8.war
```

Deploy the WAR (NetBeans: Run on Payara Server / GlassFish). Services:

| Service | WSDL |
|---|---|
| WSLogin | http://localhost:8080/WS_EurekaBank_Java_GR8/WSLogin?wsdl |
| WSCuenta | http://localhost:8080/WS_EurekaBank_Java_GR8/WSCuenta?wsdl |
| WSMovimiento | http://localhost:8080/WS_EurekaBank_Java_GR8/WSMovimiento?wsdl |

Application login: `MONSTER` / `MONSTER9` or `admin` / `admin`.
