package ec.edu.monster.phoneshop.controller;

import ec.edu.monster.phoneshop.dto.*;
import ec.edu.monster.phoneshop.service.AuthService;
import feign.FeignException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@Validated LoginRequestDto credentials, Model model) {
        try {
            AuthResponseDto response = authService.login(credentials);

            if (response.getStatus() == ResponseStatus.SUCCESS) {
                return "redirect:/";
            }

            model.addAttribute("error", response.getMessage());
        } catch (Exception e) {
            if (e instanceof FeignException) {
                if (((FeignException) e).status() == 401) {
                    model.addAttribute("error", "Credenciales inválidas");
                } else {
                    model.addAttribute("error", e.getMessage());
                }
            } else {
                model.addAttribute("error", e.getMessage());
            }
        }

        model.addAttribute("credentials", credentials);

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout();
        return "redirect:/login";
    }
}
