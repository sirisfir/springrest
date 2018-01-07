package com.computaris.springtraining.controller;

import com.computaris.springtraining.domain.Article;
import com.computaris.springtraining.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
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

}
