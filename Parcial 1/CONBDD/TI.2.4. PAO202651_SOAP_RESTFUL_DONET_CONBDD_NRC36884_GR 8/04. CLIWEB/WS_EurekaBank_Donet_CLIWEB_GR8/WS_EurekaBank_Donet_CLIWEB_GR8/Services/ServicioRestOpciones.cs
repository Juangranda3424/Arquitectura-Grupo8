namespace WS_EurekaBank_Donet_CLIWEB_GR8.Services
{
    public class ServicioRestOpciones
    {
        // URL base de la API REST del servidor WS_EurekaBank_Donet_GR8: los recursos cuelgan de aquí (/Login, /Cuenta, ...)
        public string UrlBase { get; set; } = "http://localhost:5046/api";

        // Tiempo máximo de espera por petición, en segundos
        public int TiempoEsperaSegundos { get; set; } = 15;
    }
}
