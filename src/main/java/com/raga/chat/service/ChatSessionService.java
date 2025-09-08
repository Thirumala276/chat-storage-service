package com.raga.chat.service;

import com.raga.chat.model.AddMessageRequest;
import com.raga.chat.model.CreateSessionRequest;
import com.raga.chat.model.FavoriteRequest;
import com.raga.chat.model.RenameRequest;
import com.raga.chat.persistence.entity.ChatMessage;
import com.raga.chat.persistence.entity.ChatSession;
import java.util.List;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;

public interface ChatSessionService {

  ChatSession createSession(CreateSessionRequest req);

  List<ChatSession> getSessionsByUserId(String userId);

  Page<ChatMessage> getMessages(Long sessionId, int page, int size);

  ChatSession markFavorite(Long sessionId, FavoriteRequest request);

  Flux<ChatMessage> addMessage(Long sessionId, AddMessageRequest request);

  ChatSession renameSession(Long sessionId, RenameRequest request);

  void deleteSession(Long sessionId);
}
