package com.online.store.controller.cart.web;

import com.online.store.service.cart.CartService;
import com.online.store.service.payment.PaymentClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartViewController {

    private final CartService cartService;
    private final PaymentClientService paymentClientService;

    @GetMapping
    public Mono<String> cartPage(WebSession session, Model model) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just("redirect:/login");
        }

        return cartService.getOrCreateCart(userUuid)
                .flatMap(cartService::toDtoWithProducts)
                .flatMap(cart -> paymentClientService.getBalance(userUuid)
                        .map(balance -> {
                            boolean canCheckout = balance.compareTo(cart.getTotalCartPrice()) >= 0;
                            model.addAttribute("cart", cart);
                            model.addAttribute("balance", balance);
                            model.addAttribute("canCheckout", canCheckout);
                            model.addAttribute("paymentServiceAvailable", true);
                            session.getAttributes().put("cartCount", cart.getItems().size());
                            return "cart";
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            model.addAttribute("cart", cart);
                            model.addAttribute("paymentServiceAvailable", false);
                            model.addAttribute("canCheckout", false);
                            session.getAttributes().put("cartCount", cart.getItems().size());
                            return Mono.just("cart");
                        })));
    }

}
