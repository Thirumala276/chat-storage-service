package com.raga.chat.persistence.repository;

import com.raga.chat.persistence.entity.ChatSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

  Optional<List<ChatSession>> findByUserIdOrderByModifiedAtDesc(String userId);
}