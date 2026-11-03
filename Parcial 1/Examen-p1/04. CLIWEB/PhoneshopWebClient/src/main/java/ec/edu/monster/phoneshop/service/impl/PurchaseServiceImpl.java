package ec.edu.monster.phoneshop.service.impl;

import ec.edu.monster.phoneshop.client.PhoneShopServerClient;
import ec.edu.monster.phoneshop.dto.ProductDto;
import ec.edu.monster.phoneshop.dto.PurchaseRequestDto;
import ec.edu.monster.phoneshop.dto.PurchaseResultDto;
import ec.edu.monster.phoneshop.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final PhoneShopServerClient phoneshopBankServerClient;
    private final HttpSession session;

    @Override
    public PurchaseResultDto purchase(PurchaseRequestDto request) {
        try {
            String serverIp = session.getAttribute("EUREKABANK_SERVER_IP") == null
                    ? "localhost"
                    : session.getAttribute("EUREKABANK_SERVER_IP").toString();
            String serverPort = session.getAttribute("EUREKABANK_SERVER_PORT") == null
                    ? "9001"
                    : session.getAttribute("EUREKABANK_SERVER_PORT").toString();
            URI uri = new URI(String.format("http://%s:%s", serverIp, serverPort));

            return phoneshopBankServerClient.purchase(uri, request);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new PurchaseResultDto();
        }
    }

    @Override
    public ProductDto updateProduct(ProductDto product) {
        try {
            String serverIp = session.getAttribute("EUREKABANK_SERVER_IP") == null
                    ? "localhost"
                    : session.getAttribute("EUREKABANK_SERVER_IP").toString();
            String serverPort = session.getAttribute("EUREKABANK_SERVER_PORT") == null
                    ? "9001"
                    : session.getAttribute("EUREKABANK_SERVER_PORT").toString();
            URI uri = new URI(String.format("http://%s:%s", serverIp, serverPort));

            return phoneshopBankServerClient.updatePhone(uri, product);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new ProductDto();
        }
    }
}
