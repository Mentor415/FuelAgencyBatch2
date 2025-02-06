package com.faos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.faos.service.LoginService;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	@Autowired
    private  LoginService loginService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long consumerId = Long.valueOf(userDetails.getUsername());
        String redirectUrl = loginService.status(consumerId) 
            ? (loginService.isFirstLogin(consumerId, userDetails.getPassword()) ? "/forget/password" : "/user/home") 
            : "/customer/deactived";
        response.sendRedirect(redirectUrl);
    }
}

