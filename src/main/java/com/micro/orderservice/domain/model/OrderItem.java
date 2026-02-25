package com.micro.orderservice.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "order_items", schema = "sales")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK nội bộ: order_items.order_id -> orders.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private Order order;

    @Column(name="product_id", nullable = false)
    private Long productId; // NO FK cross DB

    @Column(length = 64)
    private String sku; // snapshot (optional)

    @Column(name="product_name", length = 200)
    private String productName; // snapshot (optional)

    @Column(name="unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;
}