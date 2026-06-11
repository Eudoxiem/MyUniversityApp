package com.myuniversity.app.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Order(1)
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final Map<String, Bandwidth> LIMITS = Map.of(
            "auth", Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))),
            "register", Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(1))),
            "export", Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))),
            "upload", Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))),
            "default", Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1)))
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String ip = getClientIp(request);
        String group = resolveGroup(request.getMethod(), request.getRequestURI());
        String key = ip + ":" + group;
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(group));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit atteint - IP: {}, group: {}, path: {}", ip, group, request.getRequestURI());
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Trop de requêtes. Veuillez réessayer.\"}");
        }
    }

    private String resolveGroup(String method, String path) {
        if (path.startsWith("/api/auth/login")) return "auth";
        if (path.startsWith("/api/auth/register")) return "register";
        if (path.startsWith("/api/export")) return "export";
        if (path.startsWith("/api/fichiers/upload")) return "upload";
        return "default";
    }

    private Bucket createBucket(String group) {
        return Bucket.builder()
                .addLimit(LIMITS.getOrDefault(group, LIMITS.get("default")))
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
