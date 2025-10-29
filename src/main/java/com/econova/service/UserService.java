package com.econova.service;

import com.econova.entity.User;


import java.util.Optional;

public interface UserService {
    User login(String email, String password);
    User findByEmail(String email);
    User registerUser(User user);
    // Find user by ID
    Optional<User> findById(Long id);
    long countUsers();
    User saveUser(User user);

}
