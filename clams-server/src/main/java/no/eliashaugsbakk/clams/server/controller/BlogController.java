package no.eliashaugsbakk.clams.server.controller;

import io.javalin.http.Context;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import no.eliashaugsbakk.clams.server.repository.BlogPostRepo;
import no.eliashaugsbakk.clams.server.repository.BlogPostRepoSqlite;
import no.eliashaugsbakk.clams.server.repository.SqliteManager;
import no.eliashaugsbakk.clams.server.utils.MarkdownConverter;

public class BlogController {
  private final BlogPostRepo blogPostRepo;

  public BlogController(SqliteManager sqliteManager) {
    this.blogPostRepo = new BlogPostRepoSqlite(sqliteManager);
  }

  public void handleGetPost(Context ctx) {
    blogPostRepo.getPost(ctx.pathParam("slug")).ifPresentOrElse(post -> ctx.render(
        "templates/post.html",
        Map.of("page_title", post.title() + " -- Elias Haugsbakk",
            "page_css", "post",
            "title", post.title(),
            "published_date", post.timePublished().atZone(ZoneId.of("Europe/Oslo")).format(
                DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            "author", "by Elias Haugsbakk",
            "content", MarkdownConverter.convertToHtml(post.content()),
            "back_to_index", "<a href=\"/blog/\">back to all posts</a>"
        )), () -> ctx.status(404));
  }

  public void handleGetOverview(Context ctx) {
    StringBuilder allPosts = new StringBuilder();

    int prevYear = blogPostRepo.listPostsByPublishedDesc().getFirst().published()
        .atZone(ZoneId.of("Europe/Oslo")).getYear();

    allPosts.append(String.format("""
        <details class="year-accordion" open>
          <summary class="year-toggle">%d</summary>
          <ul class="post-list">
        """, prevYear));

    for (var post : blogPostRepo.listPostsByPublishedDesc()) {
      int year = post.published().atZone(ZoneId.of("Europe/Oslo")).getYear();
      if (year == prevYear) {
        allPosts.append(String.format("""
                <li><a href="/blog/%s">%s</a></li>
            """, post.slug(), post.title()));
      } else {
        allPosts.append(String.format("""
                </ul>
              </details>
              <details class="year-accordion">
                <summary class="year-toggle">%d</summary>
                <ul class="post-list">
                  <li><a href="/blog/%s">%s</a></li>
            """, year, post.slug(), post.title()));
        prevYear = year;
      }
    }
    allPosts.append("""
          </ul>
        </details>
        """);


    var featuredSlugs = List.of("test-page", "another-one");

    StringBuilder featuredPosts = new StringBuilder();
    featuredPosts.append("<ul class=\"recent-list\">");

    for (String slug : featuredSlugs) {
      blogPostRepo.getPost(slug).ifPresent(post -> featuredPosts.append(String.format("""
          <li>
            <a href="/blog/%s">%s</a> <span class="featured-desc">-- %s</span>
          </li>
          """, post.slug(), post.title(), post.summary())));
    }
    featuredPosts.append("</ul>");

    ctx.render("templates/blog.html",
        Map.of("page_title", "Elias Haugsbakk's Blog", "page_css", "blog", "all_posts",
            allPosts.toString(), "recent_posts", featuredPosts.toString()));
  }
}
