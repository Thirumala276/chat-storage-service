package com.raga.chat.service.impl;

import com.raga.chat.model.LLMResponse;
import com.raga.chat.service.LLMService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMServiceImpl implements LLMService {

  @Value("${gemini.model}")
  private String geminiModel;

  @Value("${gemini.api-key}")
  private String apiKey;

  public String generateResponseWithContext(String question, String context) {
    String prompt = String.format("""
                                    You are a helpful AI assistant. Use the following context to answer the question.
                                    Context:
                                    %s
                                    
                                    Question: %s
                                    Answer:""", context, question);

    return generateText(prompt);
  }

  public String generateText(String prompt) {
    WebClient webClient = WebClient.create("https://generativelanguage.googleapis.com");

    Map<String, Object> requestBody = Map.of("contents", List.of(
                                               Map.of("parts", List.of(Map.of("text", prompt)))
                                             )
    );

    LLMResponse response = webClient.post()
                                    .uri("/v1beta/models/{model}:generateContent", geminiModel)
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .header("X-goog-api-key", apiKey)
                                    .bodyValue(requestBody)
                                    .retrieve()
                                    .bodyToMono(LLMResponse.class)
                                    .block(); // <- block here to get plain object

    return response != null ? response.getText() : "";
  }
}
