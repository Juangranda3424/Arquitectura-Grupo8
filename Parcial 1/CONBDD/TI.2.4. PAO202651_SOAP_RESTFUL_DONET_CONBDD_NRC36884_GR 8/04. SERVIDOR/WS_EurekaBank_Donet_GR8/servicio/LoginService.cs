using System.Security.Cryptography;
using System.Text;
using Microsoft.Data.SqlClient;
using WS_EurekaBank_Donet_GR8.db;

namespace WS_EurekaBank_Donet_GR8.servicio;

/// <summary>
/// Validates credentials against dbo.credenciales, where the password is stored
/// as an MD5 hex hash. The user name comparison is case-sensitive.
/// </summary>
public class LoginService
{
    private const string Sql =
        "SELECT usuario, contrasena FROM credenciales WHERE usuario = @usuario";

    private readonly IConfiguration _configuration;

    public LoginService(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public bool Login(string? username, string? password)
    {
        if (username == null || password == null)
        {
            return false;
        }

        string hash = Convert.ToHexString(MD5.HashData(Encoding.UTF8.GetBytes(password)));

        using var connection = AccesoBD.GetConnection(_configuration);
        connection.Open();
        using var command = new SqlCommand(Sql, connection);
        command.Parameters.AddWithValue("@usuario", username);
        using var reader = command.ExecuteReader();
        while (reader.Read())
        {
            if (string.Equals(username, reader.GetString(0), StringComparison.Ordinal)
                && string.Equals(hash, reader.GetString(1), StringComparison.OrdinalIgnoreCase))
            {
                return true;
            }
        }

        return false;
    }
}
