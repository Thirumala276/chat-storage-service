package com.raga.chat.service.impl;

import com.raga.chat.exception.ResourceNotFoundException;
import com.raga.chat.model.AddMessageRequest;
import com.raga.chat.model.CreateSessionRequest;
import com.raga.chat.model.FavoriteRequest;
import com.raga.chat.model.RenameRequest;
import com.raga.chat.persistence.entity.ChatMessage;
import com.raga.chat.persistence.entity.ChatSession;
import com.raga.chat.persistence.repository.ChatMessageRepository;
import com.raga.chat.persistence.repository.ChatSessionRepository;
import com.raga.chat.service.ChatSessionService;
import com.raga.chat.service.LLMService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

  private static final int VECTOR_SIZE = 1536;
  private static final String SESSION_NOT_FOUND = "Session not found";

  private final LLMService llmService;
  private final ChatSessionRepository chatSessionRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public ChatSession createSession(CreateSessionRequest request) {
    ChatSession s = new ChatSession();
    s.setUserId(request.userId());
    s.setTitle(request.title());
    return chatSessionRepository.save(s);
  }

  @Override
  public List<ChatSession> getSessionsByUserId(String userId) {
    return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                                .orElse(List.of());
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ChatMessage> getMessages(Long sessionId, int page, int size) {
    ChatSession session = chatSessionRepository.findById(sessionId)
                                               .orElseThrow(() -> new ResourceNotFoundException(
                                                 SESSION_NOT_FOUND));
    return chatMessageRepository.findBySession(session, PageRequest.of(page, size));
  }

  @Transactional
  @Override
  public ChatMessage addMessage(Long sessionId, AddMessageRequest request) {
    // 1️⃣ Verify session exists
    ChatSession session = chatSessionRepository.findById(sessionId)
                                               .orElseThrow(() -> new ResourceNotFoundException(SESSION_NOT_FOUND));
    String content = request.content();
    // 2️⃣ Generate embedding for user message
    Float[] userEmbedding = getEmbedding(content);

    // 3️⃣ Retrieve context from previous messages
    String context = retrieveContext(content, sessionId);
    context = ObjectUtils.isEmpty(context) ? content : context;

    // 4️⃣ Save user message
    ChatMessage userMessage = new ChatMessage();
    userMessage.setSession(session);
    userMessage.setSender(request.sender());
    userMessage.setContent(content);
    userMessage.setEmbedding(userEmbedding);
    chatMessageRepository.save(userMessage);

    // 5️⃣ Generate AI response
    String aiContent = llmService.generateResponseWithContext(content, context);
    Float[] aiEmbedding = getEmbedding(aiContent); // optional: use different embedding

    // 6️⃣ Save AI message
    ChatMessage aiMessage = new ChatMessage();
    aiMessage.setSession(session);
    aiMessage.setSender("AI");
    aiMessage.setContent(aiContent);
    aiMessage.setRetrievedContext(context);
    aiMessage.setEmbedding(aiEmbedding);
    chatMessageRepository.save(userMessage);
    return aiMessage;
  }


  @Override
  public ChatSession renameSession(Long sessionId, RenameRequest request) {
    ChatSession session = chatSessionRepository.findById(sessionId)
                                               .orElseThrow(() -> new ResourceNotFoundException(
                                                 SESSION_NOT_FOUND));
    session.setTitle(request.title());
    return chatSessionRepository.save(session);
  }


  @Override
  public void deleteSession(Long sessionId) {
    ChatSession session = chatSessionRepository.findById(sessionId)
                                               .orElseThrow(() -> new ResourceNotFoundException(SESSION_NOT_FOUND));

    // Hard delete the session; messages are deleted automatically via cascade
    chatSessionRepository.delete(session);
  }

  @Override
  public ChatSession markFavorite(Long sessionId, FavoriteRequest request) {
    ChatSession session = chatSessionRepository.findById(sessionId)
                                               .orElseThrow(() -> new ResourceNotFoundException(
                                                 SESSION_NOT_FOUND));
    session.setFavorite(request.favorite());
    return chatSessionRepository.save(session);
  }

  public String retrieveContext(String query, Long sessionId) {
    Float[] queryEmbedding = getEmbedding(query);
    List<ChatMessage> topMessages = findTopKSimilar(sessionId, queryEmbedding, 5);
    return topMessages.stream()
                      .map(ChatMessage::getContent)
                      .collect(Collectors.joining("\n"));
  }

  protected List<ChatMessage> findTopKSimilar(Long sessionId, Float[] embedding, int topK) {
    if (embedding == null || embedding.length == 0) return Collections.emptyList();

    String vectorLiteral = toPgVectorLiteral(embedding);

    String sql = """
                SELECT id, session_id, sender, content, retrieved_context, created_at
                FROM chat_messages
                WHERE session_id = ?
                ORDER BY embedding <-> ?::vector
                LIMIT ?
                """;

    return jdbcTemplate.query(sql, new Object[]{sessionId, vectorLiteral, topK}, (rs, rowNum) -> {
      ChatMessage msg = new ChatMessage();
      msg.setId(rs.getLong("id"));
      ChatSession session = new ChatSession();
      session.setId(rs.getLong("session_id"));
      msg.setSession(session);
      msg.setSender(rs.getString("sender"));
      msg.setContent(rs.getString("content"));
      msg.setRetrievedContext(rs.getString("retrieved_context"));
      return msg;
    });
  }

  // ==================== EMBEDDING UTILS ====================

  public Float[] getEmbedding(String text) {
    Float[] vector = new Float[VECTOR_SIZE];
    int hash = text.hashCode();
    for (int i = 0; i < VECTOR_SIZE; i++) {
      vector[i] = ((hash >> (i % 32)) & 0xFF) / 255.0f;
    }
    return vector;
  }

  private String toPgVectorLiteral(Float[] embedding) {
    return Arrays.stream(embedding)
                 .map(String::valueOf)
                 .collect(Collectors.joining(",", "[", "]"));
  }

}
