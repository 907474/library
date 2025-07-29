package com.aw.librarysystem.controller;

import com.aw.librarysystem.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(Model model) {
        model.addAttribute("stats", dashboardService.getDashboardStats());
        return "admin/dashboard";
    }
}