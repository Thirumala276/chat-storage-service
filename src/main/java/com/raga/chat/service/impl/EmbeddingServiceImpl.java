package com.raga.chat.service.impl;

import com.raga.chat.service.EmbeddingService;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {

  /**
   * Generate embeddings from the text content
   */
  @Override
  public Float[] getEmbedding(String text) {
    // Example: replace this with actual LLM API call
    // For demonstration, return dummy 1536-d vector
    Float[] embedding = new Float[1536];
    for (int i = 0; i < 1536; i++) {
      embedding[i] = (float) Math.random(); // replace with actual embedding
    }
    return embedding;
  }

  @Override
  public String toPgVectorLiteral(Float[] embedding) {
    return Arrays.stream(embedding).map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
  }

}
