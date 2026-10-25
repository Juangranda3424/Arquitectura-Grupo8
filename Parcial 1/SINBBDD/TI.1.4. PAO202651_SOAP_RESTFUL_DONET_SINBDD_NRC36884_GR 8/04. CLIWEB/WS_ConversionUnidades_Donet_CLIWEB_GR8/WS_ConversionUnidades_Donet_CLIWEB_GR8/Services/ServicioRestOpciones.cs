namespace WS_ConversionUnidades_Donet_CLIWEB_GR8.Services
{
    public class ServicioRestOpciones
    {
        // URL base de la API REST: los recursos cuelgan de aquí (/auth/login, /catalogo, ...)
        public string UrlBase { get; set; } = "http://localhost:5080/api";

        // Tiempo máximo de espera por petición, en segundos
        public int TiempoEsperaSegundos { get; set; } = 15;
    }
}
