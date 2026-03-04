package com.micro.orderservice.presentation.controller;


import com.micro.orderservice.infrastructure.client.ProductClient;
import com.micro.orderservice.infrastructure.client.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductClient productClient;

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id) {
        return productClient.getProduct(id);
    }

    @PostMapping("/{id}/deduct")
    public void deduct(
            @PathVariable Long id,
            @RequestParam int qty,
            @RequestParam Long orderId
    ) {
        productClient.deductStock(id, qty, orderId);
    }
}