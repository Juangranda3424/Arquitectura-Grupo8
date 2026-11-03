package ec.edu.monster.phoneshop.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ec.edu.monster.phoneshop.client.PhoneShopServiceClient;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Getter
@Setter
public class ApplicationContext {
    private static ApplicationContext instance;
    private String serverIp;
    private String serverPort = "9001";
    private String identificationNumber;
    private String token;
    private Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private ApplicationContext() {}

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }

        return instance;
    }

    public Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://" + ((serverIp == null || serverIp.trim().isEmpty()) ? "10.0.2.2" : serverIp) + ":" + serverPort + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public PhoneShopServiceClient buildPhoneShopServiceClient() {
        return buildRetrofit().create(PhoneShopServiceClient.class);
    }
}
