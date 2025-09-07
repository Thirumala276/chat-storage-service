package com.raga.chat.service;

public interface LLMService {

  String generateResponseWithContext(String question, String context);
}
