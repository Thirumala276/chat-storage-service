package com.raga.chat.controller;

import com.raga.chat.model.AddMessageRequest;
import com.raga.chat.model.CreateSessionRequest;
import com.raga.chat.model.FavoriteRequest;
import com.raga.chat.model.RenameRequest;
import com.raga.chat.persistence.entity.ChatMessage;
import com.raga.chat.persistence.entity.ChatSession;
import com.raga.chat.service.ChatSessionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat/session")
@RequiredArgsConstructor
public class ChatSessionController {

  private final ChatSessionService chatSessionService;

  @PostMapping
  public ResponseEntity<ChatSession> createSession(@RequestBody CreateSessionRequest req) {
    ChatSession session = chatSessionService.createSession(req.userId(), req.title());
    return ResponseEntity.status(HttpStatus.CREATED).body(session);
  }

  @GetMapping
  public ResponseEntity<List<ChatSession>> getSessionsByUserId(@RequestParam String userId) {
    List<ChatSession> sessions = chatSessionService.getSessionsByUserId(userId);
    return ResponseEntity.ok(sessions);
  }

  @PostMapping("/{sessionId}/messages")
  public ResponseEntity<ChatMessage> addMessage(@PathVariable Long sessionId,
                                                @RequestBody AddMessageRequest req) {
    ChatMessage message = chatSessionService.addMessage(sessionId, req.sender(), req.content());
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @GetMapping("/{sessionId}/messages")
  public Page<ChatMessage> getMessages(@PathVariable Long sessionId,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
    return chatSessionService.getMessages(sessionId, page, size);
  }

  @PatchMapping("/{sessionId}/rename")
  public ChatSession rename(@PathVariable Long sessionId, @RequestBody RenameRequest req) {
    return chatSessionService.renameSession(sessionId, req.title());
  }

  @PatchMapping("/{sessionId}/favorite")
  public ChatSession favorite(@PathVariable Long sessionId, @RequestBody FavoriteRequest req) {
    return chatSessionService.markFavorite(sessionId, req.favorite());
  }

  @DeleteMapping("/{sessionId}")
  public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
    chatSessionService.deleteSession(sessionId);
    return ResponseEntity.noContent().build();
  }
}
