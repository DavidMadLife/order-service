package com.micro.orderservice.infrastructure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.clients.product")
public class ProductClientProperties {

    private String baseUrl;
    private Paths paths = new Paths();

    @Data
    public static class Paths {
        private String byId;
        private String deduct;
    }
}