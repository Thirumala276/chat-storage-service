package com.raga.chat.persistence.repository;

import com.raga.chat.persistence.entity.ChatMessage;
import com.raga.chat.persistence.entity.ChatSession;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  Page<ChatMessage> findBySession(ChatSession session, Pageable pageable);

  default List<ChatMessage> findTopKByVector(JdbcTemplate jdbcTemplate, Long sessionId, String vector, int topN) {
    String sql = " SELECT * FROM chat_messages WHERE session_id = ? ORDER BY embedding <-> CAST(? AS vector) LIMIT ?";
    return jdbcTemplate.query(sql, new Object[]{sessionId, vector, topN},
                              (rs, rowNum) -> {
                                var kb = new ChatMessage();
                                kb.setId(rs.getLong("id"));
                                kb.setContent(rs.getString("content"));
                                return kb;
                              });
  }

}
