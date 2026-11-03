package ec.edu.monster.phoneshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PhoneShopWebClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneShopWebClientApplication.class, args);
    }
}
