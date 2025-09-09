package com.raga.chat.service.impl;

import com.raga.chat.model.LLMResponse;
import com.raga.chat.service.LLMService;
import com.raga.chat.util.PromptTemplate;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMServiceImpl implements LLMService {

  @Value("${gemini.url}")
  private String geminiUrl;

  @Value("${gemini.model}")
  private String geminiModel;

  @Value("${gemini.api-key}")
  private String apiKey;

  private static final int MAX_RETRIES = 3;
  private static final Duration RETRY_BACKOFF = Duration.ofSeconds(2);
  private  WebClient webClient;
  @PostConstruct
  public void init() {
    this.webClient = WebClient.builder()
                              .baseUrl(geminiUrl)
                              .build();
  }

  @Override
  public Flux<String> generateResponseWithContext(String question, String conversationContext, String knowledgeContext) {
    String prompt = PromptTemplate.buildPrompt(conversationContext, knowledgeContext,question);
    return streamGenerateText(prompt);
  }

  public Flux<String> streamGenerateText(String prompt) {

    Map<String, Object> requestBody = Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

    return webClient.post()
                    .uri("/v1beta/models/{model}:streamGenerateContent", geminiModel)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-goog-api-key", apiKey)
                    .bodyValue(requestBody)
                    .accept(MediaType.APPLICATION_NDJSON) // 👈 stream
                    .retrieve()
                    .bodyToFlux(LLMResponse.class)
                    .map(LLMResponse::getText)
                    .filter(text -> text != null && !text.isBlank())
                    // Retry on transient errors
                    .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_BACKOFF)
                                    .filter(throwable -> {
                                      log.warn("Retrying due to error: {}", throwable.getMessage());
                                      return true; // retry for all exceptions or customize
                                    })
                    )
                    // Fallback if all retries fail
                    .onErrorResume(throwable -> {
                      log.error("All retries failed: {}", throwable.getMessage());
                      return Flux.just("Sorry, the LLM service is temporarily unavailable. Please try again later.");
                    });
  }
}
