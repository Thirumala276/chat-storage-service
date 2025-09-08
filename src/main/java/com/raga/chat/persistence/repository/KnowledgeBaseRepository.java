package com.raga.chat.persistence.repository;

import com.raga.chat.persistence.entity.KnowledgeBase;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {

  default List<KnowledgeBase> findTopNSimilar(JdbcTemplate jdbcTemplate, String vector, int topN) {
    String sql = "SELECT * FROM knowledge_base  WHERE embedding IS NOT NULL ORDER BY embedding <-> CAST(? AS vector) LIMIT ? ";
    return jdbcTemplate.query(sql, new Object[]{vector, topN},
                              (rs, rowNum) -> {
                                var kb = new KnowledgeBase();
                                kb.setId(rs.getLong("id"));
                                kb.setTitle(rs.getString("title"));
                                kb.setContent(rs.getString("content"));
                                return kb;
                              });
  }

  boolean existsByTitle(String title);
}

