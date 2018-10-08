package com.thedotin.springtraining.controller;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author valentin.raduti
 */
public class DAOProxyFactory {

    public static <D, T> D getProxy(Class<D> daoClass, EntityPersistenceMock<T> entityGenerator, long itemCount) {
	return (D) Proxy.newProxyInstance(DAOProxyFactory.class.getClassLoader(), new Class[]{daoClass}, new DAOProxy<D, T>(entityGenerator, itemCount));
    }

    private static class DAOProxy<D, T> implements InvocationHandler {

	private final EntityPersistenceMock<T> mock;
	private final long itemCount;

	public DAOProxy(EntityPersistenceMock<T> mock, long itemCount) {
	    this.mock = mock;
	    this.itemCount = itemCount;
	}

	@Override
	public Object invoke(Object o, Method method, Object[] os) throws Throwable {
	    if (method.getName().equals("findAll")
		    && Page.class.isAssignableFrom(method.getReturnType())
		    && method.getParameterTypes().length == 1
		    && Pageable.class.isAssignableFrom(method.getParameterTypes()[0])) {
		Pageable page = (Pageable) os[0];
		int totalPageCount = (int) (itemCount / page.getPageSize());
		int pageNo = page.getPageNumber();
		int itemsPerPage = 0;
		if (pageNo < totalPageCount) {
		    itemsPerPage = page.getPageSize();
		}
		if (pageNo == totalPageCount) {
		    itemsPerPage = (int) (itemCount % page.getPageSize());
		}
		List<T> re = new LinkedList<>();
		for (int i = 0; i < itemsPerPage; i++) {
		    re.add(mock.build());
		}
		return new PageImpl<>(re, page, itemCount);
	    }

	    if (method.getName().equals("search")
		    && Page.class.isAssignableFrom(method.getReturnType())
		    && method.getParameterTypes().length == 2
		    && String.class.isAssignableFrom(method.getParameterTypes()[0])
		    && Pageable.class.isAssignableFrom(method.getParameterTypes()[1])) {
		Pageable page = (Pageable) os[1];
		int totalPageCount = (int) (itemCount / page.getPageSize());
		int pageNo = page.getPageNumber();
		int itemsPerPage = 0;
		if (pageNo < totalPageCount) {
		    itemsPerPage = page.getPageSize();
		}
		if (pageNo == totalPageCount) {
		    itemsPerPage = (int) (itemCount % page.getPageSize());
		}
		List<T> re = new LinkedList<>();
		for (int i = 0; i < itemsPerPage; i++) {
		    re.add(mock.build());
		}
		return new PageImpl<>(re, page, itemCount);
	    }

	    if (method.getName().equals("toString")
		    && String.class.isAssignableFrom(method.getReturnType())
		    && method.getParameterTypes().length == 0) {
		return "Mock proxy DAO implementation of " + mock.build().getClass().getSimpleName();
	    }
	    throw new UnsupportedOperationException("Mock class does not implement requested method: " + method.getName());
	}
    }

}
