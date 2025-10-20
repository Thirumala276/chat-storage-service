package com.raga.chat.service.impl;

import com.raga.chat.persistence.entity.KnowledgeBase;
import com.raga.chat.persistence.repository.KnowledgeBaseRepository;
import com.raga.chat.service.EmbeddingService;
import com.raga.chat.service.KnowledgeBaseService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

  private final KnowledgeBaseRepository kbRepository;

  private final JdbcTemplate jdbcTemplate;

  private final EmbeddingService embeddingService;

  @Override
  public String searchRelevantContext(String question, int topN) {
    Float[] queryEmbedding = embeddingService.getEmbedding(question);
    List<KnowledgeBase> topDocs = kbRepository.findTopNSimilar(jdbcTemplate, queryEmbedding, topN);
    return topDocs.stream().map(KnowledgeBase::getContent).collect(Collectors.joining("\n"));
  }
}
