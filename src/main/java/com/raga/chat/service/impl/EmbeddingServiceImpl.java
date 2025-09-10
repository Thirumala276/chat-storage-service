package com.raga.chat.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raga.chat.service.EmbeddingService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {

  private final WebClient webClient = WebClient.builder().build();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${cohere.api-key}")
  private String apiKey;

  @Value("${cohere.url}")
  private String apiUrl;

  @Value("${cohere.model}")
  private String model;

  public Float[] getEmbedding(String text) {
    // Build correct request body
    Map<String, Object> request = new HashMap<>();
    request.put("texts", List.of(text));
    request.put("model", "embed-english-v3.0");
    request.put("input_type", "search_document");

    try {
      String response = webClient.post()
                                 .uri(apiUrl)
                                 .header("Authorization", "Bearer " + apiKey)
                                 .header("Content-Type", "application/json")
                                 .bodyValue(request)
                                 .retrieve()
                                 .bodyToMono(String.class)
                                 .block();

      JsonNode root = objectMapper.readTree(response);
      JsonNode vector = root.get("embeddings").get(0);

      Float[] arr = new Float[vector.size()];
      for (int i = 0; i < vector.size(); i++) {
        arr[i] = (float) vector.get(i).asDouble();
      }
      return arr;

    } catch (Exception e) {
      log.error("Failed to get embedding from Cohere", e);
      throw new RuntimeException(e);
    }
  }
}
