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
import com.raga.chat.service.EmbeddingService;
import com.raga.chat.service.LLMService;
import com.raga.chat.service.KnowledgeBaseService;
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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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

  @Mock
  private KnowledgeBaseService knowledgeBaseService;


  @Mock
  private EmbeddingService embeddingService;

  @Spy
  @InjectMocks
  private ChatSessionServiceImpl chatSessionService;

  @Test
  void testCreateSession() {
    CreateSessionRequest req = new CreateSessionRequest("user1", "Session 1");
    ChatSession session = new ChatSession();
    session.setUserId("user1");
    session.setTitle("Session 1");

    when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);

    ChatSession result = chatSessionService.createSession(req);

    assertThat(result.getUserId()).isEqualTo("user1");
    assertThat(result.getTitle()).isEqualTo("Session 1");
  }

  @Test
  void testGetSessionsByUserId_found() {
    ChatSession session = new ChatSession();
    session.setId(1L);

    when(chatSessionRepository.findByUserIdOrderByModifiedAtDesc("user1"))
      .thenReturn(Optional.of(List.of(session)));

    List<ChatSession> result = chatSessionService.getSessionsByUserId("user1");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(1L);
  }

  @Test
  void testGetSessionsByUserId_notFound() {
    when(chatSessionRepository.findByUserIdOrderByModifiedAtDesc("user1"))
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
    // Mock session
    ChatSession session = new ChatSession();
    session.setId(1L);
    when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));

    // Mock request
    AddMessageRequest req = new AddMessageRequest("user1", "Hello");

    // Mock embeddings
    Float[] embedding = new Float[]{0.1f, 0.2f};
    when(embeddingService.getEmbedding(anyString())).thenReturn(embedding);

    // Mock previous messages (using doReturn to bypass strict stubbing)
    doReturn(List.of()).when(chatMessageRepository)
                       .fetchLastMessage(any(), any());

    // Mock knowledge base context
    when(knowledgeBaseService.searchRelevantContext(any(), anyInt()))
      .thenReturn("KB Context");

    // Mock LLM streaming response
    when(llmService.generateResponseWithContext(anyString(), anyString(), anyString()))
      .thenReturn(Flux.just("AI Response Part1", "AI Response Part2"));

    // Mock save
    when(chatMessageRepository.save(any(ChatMessage.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    // Execute
    Flux<ChatMessage> resultFlux = chatSessionService.addMessage(1L, req);

    // Verify streaming response chunks
    StepVerifier.create(resultFlux)
                .expectNextMatches(msg -> msg.getContent().equals("AI Response Part1") && msg.getSender().equals("AI"))
                .expectNextMatches(msg -> msg.getContent().equals("AI Response Part2") && msg.getSender().equals("AI"))
                .verifyComplete();

    // Verify user message saved
    verify(chatMessageRepository).save(argThat(msg ->
                                                 msg.getContent().equals("Hello") &&
                                                   msg.getSender().equals("user1") &&
                                                   msg.getSession().equals(session)
    ));

    // Verify AI message saved after full response
    verify(chatMessageRepository).save(argThat(msg ->
                                                 msg.getContent().contains("AI Response Part1") &&
                                                   msg.getContent().contains("AI Response Part2") &&
                                                   msg.getSender().equals("AI") &&
                                                   msg.getSession().equals(session) &&
                                                   msg.getRetrievedContext().equals("KB Context")
    ));
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
    doNothing().when(chatSessionRepository).delete(any(ChatSession.class));

    chatSessionService.deleteSession(1L);

    verify(chatSessionRepository, times(1)).delete(session);
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
