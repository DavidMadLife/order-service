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
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class ProductClient {

    // ✅ constants: thêm endpoint mới chỉ việc thêm constant
    private static final String GET_BY_ID = "/api/products/{id}";
    private static final String DEDUCT    = "/api/products/{id}/deduct";

    private final RestClient productRestClient;
    private final ObjectMapper objectMapper;

    public ProductDto getProduct(Long productId) {
        try {
            return productRestClient.get()
                    .uri(GET_BY_ID, productId)
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
                    .uri(DEDUCT, productId)
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
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return prefix + ": " + ex.getStatusText();
        }

        try {
            ErrorResponse er = objectMapper.readValue(body, ErrorResponse.class);
            if (er != null && er.getError() != null && !er.getError().isBlank()) {
                return prefix + ": " + er.getError();
            }
        } catch (Exception ignore) {}

        return prefix + ": " + body;
    }
}