using System.Web;
using System.Web.Mvc;
using System.Web.Routing;

namespace WS_EurekaBank_Donet_CLIWEB_GR8
{
    public class Global : HttpApplication
    {
        void Application_Start(object sender, System.EventArgs e)
        {
            // Código que se ejecuta al iniciar la aplicación
            AreaRegistration.RegisterAllAreas();
            RouteConfig.RegisterRoutes(RouteTable.Routes);
        }
    }
}
