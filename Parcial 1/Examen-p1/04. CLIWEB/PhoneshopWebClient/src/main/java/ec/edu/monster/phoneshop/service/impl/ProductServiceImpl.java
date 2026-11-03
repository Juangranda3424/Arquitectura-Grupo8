package ec.edu.monster.phoneshop.service.impl;

import ec.edu.monster.phoneshop.client.PhoneShopServerClient;
import ec.edu.monster.phoneshop.dto.ProductDto;
import ec.edu.monster.phoneshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final PhoneShopServerClient phoneshopBankServerClient;
    private final HttpSession session;

    @Override
    public List<ProductDto> getProducts() {
        try {
            String serverIp = session.getAttribute("EUREKABANK_SERVER_IP") == null
                    ? "localhost"
                    : session.getAttribute("EUREKABANK_SERVER_IP").toString();
            String serverPort = session.getAttribute("EUREKABANK_SERVER_PORT") == null
                    ? "9001"
                    : session.getAttribute("EUREKABANK_SERVER_PORT").toString();
            URI uri = new URI(String.format("http://%s:%s", serverIp, serverPort));

            return phoneshopBankServerClient.getPhones(uri);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return List.of();
        }
    }
}
