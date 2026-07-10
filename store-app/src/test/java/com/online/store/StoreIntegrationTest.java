package com.online.store;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.order.OrderDto;
import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.exception.payment.InsufficientFundsException;
import com.online.store.model.product.Product;
import com.online.store.repository.product.ProductRepository;
import com.online.store.service.payment.PaymentClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class StoreIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private PaymentClientService paymentClientService;

    @Test
    @DisplayName("Сценарий E2E: Регистрация -> Вход -> Поиск товара -> Корзина -> Оформление заказа")
    void endToEndUserShoppingFlow() {
        String uniqueEmail = "e2e_user_" + UUID.randomUUID() + "@example.com";
        String password = "password";

        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
                .username("E2E Tester")
                .email(uniqueEmail)
                .password(password)
                .confirmPassword(password)
                .build();

        UserLoginDto LoginDto = UserLoginDto.builder()
                .email(uniqueEmail)
                .password(password)
                .build();

        Product targetProduct = productRepository.findAll().blockFirst();
        assertNotNull(targetProduct, "Проверь скрипт инициализации");
        UUID productUuid = targetProduct.getProductUuid();

        webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationDto)
                .exchange()
                .expectStatus().isCreated();

        EntityExchangeResult<Void> loginResult = webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(LoginDto)
                .exchange()
                .expectStatus().isOk()
                .expectCookie().exists("SESSION")
                .expectBody(Void.class)
                .returnResult();

        String sessionCookie = loginResult.getResponseCookies().getFirst("SESSION").getValue();
        assertNotNull(sessionCookie, "Сессионная кука 'SESSION' должна присутствовать после авторизации");

        webTestClient.get()
                .uri("/api/cart")
                .cookie("SESSION", sessionCookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .value(cart -> {
                    assertNotNull(cart);
                    assertTrue(cart.getItems().isEmpty());
                    assertEquals(0, cart.getTotalCartPrice().intValue());
                });

        CartItemDto addItemRequest = CartItemDto.builder()
                .productUuid(productUuid)
                .quantity(2)
                .build();

        webTestClient.post()
                .uri("/api/cart/items")
                .cookie("SESSION", sessionCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addItemRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .value(cart -> {
                    assertNotNull(cart);
                    assertEquals(1, cart.getItems().size());
                    CartItemDto item = cart.getItems().getFirst();
                    assertEquals(productUuid, item.getProductUuid());
                    assertEquals(2, item.getQuantity());
                    assertEquals(targetProduct.getPrice().multiply(BigDecimal.valueOf(2)), item.getTotalPrice());
                });

        when(paymentClientService.chargePayment(any(UUID.class), any(UUID.class), any(BigDecimal.class)))
                .thenReturn(Mono.just(BigDecimal.valueOf(500000)));

        EntityExchangeResult<OrderDto> orderResult = webTestClient.post()
                .uri("/api/orders")
                .cookie("SESSION", sessionCookie)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDto.class)
                .value(order -> {
                    assertNotNull(order);
                    assertNotNull(order.getUuid(), "Заказу должен быть назначен UUID");
                    assertEquals("PENDING", order.getStatus().name());
                    assertEquals(1, order.getItems().size());
                    assertEquals(productUuid, order.getItems().getFirst().getProductUuid());
                    assertEquals(2, order.getItems().getFirst().getQuantity());
                })
                .returnResult();

        OrderDto createdOrder = orderResult.getResponseBody();
        assertNotNull(createdOrder);

        webTestClient.get()
                .uri("/api/cart")
                .cookie("SESSION", sessionCookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .value(cart -> {
                    assertNotNull(cart);
                    assertTrue(cart.getItems().isEmpty(), "После создания заказа корзина пользователя должна очиститься");
                });

        webTestClient.get()
                .uri("/api/orders/{orderUuid}", createdOrder.getUuid())
                .cookie("SESSION", sessionCookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .value(order -> {
                    assertNotNull(order);
                    assertEquals(createdOrder.getUuid(), order.getUuid());
                    assertEquals(1, order.getItems().size());
                });
    }

    @Test
    @DisplayName("Оформление заказа падает, если недостаточно средств на балансе")
    void checkout_WhenInsufficientFunds_ReturnsPaymentRequired() {
        String uniqueEmail = "poor_user_" + UUID.randomUUID() + "@example.com";
        String password = "password";

        webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserRegistrationDto.builder()
                        .username("Poor")
                        .email(uniqueEmail)
                        .password(password)
                        .confirmPassword(password)
                        .build())
                .exchange()
                .expectStatus()
                .isCreated();

        String sessionCookie = webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UserLoginDto
                        .builder()
                        .email(uniqueEmail)
                        .password(password)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Void.class).getResponseCookies().getFirst("SESSION").getValue();

        Product targetProduct = productRepository.findAll().blockFirst();
        webTestClient.post()
                .uri("/api/cart/items")
                .cookie("SESSION", sessionCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CartItemDto.builder()
                        .productUuid(targetProduct.getProductUuid())
                        .quantity(1)
                        .build())
                .exchange().expectStatus().isOk();

        when(paymentClientService.chargePayment(any(UUID.class), any(), any(BigDecimal.class)))
                .thenReturn(Mono.error(new InsufficientFundsException("Недостаточно средств для оплаты заказа")));

        webTestClient.post().uri("/api/orders")
                .cookie("SESSION", sessionCookie)
                .exchange()
                .expectStatus().isEqualTo(402) // PAYMENT_REQUIRED
                .expectBody()
                .jsonPath("$.message").isEqualTo("Недостаточно средств для оплаты заказа");
    }
}
