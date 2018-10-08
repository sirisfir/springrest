package com.thedotin.springtraining.controller;

import java.util.List;

/**
 *
 * @author valentin.raduti
 */
public interface EntityPersistenceMock<T> {

	T build();

	T build(Object... params);

	List<T> getItems();

	void put(List<T> item);

	void put(List<T> item, Object... params);

	void remove(List<T> item);

	void remove(List<T> item, Object... params);
}
