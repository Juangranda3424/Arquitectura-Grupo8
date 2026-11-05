namespace WS_EurekaBank_Donet_GR8.servicio;

public class LoginService
{
    public bool Login(string? username, string? password)
    {
        if ((username == "MONSTER" && password == "MONSTER9") ||
            (username == "admin" && password == "admin"))
        {
            return true;
        }
        return false;
    }
}

