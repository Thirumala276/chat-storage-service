package com.raga.chat.filter;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  @Value("${rate-limit.capacity:100}")
  private int capacity;

  @Value("${rate-limit.duration-in-minutes:1}")
  private long durationInMinutes;

  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  private Bucket createNewBucket() {
    Bandwidth limit = Bandwidth.simple(capacity, Duration.ofMinutes(durationInMinutes));
    return Bucket.builder().addLimit(limit).build();
  }

  private Bucket resolveBucket(String key) {
    return cache.computeIfAbsent(key, k -> createNewBucket());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String clientIp = extractClientIp(request);
    Bucket bucket = resolveBucket(clientIp);

    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      response.setContentType("application/json");
      response.setStatus(TOO_MANY_REQUESTS.value());
      response.getWriter().write("{\"error\":\"Rate limit exceeded for IP: " + clientIp + "\"}");
    }
  }

  private String extractClientIp(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
