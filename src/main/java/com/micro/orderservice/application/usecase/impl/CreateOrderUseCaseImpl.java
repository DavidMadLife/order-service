package com.micro.orderservice.application.usecase.impl;

import com.micro.orderservice.application.dto.request.CreateOrderRequest;
import com.micro.orderservice.application.dto.response.OrderResponse;
import com.micro.orderservice.application.exception.BadRequestException;
import com.micro.orderservice.application.usecase.CreateOrderUseCase;
import com.micro.orderservice.domain.model.Order;
import com.micro.orderservice.domain.model.OrderItem;
import com.micro.orderservice.domain.repository.OrderRepository;
import com.micro.orderservice.infrastructure.client.ProductClient;
import com.micro.orderservice.infrastructure.client.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Override
    @Transactional
    public OrderResponse execute(CreateOrderRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Items is required");
        }

        // 1) build order skeleton
        Order order = Order.builder()
                .userId(request.getUserId())
                .status("CREATED")
                .currency("VND")
                .totalAmount(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        // 2) load product snapshot + calculate total
        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.Item it : request.getItems()) {
            ProductDto p = productClient.getProduct(it.getProductId());

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .productId(p.getId())
                    .sku(p.getSku())
                    .productName(p.getName())
                    .unitPrice(p.getPrice())
                    .quantity(it.getQuantity())
                    .build();

            order.getItems().add(item);

            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())));
        }

        order.setTotalAmount(total);

        // 3) save order + items
        order = orderRepository.save(order);

        // 4) deduct stock in product-service (sync call v1)
        for (OrderItem item : order.getItems()) {
            productClient.deductStock(item.getProductId(), item.getQuantity(), order.getId());
        }

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