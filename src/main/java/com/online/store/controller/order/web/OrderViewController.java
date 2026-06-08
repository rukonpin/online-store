package com.online.store.controller.order.web;

import com.online.store.dto.order.OrderDto;
import com.online.store.mapper.order.OrderMapper;
import com.online.store.service.order.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public String ordersPage(HttpSession session, Model model) {

        UUID userUuid = (UUID) session.getAttribute("user_id");
        if (userUuid == null) {
            return "redirect:/login";
        }

        List<OrderDto> orders = orderService.getAllOrders(userUuid)
                .stream()
                .map(orderMapper::toDto)
                .toList();

        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/{orderUuid}")
    public String orderDetailPage(@PathVariable UUID orderUuid,
                                  HttpSession session,
                                  Model model) {

        UUID userUuid = (UUID) session.getAttribute("user_id");
        if (userUuid == null) {
            return "redirect:/login";
        }

        OrderDto order = orderMapper.toDto(orderService.getOrder(orderUuid, userUuid));
        model.addAttribute("order", order);
        return "order";
    }

}
