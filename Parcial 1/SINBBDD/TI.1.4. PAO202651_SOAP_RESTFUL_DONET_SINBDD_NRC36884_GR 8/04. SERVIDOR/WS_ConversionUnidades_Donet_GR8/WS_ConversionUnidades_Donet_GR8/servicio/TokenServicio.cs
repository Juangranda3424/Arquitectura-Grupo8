using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.servicio
{
    /// <summary>
    /// Registro de tokens de sesión en memoria (SINBDD). Igual que en TI.1.2, el registro es único para
    /// todo el servidor y los tokens no vencen: solo se invalidan con el logout o al reiniciar el proceso.
    /// </summary>
    public class TokenServicio
    {
        public string generarToken(Usuario usuario)
        {
            string token = Guid.NewGuid().ToString() + usuario.usuario;
            Constantes.tokensActivos.TryAdd(token, 0);

            return token;
        }

        public bool validarToken(string? token)
        {
            if (string.IsNullOrWhiteSpace(token))
            {
                return false;
            }

            return Constantes.tokensActivos.ContainsKey(token);
        }

        public bool eliminarToken(string? token)
        {
            if (string.IsNullOrWhiteSpace(token))
            {
                return false;
            }
            return Constantes.tokensActivos.TryRemove(token, out _);
        }
    }
}
