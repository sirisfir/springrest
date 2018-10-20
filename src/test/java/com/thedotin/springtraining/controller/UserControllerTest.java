package com.thedotin.springtraining.controller;

import com.thedotin.springtraining.repository.UserRepository;
import com.thedotin.springtraining.specifications.builders.UserSpecificationsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;


import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserSpecificationsBuilder.class, UserRepository.class, UserController.class})
public class UserControllerTest {

    UserRepository mockRepo;
    Page mockPage;
    UserSpecificationsBuilder mockUserBuilder;
    UserController subject;
    Specification mockSpecification;

    @Before
    public void setUp() throws Exception {
//        mockUserBuilder = PowerMockito.mock(UserSpecificationsBuilder.class);
//        whenNew(UserSpecificationsBuilder.class).withAnyArguments().thenReturn(mockUserBuilder);
        mockRepo = PowerMockito.mock(UserRepository.class);
        mockPage = PowerMockito.mock(Page.class);
        mockSpecification = PowerMockito.mock(Specification.class);
        subject = new UserController(mockRepo);
    }

    @Test
    public void findAllTest() throws Exception {

        doReturn(mockPage).when(mockRepo).findAll(any(Specification.class), any(Pageable.class));
//        whenNew(UserSpecificationsBuilder.class).withAnyArguments().thenReturn(mockUserBuilder);
//        doReturn(mockUserBuilder).when(mockUserBuilder).with(anyString(), anyString(), anyObject());
//        when(mockUserBuilder.build()).thenReturn(mockSpecification);
        when(mockPage.getContent()).thenReturn(new ArrayList());

        Pageable p = new MyPageable();
        subject.getUsers(p, "a:b");

        ArgumentCaptor<Specification> specificationCaptor = ArgumentCaptor.forClass(Specification.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(mockRepo, times(1)).findAll(specificationCaptor.capture(), pageableCaptor.capture());
//        assertThat(specificationCaptor.getValue(), is(mockSpecification));
        assertThat(pageableCaptor.getValue(), is(p));
        verifyNoMoreInteractions(mockRepo);

    }

    private static class MyPageable implements Pageable {
        @Override
        public int getPageNumber() {
            return 0;
        }

        @Override
        public int getPageSize() {
            return 0;
        }

        @Override
        public long getOffset() {
            return 0;
        }

        @Override
        public Sort getSort() {
            return null;
        }

        @Override
        public Pageable next() {
            return null;
        }

        @Override
        public Pageable previousOrFirst() {
            return null;
        }

        @Override
        public Pageable first() {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }
    }
}
