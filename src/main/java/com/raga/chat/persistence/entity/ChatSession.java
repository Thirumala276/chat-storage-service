package com.raga.chat.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "chat_sessions")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatSession extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_id_chat_sessions")
  @SequenceGenerator(name = "seq_id_chat_sessions", sequenceName = "seq_id_chat_sessions",
    allocationSize = 1, initialValue = 10001)
  private Long id;

  private String userId;
  private String title;
  private boolean favorite = false;

  @OneToMany(mappedBy = "session", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
  private List<ChatMessage> messages;
}
