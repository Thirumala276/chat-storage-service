package com.raga.chat.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  public static final String X_APP_KEY = "X-App-Key";

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .addSecurityItem(new SecurityRequirement().addList(X_APP_KEY))
      .components(new Components()
                    .addSecuritySchemes(X_APP_KEY,
                                        new SecurityScheme()
                                          .name(X_APP_KEY)
                                          .type(SecurityScheme.Type.APIKEY)
                                          .in(SecurityScheme.In.HEADER)
                                          .description("Mandatory X-App-Key header")));
  }
}

