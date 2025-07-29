package com.aw.librarysystem.controller;

import com.aw.librarysystem.service.AccountService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
public class UserAccountController {

    private final AccountService accountService;

    public UserAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "account/change-password-form";
    }

    @PostMapping("/change-password")
    public String processChangePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: New passwords do not match.");
            return "redirect:/account/change-password";
        }

        try {
            // The service logic already ensures a user can change their own password
            accountService.changePassword(userDetails.getUsername(), userDetails.getUsername(), newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return "redirect:/account/change-password";
    }
}