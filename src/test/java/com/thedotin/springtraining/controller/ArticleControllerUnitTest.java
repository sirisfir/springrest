package com.thedotin.springtraining.controller;

import com.thedotin.springtraining.domain.Article;
import com.thedotin.springtraining.repository.ArticleRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ArticleRepository.class, ArticleController.class, Optional.class})
public class ArticleControllerUnitTest {

    ArticleRepository mockRepo;
    Page mockPage;
    Article mockArticle;
    ArticleController subject;
    Specification mockSpecification;
    Pageable mockPageable;
    Optional mockOptional = mock(Optional.class);

    @Before
    public void setUp() {
        mockRepo = mock(ArticleRepository.class);
        mockPage = mock(Page.class);
        mockSpecification = mock(Specification.class);
        mockPageable = mock(Pageable.class);
        mockArticle= mock(Article.class);
        subject = new ArticleController(mockRepo);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(mockRepo);
    }

    @Test
    public void findAllTest() throws Exception {
        doReturn(mockPage).when(mockRepo).findAll(any(Specification.class), any(Pageable.class));
        when(mockPage.getContent()).thenReturn(new ArrayList());
        List<Article> results = subject.getArticles(mockPageable, "foo:bar");
        verify(mockRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
        assertThat(results.size(), is(0));
    }

    @Test
    public void createOneTest() {
        doReturn(mockArticle).when(mockRepo).save(any(Article.class));
        subject.createArticle(mockArticle);
        verify(mockRepo, times(1)).save(any(Article.class));
    }

    @Test
    public void updateOneTest() {
        doReturn(mockOptional).when(mockRepo).findById(any(Integer.class));
        doReturn(mockArticle).when(mockOptional).orElse(any());
        subject.updateArticle(100, mockArticle);
        verify(mockRepo).findById(any(Integer.class));
        verify(mockRepo).save(any(Article.class));
    }

    @Test
    public void deleteOneTest() {
        doNothing().when(mockRepo).deleteById(any(Integer.class));
        subject.deleteArticle(100);
        verify(mockRepo, times(1)).deleteById(any(Integer.class));
    }

}
