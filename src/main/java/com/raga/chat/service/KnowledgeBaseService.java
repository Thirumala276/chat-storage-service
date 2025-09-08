package com.raga.chat.service;

public interface KnowledgeBaseService {

  String searchRelevantContext(String query, int topN);
}


