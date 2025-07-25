package com.aw.librarysystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserDashboardController {

    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        return "user/dashboard";
    }
}