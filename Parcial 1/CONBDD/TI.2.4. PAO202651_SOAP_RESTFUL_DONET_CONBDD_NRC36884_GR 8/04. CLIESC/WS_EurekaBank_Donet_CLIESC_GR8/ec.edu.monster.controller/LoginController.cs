using System;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller
{
    public class LoginController
    {
        public OperacionResultado IniciarSesion(string usuario, string contrasena)
        {
            if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(contrasena))
            {
                return new OperacionResultado(false, 0, "Por favor complete todos los campos");
            }

            try
            {
                var peticion = new
                {
                    Username = usuario.Trim(),
                    Password = contrasena.Trim()
                };

                var respuesta = ApiClient.Post<LoginResponse>("Login", peticion);

                if (respuesta != null && respuesta.Success)
                {
                    return new OperacionResultado(true, 0, "Autenticación exitosa");
                }

                return new OperacionResultado(false, 0,
                    respuesta != null && !string.IsNullOrEmpty(respuesta.Message)
                        ? respuesta.Message
                        : "Credenciales incorrectas");
            }
            catch (Exception ex)
            {
                return new OperacionResultado(false, 0, "Error al conectar con el servidor: " + ex.Message);
            }
        }
    }
}
