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
import com.raga.chat.service.EmbeddingService;
import com.raga.chat.service.KnowledgeBaseService;
import com.raga.chat.service.LLMService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

  private static final String SESSION_NOT_FOUND = "Session not found";

  private final LLMService llmService;
  private final ChatSessionRepository chatSessionRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final KnowledgeBaseService knowledgeBaseService;

  @Override
  public ChatSession createSession(CreateSessionRequest request) {
    ChatSession s = new ChatSession();
    s.setUserId(request.userId());
    s.setTitle(request.title());
    return chatSessionRepository.save(s);
  }

  @Override
  public List<ChatSession> getSessionsByUserId(String userId) {
    return chatSessionRepository.findByUserIdOrderByModifiedAtDesc(userId).orElse(List.of());
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
  public Flux<ChatMessage> addMessage(Long sessionId, AddMessageRequest request) {
    ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException(SESSION_NOT_FOUND));

    String question = request.content();

    List<ChatMessage> previousMessages = chatMessageRepository.fetchLastMessage(session, PageRequest.of(0,5));

    String conversationContext = previousMessages.stream().map(ChatMessage::getContent).collect(Collectors.joining("\n"));
    // Save user message
    var userMessage = new ChatMessage();
    userMessage.setSession(session);
    userMessage.setSender(request.sender());
    userMessage.setContent(question);
    chatMessageRepository.save(userMessage);

    String kbContext = knowledgeBaseService.searchRelevantContext(question, 5);

    return getChatMessageFluxFromLLM(question, conversationContext, kbContext, session);
  }


  @Override
  public ChatSession renameSession(Long sessionId, RenameRequest request) {
    ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException(SESSION_NOT_FOUND));
    session.setTitle(request.title());
    return chatSessionRepository.save(session);
  }

  @Override
  public void deleteSession(Long sessionId) {
    ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException(SESSION_NOT_FOUND));
    // Hard delete the session; messages are deleted automatically via cascade
    chatSessionRepository.delete(session);
  }

  @Override
  public ChatSession markFavorite(Long sessionId, FavoriteRequest request) {
    ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException(SESSION_NOT_FOUND));
    session.setFavorite(request.favorite());
    return chatSessionRepository.save(session);
  }


  private Flux<ChatMessage> getChatMessageFluxFromLLM(String question, String conversationContext, String kbContext, ChatSession session) {
    // Stream AI response
    StringBuilder aiContentBuilder = new StringBuilder();
    return llmService.generateResponseWithContext(question, conversationContext, kbContext)
                     .doOnNext(aiContentBuilder::append)
                     .doOnComplete(() -> {
                       // After full response is received, save AI message
                       var aiMessage = new ChatMessage();
                       aiMessage.setSession(session);
                       aiMessage.setSender("AI");
                       aiMessage.setContent(aiContentBuilder.toString());
                       aiMessage.setRetrievedContext(kbContext);
                       chatMessageRepository.save(aiMessage);
                     })
                     .map(chunk -> {
                       // Optional: wrap streaming chunks as ChatMessage events
                       var message = new ChatMessage();
                       message.setSession(session);
                       message.setSender("AI");
                       message.setContent(chunk);
                       return message;
                     });
  }
}
