package com.aw.librarysystem.controller;

import com.aw.librarysystem.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> payload) {
        String initiatorUsername = userDetails.getUsername();
        String targetUsername = payload.get("username");
        String newPassword = payload.get("newPassword");

        try {
            accountService.changePassword(initiatorUsername, targetUsername, newPassword);
            return ResponseEntity.ok().body("Password changed successfully for " + targetUsername);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/set-status")
    public ResponseEntity<?> setAccountStatus(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, Object> payload) {
        String initiatorUsername = userDetails.getUsername();
        String targetUsername = (String) payload.get("username");
        boolean isEnabled = (Boolean) payload.get("enabled");

        try {
            accountService.setAccountStatus(initiatorUsername, targetUsername, isEnabled);
            String status = isEnabled ? "enabled" : "disabled";
            return ResponseEntity.ok().body("Account for " + targetUsername + " has been " + status);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}