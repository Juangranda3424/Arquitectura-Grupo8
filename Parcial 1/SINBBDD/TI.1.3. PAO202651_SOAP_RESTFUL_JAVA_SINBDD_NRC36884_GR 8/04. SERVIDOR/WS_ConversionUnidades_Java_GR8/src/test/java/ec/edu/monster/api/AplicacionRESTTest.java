package ec.edu.monster.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.junit.jupiter.api.Test;

import ec.edu.monster.api.recurso.AutenticadorRecurso;
import ec.edu.monster.api.recurso.CatalogoRecurso;
import ec.edu.monster.api.recurso.ConversionRecurso;
import ec.edu.monster.api.recurso.EstadoRecurso;
import ec.edu.monster.api.soporte.MapeadorExcepciones;

import jakarta.ws.rs.ApplicationPath;

class AplicacionRESTTest {

    @Test
    void seMontaEnLaRutaApi() {
        ApplicationPath ruta = AplicacionREST.class.getAnnotation(ApplicationPath.class);

        assertNotNull(ruta, "@ApplicationPath presente");
        assertEquals("/api", ruta.value());
    }

    @Test
    void registraExplicitamenteLosCuatroRecursosYElMapeador() {
        Set<Class<?>> esperadas = Set.of(
                EstadoRecurso.class,
                AutenticadorRecurso.class,
                CatalogoRecurso.class,
                ConversionRecurso.class,
                MapeadorExcepciones.class);

        assertEquals(esperadas, new AplicacionREST().getClasses());
    }
}
