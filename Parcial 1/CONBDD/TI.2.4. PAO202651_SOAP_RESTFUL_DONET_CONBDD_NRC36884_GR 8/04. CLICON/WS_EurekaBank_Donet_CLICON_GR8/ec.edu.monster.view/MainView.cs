using System;
using System.Collections.Generic;
using System.Linq;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.controller;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.model;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.view
{
    public class MainView
    {
        private static readonly MainController mainController = new MainController();

        public static void Iniciar()
        {
            while (true)
            {
                if (mainController.GetUsuarioActual() == null)
                {
                    MenuLogin();
                }
                else
                {
                    MenuPrincipal();
                }
            }
        }

        private static void MenuLogin()
        {
            ConsoleTheme.Banner();
            ConsoleTheme.Menu("ACCESO AL SISTEMA",
                "Iniciar Sesión",
                "Salir");
            ConsoleTheme.Nota("Servidor: " + mainController.GetApiBaseUrl());
            Console.WriteLine();

            string opcionStr = ConsoleTheme.Preguntar("Opción");
            int opcion;
            if (!int.TryParse(opcionStr, out opcion))
            {
                ConsoleTheme.Aviso("Opción inválida. Intente nuevamente.");
                ConsoleTheme.Esperar();
                return;
            }

            switch (opcion)
            {
                case 1:
                    IniciarSesion();
                    break;
                case 2:
                    Console.WriteLine();
                    Console.WriteLine("  " + ConsoleTheme.Marca("Gracias por usar EurekaBank · Grupo 8. ¡Hasta luego!"));
                    Environment.Exit(0);
                    break;
                default:
                    ConsoleTheme.Aviso("Opción inválida. Intente nuevamente.");
                    ConsoleTheme.Esperar();
                    break;
            }
        }

        private static void IniciarSesion()
        {
            Console.WriteLine();
            ConsoleTheme.Titulo("Iniciar Sesión");

            string username = ConsoleTheme.Preguntar("Usuario");
            Console.Write("  " + ConsoleTheme.Marca("› ") + ConsoleTheme.Normal("Contraseña: "));
            string password = LeerPassword();

            Console.WriteLine();
            try
            {
                if (mainController.IniciarSesion(username, password))
                {
                    ConsoleTheme.Exito("Bienvenido, " + username);
                }
                else
                {
                    ConsoleTheme.Aviso("Credenciales incorrectas. Intente nuevamente.");
                }
            }
            catch (InvalidOperationException ex)
            {
                ConsoleTheme.Aviso(ex.Message);
            }
            ConsoleTheme.Esperar();
        }

        private static void MenuPrincipal()
        {
            ConsoleTheme.Banner();
            ConsoleTheme.Nota("Sesión: " + mainController.GetUsuarioActual());
            ConsoleTheme.Menu("MENÚ PRINCIPAL",
                "Realizar Depósito",
                "Realizar Retiro",
                "Realizar Transferencia",
                "Ver Movimientos",
                "Ver Datos de la Cuenta",
                "Cerrar Sesión");
            Console.WriteLine();

            string opcionStr = ConsoleTheme.Preguntar("Opción");
            int opcion;
            if (!int.TryParse(opcionStr, out opcion))
            {
                ConsoleTheme.Aviso("Opción inválida. Intente nuevamente.");
                ConsoleTheme.Esperar();
                return;
            }

            switch (opcion)
            {
                case 1:
                    RealizarDeposito();
                    break;
                case 2:
                    RealizarRetiro();
                    break;
                case 3:
                    RealizarTransferencia();
                    break;
                case 4:
                    VerMovimientos();
                    break;
                case 5:
                    VerDatosCuenta();
                    break;
                case 6:
                    CerrarSesion();
                    break;
                default:
                    ConsoleTheme.Aviso("Opción inválida. Intente nuevamente.");
                    ConsoleTheme.Esperar();
                    break;
            }
        }

        private static void RealizarDeposito()
        {
            Console.WriteLine();
            ConsoleTheme.Titulo("Nuevo Depósito");

            string cuenta = ConsoleTheme.Preguntar("Número de cuenta");
            string monto = ConsoleTheme.Preguntar("Monto a depositar");

            Console.WriteLine();
            var resultado = mainController.RealizarDeposito(cuenta, monto);
            MostrarResultado(resultado, "El depósito no se pudo realizar.");
            ConsoleTheme.Esperar();
        }

        private static void RealizarRetiro()
        {
            Console.WriteLine();
            ConsoleTheme.Titulo("Nuevo Retiro");

            string cuenta = ConsoleTheme.Preguntar("Número de cuenta");
            string monto = ConsoleTheme.Preguntar("Monto a retirar");

            Console.WriteLine();
            var resultado = mainController.RealizarRetiro(cuenta, monto);
            MostrarResultado(resultado, "El retiro no se pudo realizar.");
            ConsoleTheme.Esperar();
        }

        private static void RealizarTransferencia()
        {
            Console.WriteLine();
            ConsoleTheme.Titulo("Nueva Transferencia");

            string cuentaOrigen = ConsoleTheme.Preguntar("Cuenta origen");
            string cuentaDestino = ConsoleTheme.Preguntar("Cuenta destino");
            string monto = ConsoleTheme.Preguntar("Monto a transferir");

            Console.WriteLine();
            var resultado = mainController.RealizarTransferencia(cuentaOrigen, cuentaDestino, monto);
            MostrarResultado(resultado, "La transferencia no se pudo realizar.");
            ConsoleTheme.Esperar();
        }

        private static void MostrarResultado(OperacionResultado resultado, string mensajeFallo)
        {
            if (resultado == null)
            {
                ConsoleTheme.Aviso(mensajeFallo);
                return;
            }

            if (resultado.Exito)
            {
                ConsoleTheme.Exito(resultado.Mensaje);
                ConsoleTheme.Caja("OPERACIÓN REGISTRADA",
                    "Saldo actual : S/ " + resultado.Saldo.ToString("N2"),
                    "Mensaje      : " + resultado.Mensaje);
            }
            else
            {
                ConsoleTheme.Aviso(resultado.Mensaje ?? mensajeFallo);
            }
        }

        private static void VerMovimientos()
        {
            Console.WriteLine();
            ConsoleTheme.Titulo("Movimientos de la Cuenta");

            string cuenta = ConsoleTheme.Preguntar("Número de cuenta");

            List<MovimientoModel> movimientos;
            CuentaModel cuentaModel;
            try
            {
                movimientos = mainController.VerMovimientos(cuenta);
                cuentaModel = mainController.VerDatosCuenta(cuenta);
            }
            catch (Exception ex)
            {
                Console.WriteLine();
                ConsoleTheme.Aviso("Error al obtener los movimientos: " + ex.Message);
                ConsoleTheme.Esperar();
                return;
            }

            Console.WriteLine();

            if (cuentaModel != null)
            {
                ConsoleTheme.Caja("RESUMEN DE LA CUENTA",
                    "Cuenta       : " + (cuentaModel.ChrCuenCodigo ?? cuenta),
                    "Saldo actual : S/ " + cuentaModel.DecCuenSaldo.ToString("N2"),
                    "Movimientos  : " + cuentaModel.IntCuenContMov);
            }

            if (movimientos == null || movimientos.Count == 0)
            {
                ConsoleTheme.Nota("No se encontraron movimientos para la cuenta " + cuenta + ".");
                ConsoleTheme.Esperar();
                return;
            }

            Console.WriteLine();
            Console.WriteLine("  " + ConsoleTheme.Marca("Nro") + "     " +
                              ConsoleTheme.Marca("Fecha      ") + " " +
                              ConsoleTheme.Marca("Tipo") + "  " +
                              ConsoleTheme.Marca("Descripción          ") + " " +
                              ConsoleTheme.Marca("Importe     ") + " " +
                              ConsoleTheme.Marca("Cta. Ref.") + " " +
                              ConsoleTheme.Marca("Saldo"));
            Console.WriteLine("  " + ConsoleTheme.Tenue(new string('─', 96)));

            foreach (var mov in movimientos)
            {
                string fecha = mov.FechaMovimientoDt.HasValue
                    ? mov.FechaMovimientoDt.Value.ToString("dd/MM/yyyy")
                    : (mov.FechaMovimiento ?? "N/A");

                Console.WriteLine("  " + Pad(mov.NumeroMovimiento.ToString(), 6) + "  " +
                                  Pad(fecha, 10) + "  " +
                                  Pad(mov.CodigoTipoMovimiento ?? "N/A", 4) + "  " +
                                  Pad(Cortar(mov.TipoDescripcion ?? "N/A", 20), 20) + "  " +
                                  Pad(mov.ImporteMovimiento.ToString("N2"), 11) + "  " +
                                  Pad(mov.CuentaReferencia ?? "—", 8) + "  " +
                                  mov.Saldo.ToString("N2"));
            }

            Console.WriteLine("  " + ConsoleTheme.Tenue(new string('─', 96)));

            double totalIngresos = movimientos
                .Where(m => m.TipoDescripcion == null ||
                            !m.TipoDescripcion.Equals("Retiro", StringComparison.OrdinalIgnoreCase))
                .Sum(m => m.ImporteMovimiento);

            double totalEgresos = movimientos
                .Where(m => m.TipoDescripcion != null &&
                            m.TipoDescripcion.Equals("Retiro", StringComparison.OrdinalIgnoreCase))
                .Sum(m => m.ImporteMovimiento);

            Console.WriteLine();
            ConsoleTheme.Caja("TOTALES",
                "Ingresos     : S/ " + totalIngresos.ToString("N2"),
                "Egresos      : S/ " + Math.Abs(totalEgresos).ToString("N2"),
                "Saldo neto   : S/ " + (totalIngresos + totalEgresos).ToString("N2"),
                "Saldo actual : S/ " + (cuentaModel != null ? cuentaModel.DecCuenSaldo : 0).ToString("N2"));

            ConsoleTheme.Esperar();
        }

        private static void VerDatosCuenta()
        {
            Console.WriteLine();
            ConsoleTheme.Titulo("Datos de la Cuenta");

            string cuenta = ConsoleTheme.Preguntar("Número de cuenta");

            CuentaModel cuentaModel;
            try
            {
                cuentaModel = mainController.VerDatosCuenta(cuenta);
            }
            catch (Exception ex)
            {
                Console.WriteLine();
                ConsoleTheme.Aviso("Error al obtener los datos de la cuenta: " + ex.Message);
                ConsoleTheme.Esperar();
                return;
            }

            Console.WriteLine();
            if (cuentaModel == null)
            {
                ConsoleTheme.Aviso("No se encontraron datos para la cuenta: " + cuenta);
            }
            else
            {
                ConsoleTheme.Caja("CUENTA " + (cuentaModel.ChrCuenCodigo ?? cuenta),
                    "Saldo               : S/ " + cuentaModel.DecCuenSaldo.ToString("N2"),
                    "Contador movimientos: " + cuentaModel.IntCuenContMov,
                    "Moneda              : " + (cuentaModel.ChrMoneCodigo ?? "—"),
                    "Sucursal            : " + (cuentaModel.ChrSucucodigo ?? "—"),
                    "Estado              : " + (cuentaModel.VchCuenEstado ?? "—"),
                    "Cliente             : " + (cuentaModel.ChrClieCodigo ?? "—"));
            }

            ConsoleTheme.Esperar();
        }

        private static void CerrarSesion()
        {
            mainController.CerrarSesion();
            Console.WriteLine();
            ConsoleTheme.Exito("Sesión cerrada exitosamente.");
            ConsoleTheme.Esperar();
        }

        // ---- utilidades de presentación ----

        private static string LeerPassword()
        {
            // La consola del cliente mantiene el eco del original (Console.ReadLine).
            return Console.ReadLine();
        }

        private static string Pad(string texto, int ancho)
        {
            if (texto == null)
            {
                texto = string.Empty;
            }
            if (texto.Length >= ancho)
            {
                return texto;
            }
            return texto.PadRight(ancho);
        }

        private static string Cortar(string texto, int ancho)
        {
            if (texto == null)
            {
                return string.Empty;
            }
            return texto.Length <= ancho ? texto : texto.Substring(0, ancho);
        }
    }
}
