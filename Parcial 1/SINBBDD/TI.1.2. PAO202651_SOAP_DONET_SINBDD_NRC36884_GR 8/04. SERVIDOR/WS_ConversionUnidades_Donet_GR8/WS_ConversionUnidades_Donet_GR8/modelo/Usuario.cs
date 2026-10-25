using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WS_ConversionUnidades_Donet_GR8.modelo
{
    public class Usuario
    {

        public string usuario { get; set; }
        public string contrasena { get; set; }
        public Usuario() { }
        public Usuario(string usuario, string contrasena)
        {
            this.usuario = usuario;
            this.contrasena = contrasena;
        }

    }
}