package com.micro.orderservice.infrastructure.config.http;

import com.micro.orderservice.infrastructure.config.properties.ProductClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientsConfig {

    @Bean
    public RestClient productRestClient(ProductClientProperties props) {
        var rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(2000); // 2s
        rf.setReadTimeout(5000);    // 5s

        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .requestFactory(rf)
                .build();
    }
}