package com.micro.orderservice.infrastructure.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeductStockRequest {
    private Integer quantity;
    private String refType; // ORDER
    private Long refId;     // orderId
    private String note;
}