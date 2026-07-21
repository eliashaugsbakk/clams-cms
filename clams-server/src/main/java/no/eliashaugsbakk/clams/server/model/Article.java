package no.eliashaugsbakk.clams.server.model;

import java.time.Instant;

public record Article(String title, String slug, String summary, Instant timePublished, Instant lastEdited, String content,
                   boolean isPublished) {
  public Article(ArticleDTO articleDTO, String slug) {

    this(articleDTO.title(), slug, articleDTO.summary(), Instant.now(), Instant.now(), articleDTO.content(),
        articleDTO.isPublished());
  }
}
