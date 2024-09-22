package com.lpa.app.Entity.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lpa.app.Entity.User;

@Service
public interface UserService {
	User createUser(User user);

	User updateUser(User user, Integer userId);

	void DeleteUser(Integer userid);

	User getUserbyId(Integer userId);

	List<User> getallUsers();
}
