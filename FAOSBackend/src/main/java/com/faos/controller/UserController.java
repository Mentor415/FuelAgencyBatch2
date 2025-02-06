package com.faos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

	@GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("username", "Arjun Kumar Pandey");
        model.addAttribute("message", "This is your personalized homepage!");
        return "Home";
    }
}
