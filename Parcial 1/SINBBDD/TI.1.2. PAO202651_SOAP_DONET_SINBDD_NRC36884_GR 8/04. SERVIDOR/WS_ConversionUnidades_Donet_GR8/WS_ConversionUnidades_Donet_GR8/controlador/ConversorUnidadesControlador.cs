using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using WS_ConversionUnidades_Donet_GR8.dto;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.controlador
{
    public class ConversorUnidadesControlador
    {
        private readonly ConversorServicio conversorServicio = new ConversorServicio();

        public double? conversor(ConversionDTO dto)
        {
            return conversorServicio.procesarConversion(dto).ValorResultado;
        }
    }
}