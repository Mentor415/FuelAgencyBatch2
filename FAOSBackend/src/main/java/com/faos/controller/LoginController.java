package com.faos.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.faos.service.LoginService;
import org.springframework.ui.Model;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	private String tempEmail;
	
	@GetMapping("/menu")
	public String home() {
		return "Menu";
	}

    @GetMapping("/login")
    public String showLoginPage() {
        return "LoginPage";
    }
    
    @GetMapping("/adminLogin")
	public String adminLogin() {
		return "AdminLogin";
	}


    @GetMapping("/login-error")
    public String error(Model model) {
        model.addAttribute("message", "Invalid Credentials. Please try again.");
        return "LoginPage";
    }
    
    @GetMapping("/login-AdminError")
    public String AdminError(Model model) {
        model.addAttribute("message", "Invalid Credentials. Please try again.");
        return "AdminLogin";
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage() {
        return "ChangePassword";
    }
 
    @GetMapping("/forget/password")
    public String forgetPasswordPage() {
        return "ForgotPassword";
    }
    
    @GetMapping("/customer/deactived")
    public String deActivatedUser() {
    	return "Deactivated";
    }
    
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody String email) {
        Map<String, String> response = new HashMap<>();
        tempEmail = email;
        if (email == null || !email.contains("@")) {
            response.put("message", "Invalid email address.");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            loginService.generateAndSendOtp(email);
            response.put("message", "Verification code sent successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while sending the OTP. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        Map<String, String> response = new HashMap<>();
        try {
            if (loginService.verifyOtp(email, otp)) {
                response.put("message", "OTP verified successfully! Redirecting...");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid OTP. Please try again.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("message", "An error occurred while verifying the OTP. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/resendOtp")
    public ResponseEntity<Map<String, String>> reSend(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            loginService.generateAndSendOtp(email);
            response.put("message", "Verification code sent successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while resending the OTP. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam Long consumerId, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match. Please try again.");
            return "redirect:/change-password";
        }
        try {
            String updatePasswordResult = loginService.updatePassword(consumerId, tempEmail, newPassword);
            if ("Password updated successfully".equals(updatePasswordResult)) {
                model.addAttribute("message", "Password updated successfully");
                return "redirect:/login"; 
            } else if ("Consumer ID or email does not match".equals(updatePasswordResult)) {
                model.addAttribute("message", "Consumer ID or email does not match");
            } else if ("Customer not found".equals(updatePasswordResult)) {
                model.addAttribute("message", "Customer not found! Please verify the Consumer ID.");
            }
        } catch (Exception e) {
            model.addAttribute("message", "An error occurred. Please try again later.");
        }
        return "redirect:/change-password";
    }
}




