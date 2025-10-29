package com.econova.service;

import com.econova.entity.User;
import com.econova.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public long countUsers() {
		return userRepository.count();
	}

	@Override
	public User login(String email, String password) {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent() && user.get().getPassword().equals(password)) {
			return user.get();
		}
		return null;
	}

	@Override
	public User findByEmail(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		return user.orElse(null); // Returns null if not found, doesn't throw exception
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public User registerUser(User user) {
		// Set default role as CUSTOMER if not set
		if (user.getRole() == null) {
			user.setRole(User.Role.CUSTOMER);
		}
		return userRepository.save(user);
	}
}