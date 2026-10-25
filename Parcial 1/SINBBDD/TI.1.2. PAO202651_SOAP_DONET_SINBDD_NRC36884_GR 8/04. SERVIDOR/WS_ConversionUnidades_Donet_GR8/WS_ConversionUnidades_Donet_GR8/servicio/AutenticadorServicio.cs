using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.servicio
{
    public class AutenticadorServicio
    {
        private readonly TokenServicio token = new TokenServicio();

        public string autenticadorLogin(Usuario usuario)
        {
            if (usuario == null
                || usuario.usuario == null
                || usuario.contrasena == null
                || string.IsNullOrWhiteSpace(usuario.usuario)
                || string.IsNullOrEmpty(usuario.contrasena))
            {
                return "Usuario invalido";
            }

            if ("MONSTER".Equals(usuario.usuario)
                && "MONSTER9".Equals(usuario.contrasena))
            {
                return token.generarToken(usuario);
            }
            else
            {
                return "Usuario invalido";
            }
        }

        public bool autenticadorLogout(string tokenUsuario)
        {
            try
            {
                return token.eliminarToken(tokenUsuario);
            }
            catch (System.Exception)
            {
                return false;
            }
        }
    }

}