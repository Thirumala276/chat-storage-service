package com.raga.chat.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  @Value("${security.api-key}")
  private String apiKey;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
    String key = request.getHeader("X-API-KEY");
    if (key == null || !key.equals(apiKey)) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"error\":\"Missing or invalid API key\"}");
      return;
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    // Exclude Swagger UI, OpenAPI docs, and Actuator endpoints
    return path.startsWith("/chat/swagger-ui")
      || path.startsWith("/chat/v3/api-docs")
      || path.startsWith("/chat/actuator")
      || path.startsWith("/swagger-resources")
      || path.startsWith("/webjars")
      || path.equals("/chat/swagger-ui.html");
  }
}
