package com.raga.chat.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "knowledge_base")
@Data
public class KnowledgeBase extends AuditableEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_id_knowledge_base")
  @SequenceGenerator(name = "seq_id_knowledge_base", sequenceName = "seq_id_knowledge_base",
    allocationSize = 1, initialValue = 10001)
  private Long id;

  private String title;

  @Column(length = 5000)
  private String content;

  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(columnDefinition = "vector(1024)")
  private Float[] embedding;
}