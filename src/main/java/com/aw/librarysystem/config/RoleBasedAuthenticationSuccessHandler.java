package com.aw.librarysystem.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import java.io.IOException;

public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultSavedRequest savedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedRequest != null) {
            response.sendRedirect(savedRequest.getRedirectUrl());
            return;
        }
        boolean isUser = false;
        boolean isAdminOrStaff = false;

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String authority = auth.getAuthority();
            if ("ROLE_USER".equals(authority)) {
                isUser = true;
                break;
            }
            if ("ROLE_ADMIN".equals(authority) || "ROLE_STAFF".equals(authority)) {
                isAdminOrStaff = true;
                break;
            }
        }

        if (isAdminOrStaff) {
            response.sendRedirect("/admin-dashboard");
        } else if (isUser) {
            response.sendRedirect("/dashboard");
        } else {
            response.sendRedirect("/");
        }
    }
}