package com.raga.chat.persistence.repository;

import com.raga.chat.persistence.entity.KnowledgeBase;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {

  default List<KnowledgeBase> findTopNSimilar(JdbcTemplate jdbcTemplate, Float[] vectorArray, int topN) {
    String vectorLiteral = getVectorLiteral(vectorArray);

    // Adjusted threshold
    double similarityThreshold = 0.20;

    String sql = """
        SELECT *, 1 - (embedding <=> %s) AS similarity
        FROM knowledge_base
        WHERE embedding IS NOT NULL
          AND 1 - (embedding <=> %s) >= %f
        ORDER BY similarity DESC
        LIMIT %d
        """.formatted(vectorLiteral, vectorLiteral, similarityThreshold, topN);

    return jdbcTemplate.query(
      sql,
      (rs, rowNum) -> {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setId(rs.getLong("id"));
        kb.setTitle(rs.getString("title"));
        kb.setContent(rs.getString("content"));
        // You can also print the similarity score here for validation
        System.out.println("Result: ID " + rs.getLong("id") + ", Similarity: " + rs.getDouble("similarity"));
        return kb;
      }
    );
  }

  boolean existsByTitle(String title);

  private static String getVectorLiteral(Float[] vectorArray) {
    return "ARRAY[" + Arrays.stream(vectorArray)
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")) +
      "]::vector";
  }
}