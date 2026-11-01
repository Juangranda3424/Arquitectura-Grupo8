package ec.edu.monster.ws;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Aplicación JAX-RS de EurekaBank (servicio RESTful).
 *
 * Define el prefijo de todos los recursos publicados por el servidor:
 * http://localhost:8080/WS_EurekaBank_Java_GR8/resources
 *
 * Los recursos (@Path) se descubren automáticamente; no hace falta listarlos.
 */
@ApplicationPath("/resources")
public class RestApplication extends Application {
}
