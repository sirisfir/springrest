package com.computaris.springtraining.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.thedotin.springtraining.controller.UserController;
import com.thedotin.springtraining.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author valentin.raduti
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@EnableSpringDataWebSupport
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testGetUsers() throws Exception {
	this.mvc.perform(get("/api/user").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnauthorized());
    }
}
