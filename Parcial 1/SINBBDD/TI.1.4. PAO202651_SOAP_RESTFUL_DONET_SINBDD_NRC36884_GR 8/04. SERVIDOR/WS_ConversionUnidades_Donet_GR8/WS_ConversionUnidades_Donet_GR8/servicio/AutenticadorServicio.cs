using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.servicio
{
    public class AutenticadorServicio
    {
        /// <summary>Respuesta de paridad con TI.1.2 para credenciales inválidas.</summary>
        public const string USUARIO_INVALIDO = "Usuario invalido";

        private readonly TokenServicio token = new TokenServicio();

        public string autenticadorLogin(Usuario? usuario)
        {
            if (usuario == null
                || usuario.usuario == null
                || usuario.contrasena == null
                || string.IsNullOrWhiteSpace(usuario.usuario)
                || string.IsNullOrEmpty(usuario.contrasena))
            {
                return USUARIO_INVALIDO;
            }

            if (Constantes.usuario.Equals(usuario.usuario)
                && Constantes.contrasena.Equals(usuario.contrasena))
            {
                return token.generarToken(usuario);
            }

            return USUARIO_INVALIDO;
        }

        public bool autenticadorLogout(string? tokenUsuario)
        {
            try
            {
                return token.eliminarToken(tokenUsuario);
            }
            catch (Exception)
            {
                return false;
            }
        }
    }
}
