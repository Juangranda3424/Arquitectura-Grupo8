using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Net.Http;
using System.Text;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.model;
using Newtonsoft.Json;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.controller
{
    /// <summary>
    /// Controlador del cliente de consola: consume el API REST de
    /// WS_EurekaBank_Donet_GR8 (TI.2.4 REST .NET).
    /// </summary>
    public class MainController
    {
        private string usuarioActual = null;
        private readonly HttpClient httpClient;
        private readonly string apiBaseUrl;

        public MainController()
        {
            apiBaseUrl = ConfigurationManager.AppSettings["ApiBaseUrl"] ?? "http://localhost:5046/api";

            // Asegurar que la URL base termine con /
            if (!apiBaseUrl.EndsWith("/"))
            {
                apiBaseUrl += "/";
            }

            // Configurar HttpClient para desarrollo (ignorar certificados SSL)
            var handler = new HttpClientHandler();
            handler.ServerCertificateCustomValidationCallback = (message, cert, chain, errors) => true;

            httpClient = new HttpClient(handler);
            httpClient.BaseAddress = new Uri(apiBaseUrl);
            httpClient.DefaultRequestHeaders.Accept.Clear();
            httpClient.DefaultRequestHeaders.Accept.Add(
                new System.Net.Http.Headers.MediaTypeWithQualityHeaderValue("application/json"));
        }

        public string GetUsuarioActual()
        {
            return usuarioActual;
        }

        public string GetApiBaseUrl()
        {
            return apiBaseUrl;
        }

        public bool IniciarSesion(string username, string password)
        {
            try
            {
                var loginRequest = new { Username = username, Password = password };
                var json = JsonConvert.SerializeObject(loginRequest);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var url = new Uri(new Uri(apiBaseUrl), "login");
                var response = httpClient.PostAsync(url, content).Result;
                var responseContent = response.Content.ReadAsStringAsync().Result;

                if (response.IsSuccessStatusCode)
                {
                    var result = JsonConvert.DeserializeObject<dynamic>(responseContent);

                    if (result != null && result.success == true)
                    {
                        usuarioActual = username;
                        return true;
                    }
                }
                else
                {
                    try
                    {
                        var errorResult = JsonConvert.DeserializeObject<dynamic>(responseContent);
                        if (errorResult != null && errorResult.message != null)
                        {
                            Console.Error.WriteLine("Error: " + errorResult.message);
                        }
                    }
                    catch
                    {
                    }
                }
            }
            catch (Exception ex)
            {
                // Fallo de conexión: no es lo mismo que credenciales rechazadas
                throw new InvalidOperationException("No se pudo conectar con el servidor REST en " + apiBaseUrl
                    + ". Verifique que esté en ejecución.", ex);
            }
            return false;
        }

        public void CerrarSesion()
        {
            usuarioActual = null;
        }

        /// <summary>
        /// Ejecuta una operación sobre la cuenta: DEP (depósito), RET (retiro) o TRA (transferencia).
        /// </summary>
        public OperacionResultado RealizarOperacion(string cuenta, string monto, string tipo, string cuentaDestino)
        {
            try
            {
                object request;
                string endpoint;

                if (tipo == "DEP")
                {
                    request = new { Cuenta = cuenta, Monto = double.Parse(monto) };
                    endpoint = "cuenta/deposito";
                }
                else if (tipo == "RET")
                {
                    request = new { Cuenta = cuenta, Monto = double.Parse(monto) };
                    endpoint = "cuenta/retiro";
                }
                else if (tipo == "TRA")
                {
                    request = new { Cuenta = cuenta, Monto = double.Parse(monto), CuentaDestino = cuentaDestino };
                    endpoint = "cuenta/transferencia";
                }
                else
                {
                    return new OperacionResultado(false, 0, "Tipo de operación no soportado: " + tipo);
                }

                var json = JsonConvert.SerializeObject(request);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var url = new Uri(new Uri(apiBaseUrl), endpoint);
                var response = httpClient.PostAsync(url, content).Result;
                var responseContent = response.Content.ReadAsStringAsync().Result;

                if (response.IsSuccessStatusCode)
                {
                    var result = JsonConvert.DeserializeObject<dynamic>(responseContent);
                    bool exito = result != null && result.success == true;
                    double saldo = result != null && result.saldo != null ? (double)result.saldo : 0;
                    string mensaje = result != null && result.mensaje != null
                        ? result.mensaje.ToString()
                        : (exito ? "Operación realizada exitosamente" : "No se pudo realizar la operación");
                    return new OperacionResultado(exito, saldo, mensaje);
                }

                // 400 / 500 devuelven un OperacionResponse con el detalle
                try
                {
                    var error = JsonConvert.DeserializeObject<dynamic>(responseContent);
                    string mensaje = error != null && error.mensaje != null
                        ? error.mensaje.ToString()
                        : "Error " + (int)response.StatusCode + " al realizar la operación";
                    return new OperacionResultado(false, 0, mensaje);
                }
                catch
                {
                    return new OperacionResultado(false, 0, "Error " + (int)response.StatusCode + " al realizar la operación");
                }
            }
            catch (Exception ex)
            {
                return new OperacionResultado(false, 0, "Error al realizar la operación: " + ex.Message);
            }
        }

        public OperacionResultado RealizarDeposito(string cuenta, string monto)
        {
            return RealizarOperacion(cuenta, monto, "DEP", null);
        }

        public OperacionResultado RealizarRetiro(string cuenta, string monto)
        {
            return RealizarOperacion(cuenta, monto, "RET", null);
        }

        public OperacionResultado RealizarTransferencia(string cuentaOrigen, string cuentaDestino, string monto)
        {
            return RealizarOperacion(cuentaOrigen, monto, "TRA", cuentaDestino);
        }

        /// <summary>
        /// Obtiene los movimientos de una cuenta (más reciente primero, con saldo acumulado).
        /// </summary>
        public List<MovimientoModel> VerMovimientos(string cuenta)
        {
            var movimientoRequest = new { Numcuenta = cuenta };
            var contenido = new StringContent(JsonConvert.SerializeObject(movimientoRequest), Encoding.UTF8, "application/json");

            var url = new Uri(new Uri(apiBaseUrl), "movimiento");
            var response = httpClient.PostAsync(url, contenido).Result;

            if (!response.IsSuccessStatusCode)
            {
                var detalle = response.Content.ReadAsStringAsync().Result;
                throw new Exception("No se pudieron obtener los movimientos (" + (int)response.StatusCode + "): " + detalle);
            }

            var json = response.Content.ReadAsStringAsync().Result;
            var movimientos = JsonConvert.DeserializeObject<List<MovimientoModel>>(json);
            return movimientos ?? new List<MovimientoModel>();
        }

        /// <summary>
        /// Obtiene los datos de una cuenta por su código.
        /// </summary>
        public CuentaModel VerDatosCuenta(string cuenta)
        {
            var url = new Uri(new Uri(apiBaseUrl), "cuenta/" + cuenta);
            var response = httpClient.GetAsync(url).Result;

            if (!response.IsSuccessStatusCode)
            {
                return null;
            }

            var json = response.Content.ReadAsStringAsync().Result;
            return JsonConvert.DeserializeObject<CuentaModel>(json);
        }
    }
}
