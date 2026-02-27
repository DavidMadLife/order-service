package com.micro.orderservice.infrastructure.client;

import com.micro.orderservice.application.exception.BadRequestException;
import com.micro.orderservice.application.exception.NotFoundException;
import com.micro.orderservice.infrastructure.client.dto.DeductStockRequest;
import com.micro.orderservice.infrastructure.client.dto.ErrorResponse;
import com.micro.orderservice.infrastructure.client.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient productRestClient;

    public ProductDto getProduct(Long productId) {
        try {
            return productRestClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductDto.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("Product not found: " + productId);
            }
            throw new BadRequestException(buildError("Get product failed", ex));
        }
    }

    public void deductStock(Long productId, int qty, Long orderId) {
        DeductStockRequest req = DeductStockRequest.builder()
                .quantity(qty)
                .refType("ORDER")
                .refId(orderId)
                .note("Deduct for order")
                .build();

        try {
            productRestClient.post()
                    .uri("/api/products/{id}/deduct", productId)
                    .body(req)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("Product not found: " + productId);
            }
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new BadRequestException(buildError("Deduct stock failed", ex));
            }
            throw new BadRequestException(buildError("Product service error", ex));
        }
    }

    private String buildError(String prefix, RestClientResponseException ex) {
        // try parse { "error": "..." }
        try {
            ErrorResponse er = productRestClient
                    .get() // dummy to access message converters? (không cần)
                    .retrieve()
                    .body(ErrorResponse.class);
        } catch (Exception ignore) {
            // ignore
        }

        // Cách đơn giản & ổn định: lấy raw response body
        String body = ex.getResponseBodyAsString();
        if (body != null && !body.isBlank()) {
            return prefix + ": " + body;
        }
        return prefix + ": " + ex.getStatusText();
    }
}