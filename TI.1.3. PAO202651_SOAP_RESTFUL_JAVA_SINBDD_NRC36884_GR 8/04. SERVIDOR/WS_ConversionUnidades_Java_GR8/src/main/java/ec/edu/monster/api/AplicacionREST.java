package ec.edu.monster.api;

import java.util.Set;

import ec.edu.monster.api.recurso.AutenticadorRecurso;
import ec.edu.monster.api.recurso.CatalogoRecurso;
import ec.edu.monster.api.recurso.ConversionRecurso;
import ec.edu.monster.api.recurso.EstadoRecurso;
import ec.edu.monster.api.soporte.MapeadorExcepciones;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Aplicación JAX-RS del conversor de unidades.
 *
 * <p>Registra explícitamente los cuatro recursos y el mapeador de excepciones, de modo que el
 * conjunto de endpoints publicados no dependa del escaneo del contenedor.</p>
 */
@ApplicationPath("/api")
public class AplicacionREST extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                EstadoRecurso.class,
                AutenticadorRecurso.class,
                CatalogoRecurso.class,
                ConversionRecurso.class,
                MapeadorExcepciones.class);
    }
}
