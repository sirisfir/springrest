package com.thedotin.springtraining.specifications;

import org.springframework.data.jpa.domain.Specification;

public class UserSpecification<User> extends GenericSpecification<User> implements Specification<User> {

	public UserSpecification(SearchCriteria param) {
		super(param);
	}
}
