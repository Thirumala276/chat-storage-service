package com.raga.chat.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PromptTemplate {

  public static String buildPrompt(String conversationContext, String knowledgeContext, String question) {
    return String.format("""
            You are a knowledgeable and helpful AI assistant capable of answering a wide range of questions accurately and clearly.
            Use the following conversation history and knowledge base context to generate responses that are precise, relevant, and consistent with prior interactions.

            Conversation History:
            %s

            Knowledge Base:
            %s

            User's Current Question:
            %s

            Guidelines:
            1. Provide clear, concise, and accurate answers based on context.
            2. Reference relevant knowledge base entries when applicable.
            3. Maintain continuity with previous conversation; avoid unnecessary repetition.
            4. Include examples, explanations, or code snippets if relevant.
            5. Assume standard definitions and common interpretations unless explicitly stated otherwise.
            6. Keep responses professional and helpful, while adapting to the context provided.
            7. Only ask the user for clarification if absolutely necessary.

            Response:
            """, conversationContext, knowledgeContext, question);
  }
}
