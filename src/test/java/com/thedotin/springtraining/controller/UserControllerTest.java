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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserRepository.class, UserController.class})
public class UserControllerTest {

    UserRepository mockRepo;
    Page mockPage;
    User mockUser;
    UserController subject;
    Specification mockSpecification;
    Pageable mockPageable;

    @Before
    public void setUp() {
        mockRepo = mock(UserRepository.class);
        mockPage = mock(Page.class);
        mockSpecification = mock(Specification.class);
        mockPageable = mock(Pageable.class);
        mockUser = mock(User.class);
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
    public void createUserTest() {
        doReturn(mockUser).when(mockRepo).save(any(User.class));
        subject.createUser(mockUser);
        verify(mockRepo, times(1)).save(any(User.class));
    }

    @Test
    public void deleteUserTest() {
        doNothing().when(mockRepo).deleteById(any(Integer.class));
        subject.deleteUser(100);
        verify(mockRepo).deleteById(any(Integer.class));
    }

}
