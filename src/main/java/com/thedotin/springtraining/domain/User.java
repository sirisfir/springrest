package com.thedotin.springtraining.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author valentin.raduti
 */
@Entity
@ApiModel
public class User implements UserDetails {

    public static final String ROLE_EDITOR = "editor", ROLE_ADMIN = "admin";
    public static final String STATUS_ACTIVE = "active", STATUS_INACTIVE = "inactive";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic(optional = false)
    private String username;

    @Basic(optional = false)
    private String fullName;

    @Basic(optional = false)
    private String role;

    @JsonIgnore
    @Basic(optional = true)
    private String password = "{noop}password";

    @Basic(optional = false)
    private String status;

    @OneToMany(mappedBy = "author")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Article> articles = new LinkedList<>();

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    @Override
    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getFullName() {
	return fullName;
    }

    public void setFullName(String fullName) {
	this.fullName = fullName;
    }

    @JsonIgnore
    public List<Article> getArticles() {
	return articles;
    }

    public void setArticles(List<Article> articles) {
	this.articles = articles;
    }

    public String getRole() {
	return role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    @Override
    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
	return Arrays.asList(new GrantedAuthority() {
	    @Override
	    public String getAuthority() {
		return role;
	    }
	});
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
    	return this.status.equals(STATUS_ACTIVE);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
    	return this.status.equals(STATUS_ACTIVE);
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
    	return this.status.equals(STATUS_ACTIVE);
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
    	return this.status.equals(STATUS_ACTIVE);
    }

}
