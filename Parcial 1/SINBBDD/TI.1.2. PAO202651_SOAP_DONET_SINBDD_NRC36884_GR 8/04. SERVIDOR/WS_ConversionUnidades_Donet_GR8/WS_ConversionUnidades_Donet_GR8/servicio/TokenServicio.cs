using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.servicio
{
    public class TokenServicio
    {
        public string generarToken(Usuario usuario)
        {
            string token = Guid.NewGuid().ToString() + usuario.usuario;
            Constantes.tokensActivos.Add(token);

            return token;
        }

        public bool validarToken(string token)
        {
            if (string.IsNullOrWhiteSpace(token))
            {
                return false;
            }

            return Constantes.tokensActivos.Contains(token);
        }

        public bool eliminarToken(string token)
        {
            if (string.IsNullOrWhiteSpace(token))
            {
                return false;
            }
            return Constantes.tokensActivos.Remove(token);
        }
    }

}