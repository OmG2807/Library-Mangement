package com.library.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

/**
 * Interceptor for request logging
 */
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(START_TIME_ATTRIBUTE, Instant.now());
        
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        log.info("Request started - Method: {}, URI: {}, IP: {}, User-Agent: {}", 
                request.getMethod(), request.getRequestURI(), clientIp, userAgent);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Instant startTime = (Instant) request.getAttribute(START_TIME_ATTRIBUTE);
        Duration duration = Duration.between(startTime, Instant.now());
        
        String clientIp = getClientIpAddress(request);
        
        if (ex != null) {
            log.error("Request completed with error - Method: {}, URI: {}, IP: {}, Status: {}, Duration: {}ms, Error: {}", 
                    request.getMethod(), request.getRequestURI(), clientIp, response.getStatus(), 
                    duration.toMillis(), ex.getMessage());
        } else {
            log.info("Request completed - Method: {}, URI: {}, IP: {}, Status: {}, Duration: {}ms", 
                    request.getMethod(), request.getRequestURI(), clientIp, response.getStatus(), 
                    duration.toMillis());
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
