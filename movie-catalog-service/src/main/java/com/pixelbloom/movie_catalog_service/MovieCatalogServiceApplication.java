package com.pixelbloom.movie_catalog_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@ComponentScan(basePackages = "com.pixelbloom.movie_catalog_service")
@EnableJpaRepositories(basePackages = "com.pixelbloom.movie_catalog_service.Repository")
@SpringBootApplication()
public class MovieCatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieCatalogServiceApplication.class, args);
       // SpringApplication app = new SpringApplication(MovieCatalogServiceApplication.class);
     //   app.setAdditionalProfiles("database");
       // app.run(args);
//SPRING_PROFILES_ACTIVE=database
    }

}
