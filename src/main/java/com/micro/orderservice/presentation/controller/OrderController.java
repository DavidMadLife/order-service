package com.micro.orderservice.presentation.controller;

import com.micro.orderservice.application.dto.request.CreateOrderRequest;
import com.micro.orderservice.application.dto.response.OrderResponse;
import com.micro.orderservice.application.usecase.CreateOrderUseCase;
import com.micro.orderservice.application.usecase.GetOrderByIdUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody @Valid CreateOrderRequest request) {
        return createOrderUseCase.execute(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return getOrderByIdUseCase.execute(id);
    }
}