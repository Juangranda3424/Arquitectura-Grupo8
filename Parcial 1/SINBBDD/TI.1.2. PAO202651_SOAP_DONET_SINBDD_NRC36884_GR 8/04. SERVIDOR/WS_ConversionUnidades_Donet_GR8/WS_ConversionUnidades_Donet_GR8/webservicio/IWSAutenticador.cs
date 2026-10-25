using System.ServiceModel;
using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.webservicio
{
    [ServiceContract]
    public interface IWSAutenticador
    {
        [OperationContract]
        string login(Usuario usuario);

        [OperationContract]
        bool logout(string token);
    }
}