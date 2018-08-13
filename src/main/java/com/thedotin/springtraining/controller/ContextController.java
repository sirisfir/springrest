package com.thedotin.springtraining.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author valentin.raduti
 */
@RestController
@RequestMapping("/api/context")
public class ContextController {

    private final ApplicationContext context;

    @Autowired
    public ContextController(ApplicationContext context) {
	this.context = context;
    }

    @GetMapping(produces = "application/json")
    public List<String> getContextMembers() {
	return Arrays.asList(context.getBeanDefinitionNames());
    }
}
