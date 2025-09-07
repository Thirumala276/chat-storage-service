package com.raga.chat.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "chat_sessions")
@Data
public class ChatSession {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_id_chat_sessions")
  @SequenceGenerator(name = "seq_id_chat_sessions", sequenceName = "seq_id_chat_sessions",
    allocationSize = 1, initialValue = 10001)
  private Long id;

  private String userId;
  private String title;
  private boolean favorite = false;
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();

  @OneToMany(mappedBy = "session",fetch = FetchType.LAZY,  orphanRemoval = true, cascade = CascadeType.ALL)
  private List<ChatMessage> messages;
}
