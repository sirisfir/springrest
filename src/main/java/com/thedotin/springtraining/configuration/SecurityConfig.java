/*
 * Copyright 2017 Gabriel Valentin Raduti.
 * This source code file is the property of Gabriel Valentin Raduti.
 * You are not allowed to view, edit, copy, re-use or re-engineer
 * this source code file unless specifically permissioned by Gabriel Valentin Raduti.
 */
package com.thedotin.springtraining.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

import com.thedotin.springtraining.domain.User;
import com.thedotin.springtraining.repository.UserRepository;

/**
 *
 * @author valentin.raduti
 */
@Configuration
@EnableAuthorizationServer
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepo;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	http
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.formLogin().disable()
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
		.antMatchers("/oauth/**").permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.userDetailsService(userDetailsServiceBean());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
	// provides the default AuthenticationManager as a Bean
	return super.authenticationManagerBean();
    }

    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
	return (u) -> {
	    User user = this.userRepo.findOneByUsername(u);
	    if (user == null) {
		throw new UsernameNotFoundException("Email not found");
	    }
	    return user;
	};
    }

}
