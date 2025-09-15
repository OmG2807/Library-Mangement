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
                .addPathPatterns("/**") // Apply to all paths
                .excludePathPatterns("/actuator/**", "/h2-console/**"); // Exclude actuator and H2 console
    }
    
    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        // Configure: 2 requests per minute per IP for testing
        return new RateLimitInterceptor(2, 1);
    }
}
