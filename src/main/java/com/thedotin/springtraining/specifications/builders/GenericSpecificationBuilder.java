package com.thedotin.springtraining.specifications.builders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import com.thedotin.springtraining.specifications.GenericSpecification;
import com.thedotin.springtraining.specifications.SearchCriteria;

public class GenericSpecificationBuilder<T> {
	
	private final List<SearchCriteria> params;
	 
    public GenericSpecificationBuilder() {
        params = new ArrayList<SearchCriteria>();
    }
 
    public GenericSpecificationBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }
 
    public Specification<T> build() {
        if (params.size() == 0) {
            return null;
        }
 
        List<Specification<T>> specs = new ArrayList<Specification<T>>();
        for (SearchCriteria param : params) {
            specs.add(new GenericSpecification<T>(param));
        }
 
        Specification<T> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = Specifications.where(result).and(specs.get(i));
        }
        return result;
    }

}
