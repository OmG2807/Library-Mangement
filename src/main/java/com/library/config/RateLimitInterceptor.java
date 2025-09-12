package com.library.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting interceptor using in-memory storage
 * Limits requests per IP address to prevent abuse
 */
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final int maxRequests;
    private final int windowSizeInMinutes;
    private final ConcurrentHashMap<String, RequestWindow> requestCounts = new ConcurrentHashMap<>();

    public RateLimitInterceptor() {
        this.maxRequests = 100; // Default: 100 requests per window
        this.windowSizeInMinutes = 1; // Default: 1 minute window
    }

    public RateLimitInterceptor(int maxRequests, int windowSizeInMinutes) {
        this.maxRequests = maxRequests;
        this.windowSizeInMinutes = windowSizeInMinutes;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = getClientIpAddress(request);
        String endpoint = request.getRequestURI();
        
        // Skip rate limiting for actuator endpoints
        if (endpoint.startsWith("/actuator")) {
            return true;
        }

        RequestWindow window = requestCounts.computeIfAbsent(clientIp, k -> new RequestWindow());
        
        // Clean up old windows periodically
        if (window.isExpired(windowSizeInMinutes)) {
            requestCounts.remove(clientIp);
            window = new RequestWindow();
            requestCounts.put(clientIp, window);
        }

        int currentCount = window.incrementAndGet();
        
        log.debug("Rate limit check - IP: {}, Endpoint: {}, Count: {}/{}", 
                 clientIp, endpoint, currentCount, maxRequests);

        if (currentCount > maxRequests) {
            log.warn("Rate limit exceeded for IP: {} on endpoint: {} ({} requests in {} minutes)", 
                    clientIp, endpoint, currentCount, windowSizeInMinutes);
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(window.getResetTime()));
            
            try {
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
                response.setContentType("application/json");
            } catch (Exception e) {
                log.error("Error writing rate limit response", e);
            }
            
            return false;
        }

        // Set rate limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(maxRequests - currentCount));
        response.setHeader("X-RateLimit-Reset", String.valueOf(window.getResetTime()));

        return true;
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

    /**
     * Inner class to track request counts within a time window
     */
    private static class RequestWindow {
        private final AtomicInteger count = new AtomicInteger(0);
        private final LocalDateTime startTime = LocalDateTime.now();

        public int incrementAndGet() {
            return count.incrementAndGet();
        }

        public boolean isExpired(int windowSizeInMinutes) {
            return startTime.plusMinutes(windowSizeInMinutes).isBefore(LocalDateTime.now());
        }

        public long getResetTime() {
            return startTime.plusMinutes(1).atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
        }
    }
}
