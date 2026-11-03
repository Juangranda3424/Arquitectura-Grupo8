package ec.edu.monster.phoneshop.service.impl;

import ec.edu.monster.phoneshop.client.PhoneShopServerClient;
import ec.edu.monster.phoneshop.dto.CheckCreditRequestDto;
import ec.edu.monster.phoneshop.dto.CheckCreditResultDto;
import ec.edu.monster.phoneshop.dto.ResponseStatus;
import ec.edu.monster.phoneshop.service.CreditService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
    private final PhoneShopServerClient phoneshopBankServerClient;
    private final HttpSession session;

    @Override
    public CheckCreditResultDto checkCredit(CheckCreditRequestDto request) {
        try {
            String serverIp = session.getAttribute("EUREKABANK_SERVER_IP") == null
                    ? "localhost"
                    : session.getAttribute("EUREKABANK_SERVER_IP").toString();
            String serverPort = session.getAttribute("EUREKABANK_SERVER_PORT") == null
                    ? "9001"
                    : session.getAttribute("EUREKABANK_SERVER_PORT").toString();
            URI uri = new URI(String.format("http://%s:%s", serverIp, serverPort));

            return phoneshopBankServerClient.checkCredit(uri, request);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return CheckCreditResultDto.builder()
                    .message("An error occurred while checking the credit")
                    .build();
        }
    }
}
