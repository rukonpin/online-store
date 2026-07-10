package com.online.store.controller.order.web;

import com.online.store.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;

    @GetMapping
    public Mono<String> ordersPage(WebSession session, Model model) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just("redirect:/login");
        }

        return orderService.getAllOrders(userUuid)
                .flatMap(orderService::toDtoWithProducts)
                       .collectList()
                       .doOnNext(orders -> model.addAttribute("orders", orders))
                       .map(orders -> "orders");
    }

    @GetMapping("/{orderUuid}")
    public Mono<String> orderDetailPage(@PathVariable UUID orderUuid,
                                  WebSession session,
                                  Model model) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just("redirect:/login");
        }

        return orderService.getOrder(orderUuid, userUuid)
                .flatMap(orderService::toDtoWithProducts)
                .doOnNext(order -> model.addAttribute("order", order))
                .map(page -> "order");
    }

}
