package com.faos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.faos.service.CustomUserDetailsService;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	 @Autowired
	 private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	 
	 @Autowired
	 private AdminAuthenticationSuccessHandler adminAuthenticationSuccessHandler;
	 
	 
    @Bean
    UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("CUSTOMER")
                .anyRequest().permitAll())
            
            .formLogin(form -> form
                    .loginPage("/login") // User login page
                    .loginProcessingUrl("/dologin") // Process login at this URL
                    .usernameParameter("consumerId")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/user/home", true)
                    .failureUrl("/login-error?error=true")
                    .successHandler((request, response, authentication) -> {
                        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                            adminAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                        } else {
                            customAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                        }
                    })
                    .permitAll())
            	
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll());

        return http.build();
    }


    
    @Bean
     PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
