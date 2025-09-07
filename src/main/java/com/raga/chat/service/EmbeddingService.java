package com.raga.chat.service;

public interface EmbeddingService {
  /**
   * Convert text into a vector embedding suitable for pgvector.
   */
  Float[] getEmbedding(String text);
}
