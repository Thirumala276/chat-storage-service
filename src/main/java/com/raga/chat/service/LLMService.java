package com.raga.chat.service;

import reactor.core.publisher.Flux;

public interface LLMService {

  Flux<String> generateResponseWithContext(String question, String context, String knowledgeContext);
}
