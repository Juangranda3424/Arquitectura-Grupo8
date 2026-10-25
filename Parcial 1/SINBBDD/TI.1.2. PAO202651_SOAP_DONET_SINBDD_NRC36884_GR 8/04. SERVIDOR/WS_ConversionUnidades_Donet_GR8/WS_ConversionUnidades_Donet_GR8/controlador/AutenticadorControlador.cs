using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.controlador
{
    public class AutenticadorControlador
    {
        private readonly AutenticadorServicio autenticador = new AutenticadorServicio();

        public string login(Usuario usuario)
        {
            return autenticador.autenticadorLogin(usuario);
        }

        public bool logout(string token)
        {
            return autenticador.autenticadorLogout(token);
        }
    }
}