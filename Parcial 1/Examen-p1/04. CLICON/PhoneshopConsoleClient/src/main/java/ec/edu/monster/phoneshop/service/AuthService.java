package ec.edu.monster.phoneshop.service;

import ec.edu.monster.phoneshop.client.PhoneShopServerClient;
import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.dto.AuthCredentialsDto;
import ec.edu.monster.phoneshop.dto.AuthResponseDto;

public class AuthService {
    private final PhoneShopServerClient client;

    public AuthService() {
        ApplicationContext applicationContext = ApplicationContext.getInstance();
        client = applicationContext.buildFeignClient(PhoneShopServerClient.class);
    }

    public AuthResponseDto login(AuthCredentialsDto credentials) {
        return client.login(credentials);
    }
}
