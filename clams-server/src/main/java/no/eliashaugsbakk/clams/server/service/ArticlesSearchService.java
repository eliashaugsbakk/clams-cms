package no.eliashaugsbakk.clams.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.eliashaugsbakk.clams.server.model.ArticleMetaData;
import no.eliashaugsbakk.clams.server.repository.ArticlesRepo;

public class ArticlesSearchService {
  private final ArticlesRepo articlesRepo;

  public ArticlesSearchService(ArticlesRepo articlesRepo) {
    this.articlesRepo = articlesRepo;
  }

  public List<ArticleMetaData> searchArticles(String query) {
    if (query == null || query.isBlank()) return List.of();

    int titleValue = 10;
    int summaryValue = 5;
    int bodyValue = 1;

    Map<ArticleMetaData, Integer> searchRanking = new HashMap<>();
    String lowerQuery = query.toLowerCase();

    List<ArticleMetaData> articlesMetaData = articlesRepo.listArticlesMetaData();
    List<ArticleMetaData> bodyResults = articlesRepo.searchArticlesBody(query);

    articlesMetaData.stream()
        .filter(article -> article.title().toLowerCase().contains(lowerQuery))
        .forEach(article -> searchRanking.merge(article, titleValue, Integer::sum));

    articlesMetaData.stream()
        .filter(article -> article.summary() != null && article.summary().toLowerCase().contains(lowerQuery))
        .forEach(article -> searchRanking.merge(article, summaryValue, Integer::sum));

    bodyResults.forEach(article -> searchRanking.merge(article, bodyValue, Integer::sum));

    return searchRanking.entrySet().stream()
        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
        .map(Map.Entry::getKey)
        .toList();
  }
}
