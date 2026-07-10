package com.online.store.config;

import com.online.store.payment.client.api.PaymentApi;
import com.online.store.payment.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PaymentClientConfig {

    @Value("${payment-service.base-url}")
    private String BaseUrl;

    @Bean
    public PaymentApi paymentApi() {
        WebClient webClient = WebClient.builder().build();
        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(BaseUrl);
        return new PaymentApi(apiClient);
    }
}
