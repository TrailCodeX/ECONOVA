package com.econova.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.econova.entity.Role;
import com.econova.entity.User;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmailAndPassword(String email, String password);
	 Optional<User> findByEmail(String email);

	 long countByRole(User.Role role);

}
