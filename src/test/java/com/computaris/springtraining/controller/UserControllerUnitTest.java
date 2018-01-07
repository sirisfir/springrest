package com.computaris.springtraining.controller;

import com.computaris.springtraining.domain.User;
import com.computaris.springtraining.repository.UserRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author valentin.raduti
 */
public class UserControllerUnitTest {

    private final UserController mockController = new UserController(DAOProxyFactory.getProxy(UserRepository.class, new EntityPersistenceMock<User>() {
	private final Random r = new Random();

	@Override
	public User build() {
	    User u = new User();
	    int x = r.nextInt();
	    u.setUsername("User " + x);
	    u.setId(x);
	    return u;
	}

	@Override
	public User build(Object... params) {
	    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<User> getItems() {
	    List<User> re = new LinkedList<>();
	    for (int i = 0; i < 10; i++) {
		re.add(build());
	    }
	    return re;
	}

	@Override
	public void put(List<User> item) {

	}

	@Override
	public void put(List<User> item, Object... params) {
	    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void remove(List<User> item) {

	}

	@Override
	public void remove(List<User> item, Object... params) {

	}
    }, 20));

    @Test
    public void testGetUsers() {
	Page<User> re = this.mockController.getUsers(new PageRequest(0, 10),"");
	Assert.assertNotNull(re);
	Assert.assertEquals(re.getContent().size(), 10);
    }
}
