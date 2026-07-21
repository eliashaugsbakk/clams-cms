package no.eliashaugsbakk.clams.server.repository;

import java.util.List;
import java.util.Optional;
import no.eliashaugsbakk.clams.server.model.Article;
import no.eliashaugsbakk.clams.server.model.ArticleMetaData;

public interface ArticlesRepo {
  List<ArticleMetaData> listArticlesMetaData();
  Optional<Article> getArticle(String slug);
  List<ArticleMetaData> searchArticlesBody(String query);

  void addArticle(Article article);
  void updateArticle(Article article);
  void deleteArticle(String slug);
  boolean existsArticleBySlug(String slug);
}
