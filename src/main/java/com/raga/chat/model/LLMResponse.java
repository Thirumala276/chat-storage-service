package com.raga.chat.model;

import java.util.List;

public record LLMResponse(List<LLMResponse.Candidate> candidates) {

  public String getText() {
    if (candidates != null && !candidates.isEmpty()) {
      LLMResponse.Candidate first = candidates.get(0);
      if (first.content() != null && first.content().parts() != null && !first.content()
                                                                              .parts()
                                                                              .isEmpty()) {
        return first.content().parts().get(0).text();
      }
    }
    return ""; // never return null
  }

  public record Candidate(LLMResponse.Content content) { }

  public record Content(List<LLMResponse.Part> parts, String role) {}

  public record Part(String text) {}
}
