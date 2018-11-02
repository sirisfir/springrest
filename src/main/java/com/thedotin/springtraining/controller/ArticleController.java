package com.thedotin.springtraining.controller;

import com.thedotin.springtraining.domain.Article;
import com.thedotin.springtraining.domain.User;
import com.thedotin.springtraining.repository.ArticleRepository;
import com.thedotin.springtraining.specifications.builders.ArticleSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author valentin.raduti
 */
@RestController
public class ArticleController {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleController(ArticleRepository articleRepository) {
	this.articleRepository = articleRepository;
    }

    @GetMapping(value="/api/articles",produces = "application/json")
    public List<Article> getArticles(@PageableDefault(page = 0, size = 10) Pageable p,
                                     @RequestParam(defaultValue = "", required = false, name = "search") String search) {

        ArticleSpecificationBuilder<Article> builder = new ArticleSpecificationBuilder();

        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        Specification<Article> spec = builder.build();
        Page<Article> pageArticle = articleRepository.findAll(spec, p);

        return pageArticle.getContent();

    }

    @GetMapping(value="/public/articles", produces = "application/json")
    public Page<Article> getPublicArticles(@PageableDefault(page = 0, size = 10) Pageable p,
	    @RequestParam(defaultValue = "", required = false, name = "search") String search) {

        return this.articleRepository.searchPublic(search, p);
    }
    
    @Transactional
    @PostMapping(value="/api/articles",consumes = "application/json")
    public Article createArticle(@RequestBody Article a) {
	    return this.articleRepository.save(a);
    }
    
    @Transactional
    @PutMapping(value = "/api/articles/{articleId}", consumes = "application/json")
    public Article updateArticle(@PathVariable("articleId") Integer articleId, @RequestBody Article article) {
        Article a = this.articleRepository.findById(articleId).orElse(null);
        if (a == null) {
            a = article;
            a.setAuthor((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } else {
            a.setArticleType(article.getArticleType());
            a.setPublishDate(article.getPublishDate());
            a.setText(article.getText());
            a.setTitle(article.getTitle());
        }
        return this.articleRepository.save(a);
    }
    
    @DeleteMapping("/api/articles/{articleId}")
    public void deleteArticle(@PathVariable("articleId") Integer articleId) {
	    this.articleRepository.deleteById(articleId);
    }

}
