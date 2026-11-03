package ec.edu.monster.phoneshop.service.impl;

import ec.edu.monster.phoneshop.client.PhoneShopServerClient;
import ec.edu.monster.phoneshop.dto.*;
import ec.edu.monster.phoneshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PhoneShopServerClient phoneshopBankServerClient;
    private final HttpSession session;

    @Override
    public AuthResponseDto login(LoginRequestDto credentials) {
        String ip = credentials.getServerIp();
        String port = "9001";

        if (ip.contains(":")) {
            String[] parts = ip.split(":");
            ip = parts[0];
            port = parts[1];
        }

        URI uri = URI.create("http://" + ip + ":" + port);
        AuthResponseDto response = phoneshopBankServerClient.login(uri, AuthCredentialsDto.builder()
                .username(credentials.getUsername())
                .password(credentials.getPassword())
                .build());

        if (response.getStatus() == ResponseStatus.SUCCESS) {
            session.setAttribute("EUREKABANK_SERVER_IP", ip);
            session.setAttribute("EUREKABANK_SERVER_PORT", port);
            session.setAttribute("IDENTIFICATION_NUMBER", response.getUser().getIdentificationNumber());
            session.setAttribute("AUTH_TOKEN", response.getToken());
            session.setAttribute("AUTHENTICATED_USER", response.getUser());
        }

        return response;
    }

    @Override
    public void logout() {
        session.removeAttribute("AUTH_TOKEN");
        session.removeAttribute("AUTHENTICATED_USER");
        session.removeAttribute("EUREKABANK_SERVER_IP");
        session.removeAttribute("EUREKABANK_SERVER_PORT");
        session.removeAttribute("IDENTIFICATION_NUMBER");
    }

    @Override
    public UserDto getCurrentUser() {
        String serverIp = session.getAttribute("EUREKABANK_SERVER_IP").toString();
        String port = session.getAttribute("EUREKABANK_SERVER_PORT").toString();
        URI uri = URI.create("http://" + serverIp + ":" + port);
        return phoneshopBankServerClient.getCurrentUser(uri);
    }
}
