package com.thedotin.springtraining.controller;

import com.thedotin.springtraining.domain.User;
import com.thedotin.springtraining.repository.UserRepository;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserRepository.class, UserController.class, Optional.class})
public class UserControllerUnitTest {

    UserRepository mockRepo;
    Page mockPage;
    User mockUser;
    UserController subject;
    Specification mockSpecification;
    Pageable mockPageable;
    Optional mockOptional;

    @Before
    public void setUp() {
        mockRepo = mock(UserRepository.class);
        mockPage = mock(Page.class);
        mockSpecification = mock(Specification.class);
        mockPageable = mock(Pageable.class);
        mockUser = mock(User.class);
        mockOptional = mock(Optional.class);
        subject = new UserController(mockRepo);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(mockRepo);
    }

    @Test
    public void findAllTest() throws Exception {
        doReturn(mockPage).when(mockRepo).findAll(any(Specification.class), any(Pageable.class));
        when(mockPage.getContent()).thenReturn(new ArrayList());
        List results = subject.getUsers(mockPageable, "foo:bar");
        verify(mockRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
        assertThat(results.size(), is(0));
    }

    @Test
    public void createOneTest() {
        doReturn(mockUser).when(mockRepo).save(any(User.class));
        subject.createUser(mockUser);
        verify(mockRepo, times(1)).save(any(User.class));
    }

    @Test
    public void updateOneTest() {
        doReturn(mockOptional).when(mockRepo).findById(any(Integer.class));
        doReturn(mockUser).when(mockOptional).orElse(any());
        subject.updateUser(100, mockUser);
        verify(mockRepo, times(1)).findById(any(Integer.class));
        verify(mockRepo, times(1)).save(any(User.class));
    }

    @Test
    public void deleteOneTest() {
        doNothing().when(mockRepo).deleteById(any(Integer.class));
        subject.deleteUser(100);
        verify(mockRepo, times(1)).deleteById(any(Integer.class));
    }

}
