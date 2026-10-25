using System.Text.Json;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using WS_ConversionUnidades_Donet_GR8.api.soporte;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.seguridad;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.api.recursos
{
    /// <summary>
    /// POST /api/auth/login y POST /api/auth/logout: ciclo de sesión en memoria.
    /// </summary>
    [Route("api/auth")]
    public class AutenticadorRecurso : ControllerBase
    {
        private const string MENSAJE_CAMPOS_OBLIGATORIOS = "El nombre de usuario y la contraseña son obligatorios.";
        private const string MENSAJE_LOGIN_OK = "Autenticación exitosa.";
        private const string MENSAJE_LOGOUT_OK = "Sesión finalizada.";

        private readonly AutenticadorServicio autenticador = new AutenticadorServicio();

        [HttpPost("login")]
        public async Task<IActionResult> iniciarSesion()
        {
            SoportePeticion.exigirTipoJSON(Request.ContentType);

            JsonElement datos = SoportePeticion.leerObjetoJSON(await SoportePeticion.leerCuerpo(Request));
            string? usuario = SoportePeticion.texto(datos, "username");
            string? contrasena = SoportePeticion.texto(datos, "password");

            if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(contrasena))
            {
                throw new ErrorAPI(400, MENSAJE_CAMPOS_OBLIGATORIOS);
            }

            string token = autenticador.autenticadorLogin(new Usuario(usuario, contrasena));
            if (AutenticadorServicio.USUARIO_INVALIDO.Equals(token))
            {
                throw new ErrorAPI(401, AutenticadorServicio.USUARIO_INVALIDO);
            }

            // Los tokens de TI.1.2 no vencen: expiraEn se publica vacío, como hace TI.1.3 sin vencimiento.
            var resultado = new JsonObject
            {
                ["token"] = token,
                ["usuario"] = Constantes.usuario,
                ["expiraEn"] = "",
            };

            return RespuestaAPI.respuesta(200, RespuestaAPI.ok(200, MENSAJE_LOGIN_OK, resultado));
        }

        [HttpPost("logout")]
        [RequiereToken]
        public IActionResult cerrarSesion()
        {
            autenticador.autenticadorLogout(ValidadorToken.extraerToken(Request.Headers.Authorization.ToString()));

            return RespuestaAPI.respuesta(200, RespuestaAPI.ok(200, MENSAJE_LOGOUT_OK, JsonValue.Create(true)));
        }
    }
}
