using Microsoft.AspNetCore.Localization;
using Microsoft.Extensions.Options;
using System.Globalization;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllersWithViews();

// El usuario autenticado se guarda en la sesión del cliente (antes Session["Username"] en MVC 5)
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(opciones =>
{
    opciones.IdleTimeout = TimeSpan.FromMinutes(30);
    opciones.Cookie.HttpOnly = true;
    opciones.Cookie.IsEssential = true;
});

// URL base y tiempo de espera del servicio REST (se editan en appsettings.json)
builder.Services.Configure<ServicioRestOpciones>(builder.Configuration.GetSection("ServicioRest"));
builder.Services.AddHttpClient<RestClienteServicio>((proveedor, http) =>
{
    var opciones = proveedor.GetRequiredService<IOptions<ServicioRestOpciones>>().Value;
    http.Timeout = TimeSpan.FromSeconds(opciones.TiempoEsperaSegundos > 0 ? opciones.TiempoEsperaSegundos : 15);
});

var app = builder.Build();

// Cultura invariante: los montos viajan con punto decimal (100.50) sin importar el idioma del equipo
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
    pattern: "{controller=Login}/{action=Index}/{id?}");

app.Run();
