package ec.edu.monster.phoneshop.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignClientInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER="Authorization";
    private static final String TOKEN_TYPE = "Bearer";
    private final HttpSession httpSession;

    @Override
    public void apply(RequestTemplate template) {
        if (httpSession.getAttribute("AUTH_TOKEN") != null) {
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, httpSession.getAttribute("AUTH_TOKEN")));
        }
    }
}
