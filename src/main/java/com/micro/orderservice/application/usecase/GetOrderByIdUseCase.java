package com.micro.orderservice.application.usecase;

import com.micro.orderservice.application.dto.response.OrderResponse;

public interface GetOrderByIdUseCase {
    OrderResponse execute(Long id);
}