package com.raga.chat.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;

@Entity
@Table(name = "chat_messages")
@Data
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_id_chat_messages")
  @SequenceGenerator(name = "seq_id_chat_messages", sequenceName = "seq_id_chat_messages",
    allocationSize = 1, initialValue = 10001)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "session_id")
  private ChatSession session;

  private String sender;

  @Column(columnDefinition = "TEXT")
  private String content;

  private Instant createdAt = Instant.now();

  @Column(columnDefinition = "vector(1536)")
  private Float[] embedding;

  @Column(columnDefinition = "TEXT")
  private String retrievedContext;
}
