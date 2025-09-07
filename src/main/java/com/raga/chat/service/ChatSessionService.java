package com.raga.chat.service;

import com.raga.chat.persistence.entity.ChatMessage;
import com.raga.chat.persistence.entity.ChatSession;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ChatSessionService {

  ChatSession createSession(String userId, String title);

  List<ChatSession> getSessionsByUserId(String userId);

  Page<ChatMessage> getMessages(Long sessionId, int page, int size);

  ChatSession markFavorite(Long sessionId, boolean favorite);

  ChatMessage addMessage(Long sessionId, String sender, String content);

  ChatSession renameSession(Long sessionId, String title);

  void deleteSession(Long sessionId);

}
