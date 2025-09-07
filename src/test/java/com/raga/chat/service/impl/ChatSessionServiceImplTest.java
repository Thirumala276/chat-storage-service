package com.raga.chat.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.raga.chat.exception.ResourceNotFoundException;
import com.raga.chat.model.AddMessageRequest;
import com.raga.chat.model.CreateSessionRequest;
import com.raga.chat.model.FavoriteRequest;
import com.raga.chat.model.RenameRequest;
import com.raga.chat.persistence.entity.ChatMessage;
import com.raga.chat.persistence.entity.ChatSession;
import com.raga.chat.persistence.repository.ChatMessageRepository;
import com.raga.chat.persistence.repository.ChatSessionRepository;
import com.raga.chat.service.LLMService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceImplTest {

  @Mock
  private ChatSessionRepository chatSessionRepository;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private LLMService llmService;

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Spy
  @InjectMocks
  private ChatSessionServiceImpl chatSessionService;

  @Test
  void testCreateSession() {
    ChatSession session = new ChatSession();
    session.setUserId("user1");
    session.setTitle("Session 1");

    when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);

    ChatSession result = chatSessionService.createSession(new CreateSessionRequest("user1", "Session 1"));

    assertThat(result.getUserId()).isEqualTo("user1");
    assertThat(result.getTitle()).isEqualTo("Session 1");
  }

  @Test
  void testGetSessionsByUserId_found() {
    ChatSession session = new ChatSession();
    session.setId(1L);

    when(chatSessionRepository.findByUserIdAndDeletedAtIsNullOrderByUpdatedAtDesc("user1"))
      .thenReturn(Optional.of(List.of(session)));

    List<ChatSession> result = chatSessionService.getSessionsByUserId("user1");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(1L);
  }

  @Test
  void testGetSessionsByUserId_notFound() {
    when(chatSessionRepository.findByUserIdAndDeletedAtIsNullOrderByUpdatedAtDesc("user1"))
      .thenReturn(Optional.empty());

    List<ChatSession> result = chatSessionService.getSessionsByUserId("user1");

    assertThat(result).isEmpty();
  }

  @Test
  void testGetMessages_success() {
    ChatSession session = new ChatSession();
    session.setId(1L);
    when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));

    ChatMessage msg = new ChatMessage();
    Page<ChatMessage> page = new PageImpl<>(List.of(msg), PageRequest.of(0, 10), 1);
    when(chatMessageRepository.findBySession(eq(session), any(PageRequest.class))).thenReturn(page);

    Page<ChatMessage> result = chatSessionService.getMessages(1L, 0, 10);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void testGetMessages_sessionNotFound() {
    when(chatSessionRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> chatSessionService.getMessages(1L, 0, 10));
  }

  @Test
  void testAddMessage_success() {
    ChatSession session = new ChatSession();
    session.setId(1L);
    when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(llmService.generateResponseWithContext(anyString(), anyString())).thenReturn("AI Response");

    // mock retrieveContext via spy
    doReturn("Context").when(chatSessionService).retrieveContext(anyString(), anyLong());
    when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ChatMessage aiMessage = chatSessionService.addMessage(1L, new AddMessageRequest("user1", "Hello"));

    assertThat(aiMessage.getSender()).isEqualTo("AI");
    assertThat(aiMessage.getContent()).isEqualTo("AI Response");
  }

  @Test
  void testRenameSession_success() {
    ChatSession session = new ChatSession();
    session.setId(1L);
    session.setTitle("Old Title");
    when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);

    ChatSession result = chatSessionService.renameSession(1L, new RenameRequest("New Title"));

    assertThat(result.getTitle()).isEqualTo("New Title");
  }

  @Test
  void testDeleteSession_success() {
    ChatSession session = new ChatSession();
    session.setId(1L);
    when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);

    chatSessionService.deleteSession(1L);

    assertThat(session.getDeletedAt()).isNotNull();
  }

  @Test
  void testMarkFavorite_success() {
    ChatSession session = new ChatSession();
    session.setId(1L);
    session.setFavorite(false);
    when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);

    ChatSession result = chatSessionService.markFavorite(1L, new FavoriteRequest(true));

    assertThat(result.isFavorite()).isTrue();
  }

}
