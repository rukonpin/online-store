package com.online.store.controller.order.api;

import com.online.store.dto.order.OrderDto;
import com.online.store.dto.order.OrderItemDto;
import com.online.store.exception.order.OrderNotFoundException;
import com.online.store.mapper.order.OrderMapper;
import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import com.online.store.model.order.OrderStatus;
import com.online.store.model.product.Product;
import com.online.store.model.user.User;
import com.online.store.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    private UUID userUuid;
    private UUID orderUuid;
    private UUID productUuid;
    private User userMock;
    private Product productMock;
    private OrderItem orderItem;
    private List<OrderItem> existingOrderItems;
    private Order mockOrder;
    private OrderItemDto itemResponseDto;
    private OrderDto mockOrderDto;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        orderUuid = UUID.randomUUID();
        productUuid = UUID.randomUUID();

        userMock = User.builder().uuid(userUuid).build();

        productMock = Product.builder()
                .uuid(productUuid)
                .price(BigDecimal.valueOf(100))
                .build();

        orderItem = OrderItem.builder()
                .uuid(UUID.randomUUID())
                .product(productMock)
                .priceAtPurchase(BigDecimal.valueOf(100))
                .quantity(1)
                .build();

        existingOrderItems = new ArrayList<>();
        existingOrderItems.add(orderItem);

        mockOrder = Order.builder()
                .uuid(orderUuid)
                .user(userMock)
                .items(existingOrderItems)
                .status(OrderStatus.PENDING)
                .build();

        itemResponseDto = OrderItemDto.builder()
                .uuid(mockOrder.getItems().getFirst().getUuid())
                .productUuid(productUuid)
                .priceAtPurchase(BigDecimal.valueOf(100))
                .quantity(1)
                .build();

        mockOrderDto = OrderDto.builder()
                .uuid(orderUuid)
                .userUuid(userUuid)
                .items(List.of(itemResponseDto))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("POST /api/orders - should return order when user purchased on order")
    void createOrder_WhenUserPurchased_ReturnsOrder() throws Exception{

        when(orderService.createOrder(userUuid))
                .thenReturn(mockOrder);
        when(orderMapper.toDto(mockOrder))
                .thenReturn(mockOrderDto);

        mockMvc.perform(post("/api/orders")
                    .sessionAttr("user_id", userUuid)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(orderUuid.toString()))
                .andExpect(jsonPath("$.userUuid").value(userUuid.toString()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productUuid").value(productUuid.toString()))
                .andExpect(jsonPath("$.items[0].priceAtPurchase").value(100));
    }

    @Test
    @DisplayName("POST /api/orders - should return 401 Unauthorized when user is not authenticated")
    void createOrder_WhenNotAuthenticated_ReturnsUnauthorized() throws Exception {

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/orders/{orderUuid} - should return order uuid when order exists")
    void getOrder_WhenOrderExists_ReturnsOrder() throws Exception{

        when(orderService.getOrder(orderUuid, userUuid))
                .thenReturn(mockOrder);
        when(orderMapper.toDto(mockOrder))
                .thenReturn(mockOrderDto);

        mockMvc.perform(get("/api/orders/{orderUuid}", orderUuid.toString())
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(orderUuid.toString()))
                .andExpect(jsonPath("$.userUuid").value(userUuid.toString()))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/orders/{orderUuid} - should return 401 Unauthorized when user is not authenticated")
    void getOrder_WhenNotAuthenticated_ReturnsUnauthorized() throws Exception {

        mockMvc.perform(get("/api/orders/{orderUuid}", orderUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/orders/{orderUuid} - should throw OrderNotFoundException when order not found")
    void getOrder_WhenOrderDoesNotExist_ReturnsNotFound() throws Exception {

        when(orderService.getOrder(orderUuid, userUuid))
                .thenThrow(new OrderNotFoundException(orderUuid));

        mockMvc.perform(get("/api/orders/{orderUuid}", orderUuid.toString())
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Order with this " + orderUuid + " not found"));
    }
}