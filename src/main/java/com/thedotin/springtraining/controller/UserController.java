package com.computaris.springtraining.controller;

import com.computaris.springtraining.domain.User;
import com.computaris.springtraining.repository.UserRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author valentin.raduti
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserController(UserRepository userRepository) {
	this.userRepository = userRepository;
    }
    
    @GetMapping(produces = "application/json")
    public Page<User> getUsers(@PageableDefault(page = 0, size = 10, sort = {"fullName"}, direction = Sort.Direction.ASC) Pageable p,
	    @RequestParam(name = "search", defaultValue = "", required = false) String search) {
	return this.userRepository.search(search, p);
    }
    
    @GetMapping(value = "/me", produces = "application/json")
    public User me() {
	return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    @Transactional
    @PostMapping(consumes = "application/json")
    public User createUser(@RequestBody User user) {
	return this.userRepository.save(user);
    }
    
    @PutMapping(value = "/{userId}", consumes = "application/json")
    public User updateUser(@PathVariable("userId") Integer userId, @RequestBody User user) {
	User u = this.userRepository.findById(userId).orElse(null);
	if (u == null) {
	    u = user;
	} else {
	    u.setUsername(user.getUsername());
	    u.setFullName(user.getFullName());
	    u.setRole(user.getRole());
	    u.setStatus(user.getStatus());
	}
	return this.userRepository.save(u);
    }
    
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Integer userId) {
	this.userRepository.deleteById(userId);
    }
    
}
