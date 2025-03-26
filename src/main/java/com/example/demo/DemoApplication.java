package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

class User {
	private Long id;
	private String username;
	private String password; // In production, use encrypted passwords!

	// Constructor
	public User(Long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

@Controller
class HomeController {

	@GetMapping("/home")
	public String home(HttpSession session) {
		// Check if a user is logged in by verifying the session attribute
		if (session.getAttribute("loggedInUser") == null) {
			return "redirect:/login";
		}
		return "home"; // Renders src/main/resources/templates/home.html
	}
}

@Controller
class LoginController {

	@Autowired
	private UserService userService;

	@GetMapping("/login")
	public String showLoginPage(@RequestParam(required = false) String error, Model model) {
		if (error != null) {
			model.addAttribute("errorMsg", "Invalid username or password");
		}
		return "login"; // Renders src/main/resources/templates/login.html
	}

	@PostMapping("/login")
	public String processLogin(@RequestParam String username,
							   @RequestParam String password,
							   HttpSession session) {
		// Retrieve the user using the UserService
		User user = userService.findByUsername(username).orElse(null);
		if (user != null && user.getPassword().equals(password)) {
			// Store the user in the session to mark them as logged in
			session.setAttribute("loggedInUser", user);
			return "redirect:/home";
		}
		// Invalid login; redirect back to login page with an error flag
		return "redirect:/login?error=true";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}


@Service
class UserService {
	private final List<User> users = new ArrayList<>();

	public UserService() {
		// Hardcode a single user for demonstration purposes
		users.add(new User(1L, "user", "password"));
	}

	public void registerUser(String username, String password) {
		long newId = users.size() + 1;
		users.add(new User(newId, username, password));
	}

	public Optional<User> findByUsername(String username) {
		return users.stream()
				.filter(u -> u.getUsername().equals(username))
				.findFirst();
	}
}

@Controller
class RegisterController {

	@Autowired
	private UserService userService;

	@GetMapping("/register")
	public String showRegisterPage(Model model) {
		return "register";
	}

	@PostMapping("/register")
	public String processRegister(@RequestParam String username,@RequestParam String password, Model model) {

		if (userService.findByUsername(username).isPresent()) {
			model.addAttribute("errorMsg", "Username is already in use");
			return "register";
		}

		userService.registerUser(username, password);
		return "redirect:/login";
	}
}


