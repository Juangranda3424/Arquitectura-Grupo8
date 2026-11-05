using System;
using CoreWCF;
using Microsoft.Extensions.Configuration;
using CoreWCF.Channels;
using CoreWCF.Configuration;
using CoreWCF.Description;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using WS_EurekaBank_Donet_GR8.db;
using WS_EurekaBank_Donet_GR8.webservicio;

// Servidor SOAP del banco: CoreWCF sobre Kestrel (se inicia con "dotnet run").
// Puertos: http 8090 en todas las interfaces (accesible desde la red local) y https 44390 solo en
// localhost (ver appsettings.json y Properties/launchSettings.json).
const string RutaServicio = "/WS_EurekaBank_Donet_GR8/webservicio/WSEureka.svc";

WebApplicationBuilder builder = WebApplication.CreateBuilder(args);

ConexionBD.Configurar(builder.Configuration.GetConnectionString("EurekaBank"));

builder.Services.AddServiceModelServices();
builder.Services.AddServiceModelMetadata();
builder.Services.AddSingleton<IServiceBehavior, UseRequestHeadersForMetadataAddressBehavior>();

WebApplication app = builder.Build();

// CORS abierto para el cliente web (CLIWEB) que corre en otro origen.
// El preflight OPTIONS se responde 200 sin sobre SOAP.
app.Use(async (contexto, siguiente) =>
{
    IHeaderDictionary cabeceras = contexto.Response.Headers;
    cabeceras["Access-Control-Allow-Origin"] = "*";
    cabeceras["Access-Control-Allow-Methods"] = "POST, GET, OPTIONS";
    cabeceras["Access-Control-Allow-Headers"] = "Content-Type, SOAPAction, Accept, Origin, X-Requested-With";
    cabeceras["Access-Control-Max-Age"] = "86400";

    if (HttpMethods.IsOptions(contexto.Request.Method))
    {
        contexto.Response.StatusCode = StatusCodes.Status200OK;
        return;
    }

    await siguiente();
});

BasicHttpBinding bindingHttp = new BasicHttpBinding
{
    MaxReceivedMessageSize = 1048576,
    MaxBufferSize = 1048576
};

// Mismo binding sobre HTTPS (Kestrel, puerto 44390)
BasicHttpBinding bindingHttps = new BasicHttpBinding(BasicHttpSecurityMode.Transport)
{
    MaxReceivedMessageSize = 1048576,
    MaxBufferSize = 1048576
};

app.UseServiceModel(serviceBuilder =>
{
    serviceBuilder.AddService<WSEureka>();
    serviceBuilder.AddServiceEndpoint<WSEureka, IWSEureka>(bindingHttp, RutaServicio);
    serviceBuilder.AddServiceEndpoint<WSEureka, IWSEureka>(bindingHttps, RutaServicio);

    // Metadatos (WSDL) para que los clientes puedan regenerar su proxy
    ServiceMetadataBehavior metadatos = app.Services.GetRequiredService<ServiceMetadataBehavior>();
    metadatos.HttpGetEnabled = true;
    metadatos.HttpsGetEnabled = true;

    // Detalle de excepciones para depuracion del curso
    serviceBuilder.ConfigureServiceHostBase<WSEureka>(host =>
    {
        ServiceDebugBehavior depuracion = host.Description.Behaviors.Find<ServiceDebugBehavior>();
        if (depuracion != null)
        {
            depuracion.IncludeExceptionDetailInFaults = true;
        }
    });
});

app.Run();
