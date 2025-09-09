package com.raga.chat.config;

import com.raga.chat.persistence.entity.KnowledgeBase;
import com.raga.chat.persistence.repository.KnowledgeBaseRepository;
import com.raga.chat.service.EmbeddingService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeBaseLoader implements CommandLineRunner {

  private final KnowledgeBaseRepository kbRepository;

  private final EmbeddingService embeddingService;

  @Override
  public void run(String... args) throws IOException {
    loadPDFs();
  }

  private void loadPDFs() throws IOException {
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resolver.getResources("classpath:kb/*.pdf");
    for (Resource resource : resources) {
      String title = resource.getFilename();

      // Skip if already exists in DB
      if (kbRepository.existsByTitle(title)) {
        continue;
      }

      String content = extractTextFromPDF(resource);

      KnowledgeBase kb = new KnowledgeBase();
      kb.setTitle(title);
      kb.setContent(content);
      kb.setEmbedding(embeddingService.getEmbedding(content));
      kbRepository.save(kb);
    }
  }

  private String extractTextFromPDF(Resource resource) throws IOException {
    try (PDDocument document = PDDocument.load(resource.getInputStream())) {
      PDFTextStripper stripper = new PDFTextStripper();
      return stripper.getText(document);
    }
  }
}
