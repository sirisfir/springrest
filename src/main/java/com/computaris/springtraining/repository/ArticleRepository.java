package com.computaris.springtraining.repository;

import com.computaris.springtraining.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author valentin.raduti
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query("select a from Article a where a.author.username like %:search% or a.title like %:search%")
    public Page<Article> search(@Param("search") String search, Pageable p);
    
    
    @Query("select a from Article a where (a.author.username like %:search% or a.title like %:search%) and a.articleType=com.computaris.springtraining.domain.Article$ArticleType.FRONT_PAGE")
    public Page<Article> searchPublic(@Param("search") String search, Pageable p);

}
