package com.raga.chat.service.impl;

import com.raga.chat.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {

  private static final int VECTOR_SIZE = 1536; // pgvector column size

  @Override
  public Float[] getEmbedding(String text) {
    Float[] vector = new Float[VECTOR_SIZE];

    // Simple deterministic embedding: hash each character
    int hash = text.hashCode();
    for (int i = 0; i < VECTOR_SIZE; i++) {
      vector[i] = ((hash >> (i % 32)) & 0xFF) / 255.0f; // normalize to 0-1
    }
    return vector;
  }
}
