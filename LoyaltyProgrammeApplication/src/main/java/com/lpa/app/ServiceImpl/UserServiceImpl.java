package com.lpa.app.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lpa.app.Entity.User;
import com.lpa.app.Entity.Service.UserService;
import com.lpa.app.Repo.UserRepo;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepository;

	@Override
	public User createUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public User updateUser(User user, Integer userId) {
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isPresent()) {
			User existingUser = optionalUser.get();
			existingUser.setEmail(user.getEmail());
			existingUser.setUsername(user.getUsername());
			return userRepository.save(existingUser);
		} else {
			// Handle user not found scenario
			throw new RuntimeException("User not found with id " + userId);
		}
	}

	@Override
	public void DeleteUser(Integer userId) {
		userRepository.deleteById(userId);
	}

	@Override
	public User getUserbyId(Integer userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id " + userId));
	}

	@Override
	public List<User> getallUsers() {
		return userRepository.findAll();
	}
}
