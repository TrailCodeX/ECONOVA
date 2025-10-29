package com.econova.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import com.econova.entity.User;
import com.econova.service.UserService;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	// Show login page
	@GetMapping("/login")
	public String showLogin() {
		return "login";
	}

	// Show registration page
	@GetMapping("/register")
	public String showRegister() {
		return "register";
	}

	// Process login
	@PostMapping("/login")
	public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
		User user = userService.login(email, password);

		if (user != null) {
			session.setAttribute("currentUser", user);
			if (user.getRole() == User.Role.ADMIN) {
				return "redirect:/admin/home";
			} else {
				return "redirect:/customerhome";
			}
		} else {
			model.addAttribute("error", "Invalid email or password!");
			return "login";
		}
	}

	// Process registration
	@PostMapping("/register")
	public String registerUser(
			@RequestParam String firstName,
			@RequestParam String lastName,
			@RequestParam String email,
			@RequestParam String password,
			@RequestParam String confirmPassword,
			Model model) {

		// Validate passwords match
		if (!password.equals(confirmPassword)) {
			model.addAttribute("error", "Passwords do not match!");
			return "register";
		}

		// Validate password length
		if (password.length() < 6) {
			model.addAttribute("error", "Password must be at least 6 characters long!");
			return "register";
		}

		// Check if email already exists
		if (userService.findByEmail(email) != null) {
			model.addAttribute("error", "Email is already registered!");
			return "register";
		}

		try {
			// Create new user
			User newUser = new User();
			newUser.setFirstName(firstName);
			newUser.setLastName(lastName);
			newUser.setEmail(email);
			newUser.setPassword(password);
			newUser.setRole(User.Role.CUSTOMER);

			userService.registerUser(newUser);

			model.addAttribute("success", "Registration successful! Please login.");
			return "register";

		} catch (Exception e) {
			model.addAttribute("error", "Registration failed: " + e.getMessage());
			return "register";
		}
	}

	// Logout
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}