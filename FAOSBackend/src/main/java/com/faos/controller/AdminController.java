package com.faos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("username", "Admin Logined");
        model.addAttribute("message", "This is your personalized homepage!");
        return "Home";
    }
	
	
}
