package ec.edu.espe.banquito;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BanquitoCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BanquitoCoreApplication.class, args);
    }

    /** Single source of "today" for the credit rules; tests replace it with a fixed clock. */
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
