package com.aw.librarysystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Rule 1: Allow public access to login and static resources
                        .requestMatchers("/login", "/css/**", "/js/**", "/error").permitAll()
                        // Rule 2: Only STAFF and ADMIN can access management pages
                        .requestMatchers("/books/**", "/members/**").hasAnyRole("STAFF", "ADMIN")
                        // Rule 3: Any authenticated user can access the dashboard (we'll make it the user's homepage)
                        .requestMatchers("/dashboard").authenticated()
                        // Rule 4: The root path is accessible to any logged-in user
                        .requestMatchers("/").authenticated()
                        .anyRequest().denyAll() // Deny any other request by default for security
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(roleBasedAuthenticationSuccessHandler()) // Use our custom success handler
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                );
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                // Check if the user tried to access a specific page before logging in
                DefaultSavedRequest savedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                if (savedRequest != null) {
                    response.sendRedirect(savedRequest.getRedirectUrl());
                    return;
                }

                // Redirect user based on their role
                boolean isUser = false;
                boolean isAdminOrStaff = false;

                for (GrantedAuthority auth : authentication.getAuthorities()) {
                    if ("ROLE_USER".equals(auth.getAuthority())) {
                        isUser = true;
                        break;
                    }
                    if ("ROLE_ADMIN".equals(auth.getAuthority()) || "ROLE_STAFF".equals(auth.getAuthority())) {
                        isAdminOrStaff = true;
                        break;
                    }
                }

                if (isAdminOrStaff) {
                    response.sendRedirect("/books");
                } else if (isUser) {
                    response.sendRedirect("/dashboard");
                } else {
                    response.sendRedirect("/");
                }
            }
        };
    }
}