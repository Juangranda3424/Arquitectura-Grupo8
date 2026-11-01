namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.modelo
{
    /// <summary>
    /// Respuesta de las operaciones que devuelven 1 (exito) o -1 (fallo).
    /// </summary>
    public class Respuesta
    {
        public int Resultado { get; set; }

        public bool EsExitoso()
        {
            return Resultado == 1;
        }
    }
}
