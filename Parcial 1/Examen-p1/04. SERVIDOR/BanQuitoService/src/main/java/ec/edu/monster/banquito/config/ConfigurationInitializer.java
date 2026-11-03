package ec.edu.monster.banquito.config;

import ec.edu.monster.banquito.entity.*;
import ec.edu.monster.banquito.repository.*;
import ec.edu.monster.banquito.service.DatabaseSeeder;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class ConfigurationInitializer {
    private final Logger logger = Logger.getLogger(ConfigurationInitializer.class.getName());
    private final DatabaseSeeder databaseSeeder;


    @PostConstruct
    public void init() {
        createDirectories();
        databaseSeeder.seed();
    }

    private void createDirectories() {
        createDirectory(Paths.get("uploads"));
    }

    private void createDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
                logger.info("Carpeta '" + path + "' creada exitosamente.");
            } catch (Exception e) {
                logger.severe("Error al crear la carpeta '" + path + "': " + e.getMessage());
            }
        } else {
            logger.info("La carpeta '" + path + "' ya existe.");
        }
    }
}
