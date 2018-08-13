package com.thedotin.springtraining.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thedotin.springtraining.domain.User;

/**
 *
 * @author valentin.raduti
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    public User findOneByUsername(String email);

    @Query(value = "select u from User u where u.fullName like %:search% or u.username like %:search%",
	    countQuery = "select count (*) from User u where u.fullName like %:search% or u.username like %:search%")
    public Page<User> search(@Param("search") String search, Pageable pageable);

}
