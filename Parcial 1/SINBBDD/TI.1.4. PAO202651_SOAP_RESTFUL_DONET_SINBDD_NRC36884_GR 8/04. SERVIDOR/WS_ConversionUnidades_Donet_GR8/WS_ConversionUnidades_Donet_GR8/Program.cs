using WS_ConversionUnidades_Donet_GR8.api.soporte;
using WS_ConversionUnidades_Donet_GR8.modelo;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

var app = builder.Build();

// Toda excepción y todo código de error sin cuerpo (404/405 del enrutador) se devuelven con el envelope JSON.
app.UseMiddleware<MapeadorExcepciones>();
app.UseStatusCodePages(MapeadorExcepciones.escribirCodigoEstado);

// La API responde en /api/... y también bajo /WS_ConversionUnidades_Donet_GR8/api/..., equivalente al
// contexto del WAR de TI.1.3, para que los clientes conserven su esquema de URL base.
app.UsePathBase("/" + Constantes.NOMBRE_SERVICIO);
app.UseRouting();

app.MapControllers();

app.Run();

/// <summary>
/// Punto de entrada expuesto para WebApplicationFactory en las pruebas de integración.
/// </summary>
public partial class Program
{
}
