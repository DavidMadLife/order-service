package com.micro.orderservice.infrastructure.client;


import com.micro.orderservice.application.exception.BadRequestException;
import com.micro.orderservice.application.exception.NotFoundException;
import com.micro.orderservice.infrastructure.client.dto.DeductStockRequest;
import com.micro.orderservice.infrastructure.client.dto.ErrorResponse;
import com.micro.orderservice.infrastructure.client.dto.ProductDto;
import com.micro.orderservice.infrastructure.config.properties.ProductClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient productRestClient;
    private final ProductClientProperties props;
    private final tools.jackson.databind.ObjectMapper objectMapper; // spring boot tự có bean

    public ProductDto getProduct(Long productId) {
        try {
            return productRestClient.get()
                    .uri(props.getPaths().getById(), productId)
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
                    .uri(props.getPaths().getDeduct(), productId)
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

        // Nếu body là JSON dạng { "error": "..." } thì parse ra cho sạch
        try {
            ErrorResponse er = objectMapper.readValue(body, ErrorResponse.class);
            if (er != null && er.getError() != null && !er.getError().isBlank()) {
                return prefix + ": " + er.getError();
            }
        } catch (Exception ignore) {
            // body không phải JSON theo format ErrorResponse => fallback raw
        }

        return prefix + ": " + body;
    }
}