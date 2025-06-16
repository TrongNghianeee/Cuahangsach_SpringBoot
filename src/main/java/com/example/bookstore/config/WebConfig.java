package com.example.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded images from /uploads/** URLs (primary)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        // Serve static images from /img/** URLs (fallback)
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
        
        // Serve all static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
