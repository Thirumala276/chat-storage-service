package com.raga.chat.persistence.repository;

import com.raga.chat.persistence.entity.ChatMessage;
import com.raga.chat.persistence.entity.ChatSession;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  Page<ChatMessage> findBySession(ChatSession session, Pageable pageable);

  @Query(value = """
    SELECT *
    FROM chat_messages
    WHERE session_id = :sessionId
    ORDER BY embedding <-> CAST(:vector AS vector) ASC
    LIMIT 5
""", nativeQuery = true)
  List<ChatMessage> findTopKSimilar(
    @Param("sessionId") Long sessionId,
    @Param("vector") String vector
  );
}
