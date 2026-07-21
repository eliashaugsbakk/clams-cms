package no.eliashaugsbakk.clams.server.config;

import java.nio.file.Path;
import no.eliashaugsbakk.clams.server.controller.ArticlesController;
import no.eliashaugsbakk.clams.server.controller.MediaController;
import no.eliashaugsbakk.clams.server.controller.ArticleController;
import no.eliashaugsbakk.clams.server.repository.ArticlesRepo;
import no.eliashaugsbakk.clams.server.repository.ArticlesRepoSqlite;
import no.eliashaugsbakk.clams.server.repository.MediaRepo;
import no.eliashaugsbakk.clams.server.repository.MediaRepoSqlite;
import no.eliashaugsbakk.clams.server.repository.SqliteManager;
import no.eliashaugsbakk.clams.server.service.AuthService;
import no.eliashaugsbakk.clams.server.service.SlugService;

public class AppContext implements AutoCloseable {
  private final SqliteManager dbManager;
  private final ArticlesController articlesController;
  private final MediaController mediaController;
  private final ArticleController articleController;
  private final AuthService authService;

  public AppContext() {
    AppConfig appConfig = new AppConfig();
    appConfig.loadConfig();
    String dbUrl = Path.of(appConfig.getStorageLocation()).resolve("clams.db").toString();

    this.dbManager = new SqliteManager(dbUrl);
    this.dbManager.init();

    ArticlesRepo articlesRepo = new ArticlesRepoSqlite(dbManager);
    MediaRepo mediaRepo = new MediaRepoSqlite(dbManager);
    SlugService slugService = new SlugService(articlesRepo);

    this.articlesController = new ArticlesController(dbManager);
    this.mediaController = new MediaController(mediaRepo, appConfig);
    this.articleController = new ArticleController(articlesRepo, slugService);
    this.authService = new AuthService(appConfig);
  }

  public ArticlesController getArticlesController() { return articlesController; }
  public MediaController getMediaController() { return mediaController; }
  public ArticleController getArticleController() { return articleController; }
  public AuthService getAuthService() { return authService; }

  @Override
  public void close() {
    if (dbManager != null) {
      dbManager.close();
    }
  }
}
