package com.online.store.controller.cart.web;

import com.online.store.dto.cart.CartDto;
import com.online.store.mapper.cart.CartMapper;
import com.online.store.service.cart.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartViewController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping
    public String cartPage(HttpSession session, Model model) {

        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return "redirect:/login";
        }

        CartDto cart = cartMapper.toDto(cartService.getOrCreateCart(userUuid));
        model.addAttribute("cart", cart);

        session.setAttribute("cartCount", cart.getItems().size());

        return "cart";
    }

}
