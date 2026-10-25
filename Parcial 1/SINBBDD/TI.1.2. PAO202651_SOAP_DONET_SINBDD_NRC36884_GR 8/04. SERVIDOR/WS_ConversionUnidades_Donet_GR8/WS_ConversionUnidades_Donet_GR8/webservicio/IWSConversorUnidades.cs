using System.ServiceModel;
using WS_ConversionUnidades_Donet_GR8.dto;

namespace WS_ConversionUnidades_Donet_GR8.webservicio
{
    [ServiceContract]
    public interface IWSConversorUnidades
    {
        [OperationContract(Name = "convertir-unidades")]
        double convertirUnidades(ConversionDTO peticionConversion);
    }
}