package com.pixelbloom.movie_streaming_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.pixelbloom.movie_streaming_service")
public class MovieStreamingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieStreamingServiceApplication.class, args);
	}

}
