package com.pixelbloom.movie_streaming_service.Controller;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    @LoadBalanced
    public RestTemplate RestConfig() {
        System.out.println("RestConfig");
        return new RestTemplate();
    }
}
