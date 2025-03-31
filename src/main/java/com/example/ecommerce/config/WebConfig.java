package com.example.ecommerce.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Az összes endpoint
                .allowedOrigins("http://localhost:4200") // Angular frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Az engedélyezett HTTP metódusok
                .allowCredentials(true)
                .allowedHeaders("*"); // Az engedélyezett fejlécek
    }
}
