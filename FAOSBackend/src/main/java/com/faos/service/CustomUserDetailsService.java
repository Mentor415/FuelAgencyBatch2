package com.faos.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.faos.config.CustomUserDetails;
import com.faos.model.Login;
import com.faos.repository.LoginRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
    private LoginRepository loginRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Optional<Login> user = loginRepository.findById(Long.valueOf(username));
            if (!user.isPresent()) {
                throw new UsernameNotFoundException("User not found with ID: " + username);
            }
            return new CustomUserDetails(user.get());
        } catch (Exception e) {
            throw new UsernameNotFoundException("Error fetching user: " + e.getMessage());
        }
    }
}

