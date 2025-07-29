package com.aw.librarysystem.controller;

import com.aw.librarysystem.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AccountService accountService;

    public AdminController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public String manageAccounts(Model model) {
        model.addAttribute("accounts", accountService.findAllAccounts());
        return "admin/accounts";
    }
}