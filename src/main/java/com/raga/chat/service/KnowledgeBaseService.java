package com.raga.chat.service;

public interface KnowledgeBaseService {

  String searchRelevantContext(String question, int topN);
}


