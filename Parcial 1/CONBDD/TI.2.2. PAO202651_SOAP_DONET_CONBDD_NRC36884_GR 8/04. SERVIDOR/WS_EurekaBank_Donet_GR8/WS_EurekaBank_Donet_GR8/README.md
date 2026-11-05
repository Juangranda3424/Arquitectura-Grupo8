# Servidor SOAP EurekaBank (TI.2.2) — .NET 8 + CoreWCF + Kestrel

## Como ejecutar
- Consola: `dotnet run` dentro de esta carpeta.
- Visual Studio: abrir `WS_EurekaBank_Donet_GR8.sln` y ejecutar el perfil `WS_EurekaBank_Donet_GR8`.

Requiere MySQL en `127.0.0.1:3306` (cadena en `appsettings.json`, `ConnectionStrings:EurekaBank`).
La primera restauracion usa el `NuGet.config` de la raiz de TI.2.2 (nuget.org).
Para HTTPS se usa el certificado de desarrollo: `dotnet dev-certs https --trust`.

## URLs
- HTTP:  http://localhost:8090/WS_EurekaBank_Donet_GR8/webservicio/WSEureka.svc
- HTTPS: https://localhost:44390/WS_EurekaBank_Donet_GR8/webservicio/WSEureka.svc
- WSDL: agregar `?wsdl` a cualquiera de las dos.

El contrato (`WSEureka`, namespace `http://ws.monster.edu.ec/`) y las URLs no cambian,
por lo que los clientes siguen funcionando igual. CORS esta abierto para el cliente web.
