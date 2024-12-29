package com.example.HyphaemaProgressTracker.securityConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This class contains the security configuration for the Spring Boot application.
 * It configures HTTP security settings for authentication, authorization, login, logout, 
 * and password encoding using Spring Security.
 */
@Configuration
public class SecurityConfig {
    
    /**
     * Configures the HTTP security filter chain.
     * This method sets up various security policies such as CSRF protection, authorization rules,
     * login and logout settings, and more.
     *
     * @param http the HttpSecurity object used to configure the security settings.
     * @return the configured SecurityFilterChain.
     * @throws Exception if there is an error in configuring the security settings.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disables CSRF protection (Cross-Site Request Forgery) for simplicity.
        http
            .csrf(csrf -> csrf.disable()) // CSRF protection disabled.
            // Configures the authorization rules for different HTTP requests
            .authorizeHttpRequests(auth -> auth
            // Allows unauthenticated access to specific paths (e.g., home, registration, static resources)
            .requestMatchers("/", "/register", "/css/**", "/js/**", "/images/**", "/illustrations/**").permitAll()
                .anyRequest().authenticated() // All other URLs require user to be logged in
            )
            // Configures the login page and login behavior
            .formLogin(login -> login
                .loginPage("/") // Defines the root URL ("/") as the login page
                .loginProcessingUrl("/login") // URL used to process login requests
                .defaultSuccessUrl("/dashboard", true) // Redirects to the dashboard after a successful login
                .failureUrl("/?error=true") // Redirects back to the home page with an error parameter if login fails
                .permitAll() // Allows everyone to access the login page
            )
            // Configures logout behavior
            .logout(logout -> logout
                .logoutUrl("/logout") // Defines the URL for logging out
                .logoutSuccessUrl("/?logout=true") // Redirects to the home page with a logout parameter after a successful logout
                .permitAll() // Allows everyone to log out
            )
            .anonymous(anon -> anon.disable()); // Disables anonymous access, only authenticated users are allowed

        return http.build();
    }

    /**
     * Provides a BCryptPasswordEncoder bean for password encoding.
     * @return a BCryptPasswordEncoder instance.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

