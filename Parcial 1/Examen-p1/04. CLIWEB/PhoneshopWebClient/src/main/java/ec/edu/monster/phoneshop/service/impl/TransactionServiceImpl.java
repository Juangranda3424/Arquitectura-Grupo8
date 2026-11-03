package ec.edu.monster.phoneshop.service.impl;

import ec.edu.monster.phoneshop.client.PhoneShopServerClient;
import ec.edu.monster.phoneshop.dto.TransactionDto;
import ec.edu.monster.phoneshop.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final PhoneShopServerClient phoneshopBankServerClient;
    private final HttpSession session;

    @Override
    public void transfer(TransactionDto transaction) throws Exception {
        String serverIp = session.getAttribute("EUREKABANK_SERVER_IP") == null
                ? "localhost"
                : session.getAttribute("EUREKABANK_SERVER_IP").toString();
        URI uri = new URI(String.format("http://%s:9001", serverIp));
        phoneshopBankServerClient.transfer(uri, transaction);
    }
}
