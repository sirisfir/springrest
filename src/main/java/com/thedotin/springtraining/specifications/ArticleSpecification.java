package com.thedotin.springtraining.specifications;

import com.thedotin.springtraining.domain.Article;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecification<Article> extends GenericSpecification<Article> implements Specification<Article> {

    public ArticleSpecification(SearchCriteria param) {
        super(param);
    }
}
