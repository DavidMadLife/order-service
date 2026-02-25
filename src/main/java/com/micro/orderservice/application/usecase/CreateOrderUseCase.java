package com.micro.orderservice.application.usecase;

import com.micro.orderservice.application.dto.request.CreateOrderRequest;
import com.micro.orderservice.application.dto.response.OrderResponse;

public interface CreateOrderUseCase {
    OrderResponse execute(CreateOrderRequest request);
}