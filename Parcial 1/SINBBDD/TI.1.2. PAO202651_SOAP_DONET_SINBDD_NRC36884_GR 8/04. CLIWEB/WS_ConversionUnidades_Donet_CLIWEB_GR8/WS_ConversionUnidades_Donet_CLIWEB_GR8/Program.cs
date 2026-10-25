using Microsoft.AspNetCore.Localization;
using System.Globalization;
using WS_ConversionUnidades_Donet_CLIWEB_GR8.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllersWithViews();

// El token devuelto por el servicio SOAP se guarda en la sesión del usuario
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(opciones =>
{
    opciones.IdleTimeout = TimeSpan.FromMinutes(30);
    opciones.Cookie.HttpOnly = true;
    opciones.Cookie.IsEssential = true;
});

// URLs del servicio SOAP (se editan en appsettings.json)
builder.Services.Configure<ServicioSoapOpciones>(builder.Configuration.GetSection("ServicioSoap"));
builder.Services.AddHttpClient<SoapClienteServicio>();

var app = builder.Build();

// Cultura invariante: los números siempre viajan con punto decimal (1.5) sin importar el idioma del equipo
var invariante = CultureInfo.InvariantCulture;
app.UseRequestLocalization(new RequestLocalizationOptions
{
    DefaultRequestCulture = new RequestCulture(invariante),
    SupportedCultures = new[] { invariante },
    SupportedUICultures = new[] { invariante }
});

app.UseStaticFiles();
app.UseRouting();
app.UseSession();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Conversor}/{action=Index}/{id?}");

app.Run();
