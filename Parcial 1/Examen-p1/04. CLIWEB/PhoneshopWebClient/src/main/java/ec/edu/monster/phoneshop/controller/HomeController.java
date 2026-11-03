package ec.edu.monster.phoneshop.controller;

import ec.edu.monster.phoneshop.dto.*;
import ec.edu.monster.phoneshop.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Scope("session")
public class HomeController {
    private final AuthService authService;
    private final MovementService movementService;
    private final TransactionService transactionService;
    private final PurchaseService purchaseService;
    private final ProductService productService;
    private final CreditService creditService;

    @GetMapping("/")
    public String home(Model model) {
        List<ProductDto> products = productService.getProducts();
        model.addAttribute("products", products);
        return "index";
    }

    @PostMapping("/transaction")
    public String transaction(TransactionDto transaction, RedirectAttributes redirectAttributes) {
        try {
            transactionService.transfer(transaction);
            redirectAttributes.addFlashAttribute("fsuccess", "Transacción realizada con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("ferror", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/checkCredit")
    public String checkCredit(CheckCreditRequestDto checkCredit, RedirectAttributes redirectAttributes) {
        try {
            CheckCreditResultDto result = creditService.checkCredit(checkCredit);
            redirectAttributes.addFlashAttribute("creditMessage", result.getMessage());
            redirectAttributes.addFlashAttribute("creditMaxAmount", result.getMaxAmount());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("ferror", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/purchase")
    public String purchase(PurchaseRequestDto purchase, RedirectAttributes redirectAttributes) {
        try {
            PurchaseResultDto result = purchaseService.purchase(purchase);

            if (result.getStatus() == ResponseStatus.ERROR) {
                redirectAttributes.addFlashAttribute("ferror", result.getMessage().isBlank() ? "Error al realizar la compra" : result.getMessage());
                return "redirect:/";
            }

            redirectAttributes.addFlashAttribute("fsuccess", "Compra realizada con éxito");
            redirectAttributes.addFlashAttribute("purchase", purchase);
            redirectAttributes.addFlashAttribute("installments", purchase.getMethod() == PurchaseMethod.CREDIT ? result.getCredit().getInstallments() : new ArrayList<>());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("ferror", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/updatePhone")
    public String updatePhone(ProductDto phone, RedirectAttributes redirectAttributes) {
        try {
            purchaseService.updateProduct(phone);
            redirectAttributes.addFlashAttribute("fsuccess", "Teléfono actualizado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("ferror", e.getMessage());
        }

        return "redirect:/";
    }
}
