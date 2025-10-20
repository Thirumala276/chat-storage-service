package com.raga.chat.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.raga.chat.model.AddMessageRequest;
import com.raga.chat.model.CreateSessionRequest;
import com.raga.chat.model.FavoriteRequest;
import com.raga.chat.model.RenameRequest;
import com.raga.chat.persistence.entity.ChatMessage;
import com.raga.chat.persistence.entity.ChatSession;
import com.raga.chat.service.ChatSessionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebMvcTest(ChatSessionController.class)
class ChatSessionControllerTest {

  @Autowired
  private ChatSessionController chatSessionController;

  @MockBean
  private ChatSessionService chatSessionService;

  @Test
  void testCreateSession() {
    CreateSessionRequest req = new CreateSessionRequest("user1", "Session 1");
    ChatSession session = new ChatSession();
    session.setId(1L);
    session.setTitle("Session 1");
    session.setUserId("user1");

    when(chatSessionService.createSession(req)).thenReturn(session);

    var response = chatSessionController.createSession(req);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(session);
  }

  @Test
  void testGetSessionsByUserId() {
    List<ChatSession> sessions = List.of(new ChatSession(), new ChatSession());
    when(chatSessionService.getSessionsByUserId("user1")).thenReturn(sessions);

    var response = chatSessionController.getSessionsByUserId("user1");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(sessions);
  }

  @Test
  void testAddMessage() {
    AddMessageRequest req = new AddMessageRequest("user1", "Hello");
    ChatMessage message = new ChatMessage();
    message.setId(1L);
    message.setContent("Hello");
    message.setSender("user1");

    when(chatSessionService.addMessage(1L, req)).thenReturn(Flux.just(message));

    Flux<ChatMessage> responseFlux = chatSessionController.addMessage(1L, req);

    StepVerifier.create(responseFlux)
                .expectNextMatches(msg -> msg.getId().equals(1L) && msg.getContent().equals("Hello"))
                .verifyComplete();
  }

  @Test
  void testGetMessages() {
    ChatMessage msg1 = new ChatMessage();
    msg1.setId(1L);
    ChatMessage msg2 = new ChatMessage();
    msg2.setId(2L);

    Page<ChatMessage> page = new PageImpl<>(List.of(msg1, msg2), PageRequest.of(0, 10), 2);
    when(chatSessionService.getMessages(1L, 0, 10)).thenReturn(page);

    Page<ChatMessage> response = chatSessionController.getMessages(1L, 0, 10);

    assertThat(response.getContent()).hasSize(2);
    assertThat(response.getTotalElements()).isEqualTo(2);
  }

  @Test
  void testRename() {
    RenameRequest req = new RenameRequest("New Title");
    ChatSession session = new ChatSession();
    session.setId(1L);
    session.setTitle("New Title");

    when(chatSessionService.renameSession(1L, req)).thenReturn(session);

    ChatSession response = chatSessionController.rename(1L, req);

    assertThat(response.getTitle()).isEqualTo("New Title");
  }

  @Test
  void testFavorite() {
    FavoriteRequest req = new FavoriteRequest(true);
    ChatSession session = new ChatSession();
    session.setId(1L);
    session.setFavorite(true);

    when(chatSessionService.markFavorite(1L, req)).thenReturn(session);

    ChatSession response = chatSessionController.favorite(1L, req);

    assertThat(response.isFavorite()).isTrue();
  }

  @Test
  void testDeleteSession() {
    doNothing().when(chatSessionService).deleteSession(1L);

    var response = chatSessionController.deleteSession(1L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(chatSessionService, times(1)).deleteSession(1L);
  }
}
