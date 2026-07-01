package com.online.store.controller.cart.web;

import com.online.store.service.cart.CartService;
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

    @GetMapping
    public Mono<String> cartPage(WebSession session, Model model) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just("redirect:/login");
        }

        return cartService.getOrCreateCart(userUuid)
                .flatMap(cartService::toDtoWithProducts)
                .doOnNext(cart -> {
                    model.addAttribute("cart", cart);
                    session.getAttributes().put("cartCount", cart.getItems().size());
                })
                .map(page -> "cart");
    }

}
