using WS_EurekaBank_Donet_GR8.model;
using WS_EurekaBank_Donet_GR8.servicio;
using Microsoft.AspNetCore.Mvc;

namespace WS_EurekaBank_Donet_GR8.Controllers;

[ApiController]
[Route("api/[controller]")]
public class LoginController : ControllerBase
{
    private readonly IConfiguration _configuration;

    public LoginController(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    [HttpPost]
    [Consumes("application/json")]
    [Produces("application/json")]
    public IActionResult Authenticate([FromBody] LoginRequest request)
    {
        if (request == null || string.IsNullOrEmpty(request.Username) || string.IsNullOrEmpty(request.Password))
        {
            return BadRequest(new { success = false, message = "Username y Password son requeridos" });
        }

        var service = new LoginService(_configuration);
        bool isValid = service.Login(request.Username, request.Password);

        if (isValid)
        {
            return Ok(new { success = true, message = "Autenticación exitosa" });
        }
        else
        {
            return Unauthorized(new { success = false, message = "Credenciales inválidas" });
        }
    }
}

