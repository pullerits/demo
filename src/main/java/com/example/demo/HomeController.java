package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session) {
        // Check if a user is logged in by verifying the session attribute
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "home"; // Renders src/main/resources/templates/home.html
    }
}
