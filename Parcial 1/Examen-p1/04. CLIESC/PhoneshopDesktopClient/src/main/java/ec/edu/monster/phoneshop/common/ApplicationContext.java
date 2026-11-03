package ec.edu.monster.phoneshop.common;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationContext {
    private static ApplicationContext instance;

    private String serverIp = "localhost";
    private String serverPort = "9001";
    private String token;
    private String identificationNumber;

    private ApplicationContext() {}

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }

        return instance;
    }

    public <T> T buildFeignClient(Class<T> clazz) {
        return Feign.builder()
                .client(new OkHttpClient())
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .errorDecoder(new FeignErrorDecoder())
                .logLevel(Logger.Level.FULL)
                .target(clazz, "http://" + ((serverIp == null || serverIp.trim().isBlank()) ? "localhost" : serverIp) + ":"
                        + ((serverPort == null || serverPort.trim().isBlank()) ? "9001" : serverPort));
    }
}
