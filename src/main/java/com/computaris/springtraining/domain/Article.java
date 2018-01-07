package com.computaris.springtraining.domain;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author valentin.raduti
 */
@Entity
public class Article {

    public enum ArticleType {
	FRONT_PAGE, BLOG
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic(optional = false)
    private String title;

    @Basic
    private String text;

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private ArticleType articleType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author")
    private User author;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public Date getPublishDate() {
	return publishDate;
    }

    public void setPublishDate(Date publishDate) {
	this.publishDate = publishDate;
    }

    public ArticleType getArticleType() {
	return articleType;
    }

    public void setArticleType(ArticleType articleType) {
	this.articleType = articleType;
    }

    public User getAuthor() {
	return author;
    }

    public void setAuthor(User author) {
	this.author = author;
    }

}
