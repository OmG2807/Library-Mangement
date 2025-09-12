package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for rate limiting
 * Registers the rate limiting interceptor for all API endpoints
 */
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/api/**") // Apply to all API endpoints
                .excludePathPatterns("/actuator/**"); // Exclude actuator endpoints
    }
    
    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        // Configure: 2 requests per minute per IP for testing
        return new RateLimitInterceptor(2, 1);
    }
}
