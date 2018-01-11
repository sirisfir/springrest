package com.computaris.springtraining.controller;

import com.computaris.springtraining.domain.Article;
import com.computaris.springtraining.domain.User;
import com.computaris.springtraining.repository.ArticleRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Page<Article> getArticles(@PageableDefault(page = 0, size = 10) Pageable p,
	    @RequestParam(defaultValue = "", required = false, name = "search") String search) {
	return this.articleRepository.search(search, p);
    }

    @GetMapping(value="/public/articles", produces = "application/json")
    public Page<Article> getPublicArticles(@PageableDefault(page = 0, size = 10) Pageable p,
	    @RequestParam(defaultValue = "", required = false, name = "search") String search) {
	return this.articleRepository.searchPublic(search, p);
    }
    
    @Transactional
    @PostMapping(consumes = "application/json")
    public Article createArticle(@RequestBody Article a) {
	a.setAuthor((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	return this.articleRepository.save(a);
    }
    
    @PutMapping(value = "/{articleId}", consumes = "application/json")
    public Article updateUser(@PathVariable("articleId") Integer articleId, @RequestBody Article article) {
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
    
    @DeleteMapping("/{articleId}")
    public void deleteUser(@PathVariable("articleId") Integer articleId) {
	this.articleRepository.deleteById(articleId);
    }

}
