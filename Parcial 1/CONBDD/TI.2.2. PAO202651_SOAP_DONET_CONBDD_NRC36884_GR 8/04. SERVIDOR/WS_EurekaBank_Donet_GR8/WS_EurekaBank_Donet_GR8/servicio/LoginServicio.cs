using System;

namespace WS_EurekaBank_Donet_GR8.ec.edu.monster.servicio
{
    /// <summary>
    /// Autenticacion de los clientes del banco.
    /// Mismas credenciales que el servidor SOAP de referencia.
    /// </summary>
    public class LoginServicio
    {
        public bool Login(string usuario, string contrasena)
        {
            if (usuario == null || contrasena == null)
            {
                return false;
            }

            bool usuarioMonster = usuario == "MONSTER" && contrasena == "MONSTER9";
            bool usuarioAdmin = usuario == "admin" && contrasena == "admin";

            return usuarioMonster || usuarioAdmin;
        }
    }
}
