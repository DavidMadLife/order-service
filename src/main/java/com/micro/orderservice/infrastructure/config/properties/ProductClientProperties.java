package com.micro.orderservice.infrastructure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.clients.product")
public class ProductClientProperties {
    private String baseUrl;
}