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

  private final EmbeddingService embeddingService;

  private final JdbcTemplate jdbcTemplate;

  @Override
  public String searchRelevantContext(String query, int topN) {
    Float[] queryEmbedding = embeddingService.getEmbedding(query);
    String vectorLiteral = embeddingService.toPgVectorLiteral(queryEmbedding);
    List<KnowledgeBase> topDocs = kbRepository.findTopNSimilar(jdbcTemplate, vectorLiteral, topN);
    return topDocs.stream()
                  .map(KnowledgeBase::getContent)
                  .collect(Collectors.joining("\n"));
  }
}
