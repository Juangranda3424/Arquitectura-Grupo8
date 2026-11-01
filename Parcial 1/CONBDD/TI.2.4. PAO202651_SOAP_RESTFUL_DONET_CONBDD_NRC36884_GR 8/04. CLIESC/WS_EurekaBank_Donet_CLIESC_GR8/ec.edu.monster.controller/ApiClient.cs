using System;
using System.Configuration;
using System.Net;
using System.Net.Http;
using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller
{
    /// <summary>
    /// Cliente HTTP del API REST de WS_EurekaBank_Donet_GR8.
    /// </summary>
    public class ApiClient
    {
        private static readonly object candado = new object();
        private static HttpClient cliente;
        private static string baseUrl;

        private static readonly JsonSerializerSettings jsonSettings = new JsonSerializerSettings
        {
            ContractResolver = new DefaultContractResolver
            {
                NamingStrategy = new CamelCaseNamingStrategy()
            },
            NullValueHandling = NullValueHandling.Ignore
        };

        public static string BaseUrl
        {
            get
            {
                if (baseUrl == null)
                {
                    baseUrl = ConfigurationManager.AppSettings["ApiBaseUrl"] ?? "http://localhost:5046/api/";
                    if (!baseUrl.EndsWith("/"))
                    {
                        baseUrl += "/";
                    }
                }
                return baseUrl;
            }
        }

        private static HttpClient Cliente
        {
            get
            {
                lock (candado)
                {
                    if (cliente == null)
                    {
                        ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12 | SecurityProtocolType.Tls11 | SecurityProtocolType.Tls;
                        var handler = new HttpClientHandler();
                        handler.ServerCertificateCustomValidationCallback = (message, cert, chain, errors) => true;
                        handler.UseProxy = false;
                        cliente = new HttpClient(handler);
                        cliente.Timeout = TimeSpan.FromSeconds(30);
                        cliente.DefaultRequestHeaders.Accept.Clear();
                        cliente.DefaultRequestHeaders.Accept.Add(
                            new System.Net.Http.Headers.MediaTypeWithQualityHeaderValue("application/json"));
                    }
                    return cliente;
                }
            }
        }

        public static T Get<T>(string endpoint)
        {
            try
            {
                var response = Cliente.GetAsync(BaseUrl + endpoint).Result;

                if (response.StatusCode == HttpStatusCode.NotFound)
                {
                    return default(T);
                }

                var json = response.Content.ReadAsStringAsync().Result;

                if (!response.IsSuccessStatusCode)
                {
                    throw new Exception(MensajeDeError(json, response.StatusCode));
                }

                return JsonConvert.DeserializeObject<T>(json, jsonSettings);
            }
            catch (AggregateException ex)
            {
                throw new Exception("Error al conectar con el servidor: " + ex.GetBaseException().Message);
            }
        }

        public static T Post<T>(string endpoint, object data)
        {
            try
            {
                var jsonData = JsonConvert.SerializeObject(data);
                var contenido = new StringContent(jsonData, Encoding.UTF8, "application/json");
                var response = Cliente.PostAsync(BaseUrl + endpoint, contenido).Result;
                var json = response.Content.ReadAsStringAsync().Result;

                // El servidor responde 400/401 con el mismo contrato (success/message),
                // así que se deserializa para mostrar el mensaje real.
                if (!response.IsSuccessStatusCode)
                {
                    if (response.StatusCode == HttpStatusCode.BadRequest ||
                        response.StatusCode == HttpStatusCode.Unauthorized)
                    {
                        try
                        {
                            var resultado = JsonConvert.DeserializeObject<T>(json, jsonSettings);
                            if (resultado != null)
                            {
                                return resultado;
                            }
                        }
                        catch
                        {
                            // se propaga el mensaje de error estándar
                        }
                    }

                    throw new Exception(MensajeDeError(json, response.StatusCode));
                }

                return JsonConvert.DeserializeObject<T>(json, jsonSettings);
            }
            catch (AggregateException ex)
            {
                throw new Exception("Error al conectar con el servidor: " + ex.GetBaseException().Message);
            }
        }

        private static string MensajeDeError(string json, HttpStatusCode codigo)
        {
            try
            {
                var error = JsonConvert.DeserializeObject<dynamic>(json);
                if (error != null)
                {
                    if (error.message != null) return error.message.ToString();
                    if (error.mensaje != null) return error.mensaje.ToString();
                }
            }
            catch
            {
                // respuesta sin JSON
            }

            return "Error " + (int)codigo + " del servidor.";
        }
    }
}
