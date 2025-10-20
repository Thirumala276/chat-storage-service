package com.raga.chat.service;

public interface EmbeddingService {

  Float[] getEmbedding(String text);
}
