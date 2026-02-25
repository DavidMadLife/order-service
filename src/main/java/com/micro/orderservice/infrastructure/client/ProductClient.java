package com.micro.orderservice.infrastructure.client;

import com.micro.orderservice.application.exception.BadRequestException;
import com.micro.orderservice.application.exception.NotFoundException;
import com.micro.orderservice.infrastructure.client.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.product.base-url}")
    private String baseUrl;

    public ProductDto getProduct(Long productId) {
        try {
            return restTemplate.getForObject(baseUrl + "/api/products/" + productId, ProductDto.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("Product not found: " + productId);
        }
    }

    public void deductStock(Long productId, int qty, Long orderIdHint) {
        // gọi endpoint bạn đã làm: POST /api/products/{id}/deduct
        String url = baseUrl + "/api/products/" + productId + "/deduct";

        String body = """
                {"quantity": %d, "refType": "ORDER", "refId": %d, "note": "Deduct for order"}
                """.formatted(qty, orderIdHint == null ? 0 : orderIdHint);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("Product not found: " + productId);
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new BadRequestException("Deduct stock failed: " + ex.getResponseBodyAsString());
        }
    }
}