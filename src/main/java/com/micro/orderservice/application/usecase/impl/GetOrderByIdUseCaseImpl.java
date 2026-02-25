package com.micro.orderservice.application.usecase.impl;

import com.micro.orderservice.application.dto.response.OrderResponse;
import com.micro.orderservice.application.exception.NotFoundException;
import com.micro.orderservice.application.usecase.GetOrderByIdUseCase;
import com.micro.orderservice.domain.model.Order;
import com.micro.orderservice.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetOrderByIdUseCaseImpl implements GetOrderByIdUseCase {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse execute(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .currency(order.getCurrency())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(oi -> OrderResponse.OrderItemResponse.builder()
                                .id(oi.getId())
                                .productId(oi.getProductId())
                                .sku(oi.getSku())
                                .productName(oi.getProductName())
                                .unitPrice(oi.getUnitPrice())
                                .quantity(oi.getQuantity())
                                .build())
                        .toList())
                .build();
    }
}