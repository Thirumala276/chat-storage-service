package com.raga.chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@ConfigurationProperties(prefix = "cors")
@Setter
@Getter
public class CorsConfig {

  private List<String> allowedOrigins;

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration());
    return new CorsFilter(source);
  }

  @Bean
  public CorsConfiguration corsConfiguration() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOriginPatterns(allowedOrigins);
    corsConfiguration.addAllowedMethod("*"); // ✅ allow all methods
    corsConfiguration.addAllowedHeader("*"); // ✅ allow all headers
    corsConfiguration.setAllowCredentials(true);
    return corsConfiguration;
  }
}
