using System;
using System.Collections.Generic;
using System.Globalization;
using System.ServiceModel;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.modelo;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.servicio;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.vista;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.controlador
{
    public class Controlador
    {
        private readonly ServicioSoap servicio;

        public Controlador()
        {
            servicio = new ServicioSoap();
        }

        public void Iniciar()
        {
            Consola.Inicializar();
            Consola.BannerBienvenida();

            if (!Autenticar())
            {
                Consola.Error("No se pudo iniciar sesion. Fin del programa.");
                return;
            }

            bool salir = false;
            while (!salir)
            {
                Consola.MenuPrincipal();
                string opcion = Console.ReadLine();

                switch (opcion)
                {
                    case "1":
                        RegistrarMovimiento();
                        break;
                    case "2":
                        ConsultarMovimientos();
                        break;
                    case "3":
                        salir = true;
                        Consola.Info("Gracias por usar EurekaBank · Grupo 8.");
                        break;
                    default:
                        Consola.Error("Opcion invalida. Intente nuevamente.");
                        break;
                }
            }
        }

        private bool Autenticar()
        {
            Consola.Encabezado("INICIO DE SESION");
            Consola.Etiqueta("Usuario....: ");
            string usuario = Console.ReadLine();
            Consola.Etiqueta("Contrasena.: ");
            string contrasena = LeerContrasena();

            try
            {
                Respuesta login = servicio.Login(usuario, contrasena);
                if (!login.EsExitoso())
                {
                    Consola.Error("Usuario o contrasena incorrectos.");
                    return false;
                }

                Consola.Ok("Bienvenido " + usuario + " a EurekaBank.");
                return true;
            }
            catch (CommunicationException ex)
            {
                Consola.Error("No se pudo contactar al servidor SOAP: " + ex.Message);
                return false;
            }
            catch (TimeoutException)
            {
                Consola.Error("El servidor SOAP no respondio a tiempo.");
                return false;
            }
        }

        private static string LeerContrasena()
        {
            string contrasena = string.Empty;
            ConsoleKeyInfo tecla;

            do
            {
                tecla = Console.ReadKey(true);
                if (tecla.Key == ConsoleKey.Backspace)
                {
                    if (contrasena.Length > 0)
                    {
                        contrasena = contrasena.Substring(0, contrasena.Length - 1);
                        Console.Write("\b \b");
                    }
                }
                else if (tecla.Key != ConsoleKey.Enter)
                {
                    contrasena += tecla.KeyChar;
                    Console.Write("*");
                }
            }
            while (tecla.Key != ConsoleKey.Enter);

            Console.WriteLine();
            return contrasena;
        }

        private void RegistrarMovimiento()
        {
            Consola.Encabezado("REGISTRAR MOVIMIENTO");

            Consola.Etiqueta("Numero de cuenta..: ");
            string cuenta = (Console.ReadLine() ?? string.Empty).Trim();

            Consola.Etiqueta("Importe...........: ");
            string importe = (Console.ReadLine() ?? string.Empty).Trim();

            Consola.MenuTipoMovimiento();
            string opcionTipo = (Console.ReadLine() ?? string.Empty).Trim();

            string tipoMovimiento;
            string cuentaDestino = string.Empty;

            switch (opcionTipo)
            {
                case "1":
                    tipoMovimiento = ServicioSoap.TipoDeposito;
                    break;
                case "2":
                    tipoMovimiento = ServicioSoap.TipoRetiro;
                    break;
                case "3":
                    tipoMovimiento = ServicioSoap.TipoTransferencia;
                    Consola.Etiqueta("Cuenta destino....: ");
                    cuentaDestino = (Console.ReadLine() ?? string.Empty).Trim();
                    if (cuentaDestino.Length == 0)
                    {
                        Consola.Error("La transferencia requiere una cuenta destino.");
                        return;
                    }

                    break;
                default:
                    Consola.Error("Tipo de movimiento no valido.");
                    return;
            }

            double importeNumerico;
            if (!double.TryParse(importe, NumberStyles.Number, CultureInfo.InvariantCulture, out importeNumerico)
                && !double.TryParse(importe, NumberStyles.Number, CultureInfo.CurrentCulture, out importeNumerico))
            {
                Consola.Error("El importe debe ser un numero.");
                return;
            }

            try
            {
                Respuesta respuesta = servicio.RegistrarMovimiento(cuenta, importeNumerico.ToString(CultureInfo.InvariantCulture), tipoMovimiento, cuentaDestino);
                if (respuesta.EsExitoso())
                {
                    Consola.Ok("Movimiento registrado con exito.");
                }
                else
                {
                    Consola.Error("El servidor rechazo el movimiento (cuenta, saldo o datos invalidos).");
                }
            }
            catch (CommunicationException ex)
            {
                Consola.Error("No se pudo contactar al servidor SOAP: " + ex.Message);
            }
            catch (TimeoutException)
            {
                Consola.Error("El servidor SOAP no respondio a tiempo.");
            }
        }

        private void ConsultarMovimientos()
        {
            Consola.Encabezado("CONSULTAR MOVIMIENTOS");

            Consola.Etiqueta("Numero de cuenta..: ");
            string cuenta = (Console.ReadLine() ?? string.Empty).Trim();

            try
            {
                List<Movimiento> movimientos = servicio.TraerMovimientos(cuenta);
                if (movimientos.Count == 0)
                {
                    Consola.Info("No se encontraron movimientos para la cuenta " + cuenta + ".");
                    return;
                }

                Consola.TablaMovimientos(movimientos);
            }
            catch (CommunicationException ex)
            {
                Consola.Error("No se pudo contactar al servidor SOAP: " + ex.Message);
            }
            catch (TimeoutException)
            {
                Consola.Error("El servidor SOAP no respondio a tiempo.");
            }
        }
    }
}
