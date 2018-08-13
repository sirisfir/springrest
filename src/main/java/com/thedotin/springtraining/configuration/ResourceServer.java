/*
 * Copyright 2017 Gabriel Valentin Raduti.
 * This source code file is the property of Gabriel Valentin Raduti.
 * You are not allowed to view, edit, copy, re-use or re-engineer
 * this source code file unless specifically permissioned by Gabriel Valentin Raduti.
 */
package com.computaris.springtraining.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 *
 * @author valentin.raduti
 */
@Configuration
@EnableResourceServer
public class ResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
	http
		.authorizeRequests()
		/**
		 * These permissions...
		 */
		.antMatchers("/v2/**").permitAll()
		.antMatchers("/webjars/**").permitAll()
		.antMatchers("/swagger-ui.html").permitAll()
		.antMatchers("/swagger-resources/**").permitAll()
		/**
		 * ...should be removed in production
		 */
		.antMatchers("/public/**").permitAll()
		.anyRequest().access("#oauth2.hasScope('read')");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
	    throws Exception {
	resources.resourceId("springTraining");
    }

}
