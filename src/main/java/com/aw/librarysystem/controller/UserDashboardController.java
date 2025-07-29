package com.aw.librarysystem.controller;

import com.aw.librarysystem.entity.BorrowingRecord;
import com.aw.librarysystem.entity.Member;
import com.aw.librarysystem.repository.MemberRepository;
import com.aw.librarysystem.service.BorrowingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class UserDashboardController {

    private final BorrowingService borrowingService;
    private final MemberRepository memberRepository;

    public UserDashboardController(BorrowingService borrowingService, MemberRepository memberRepository) {
        this.borrowingService = borrowingService;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/dashboard")
    public String userDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Find the Member entity corresponding to the logged-in SystemAccount username.
        // This assumes the member's username matches the system account's username.
        memberRepository.findByUsername(userDetails.getUsername()).ifPresentOrElse(
                member -> {
                    // If the member is found, get their borrowing history.
                    List<BorrowingRecord> history = borrowingService.getMemberHistory(member.getId());
                    model.addAttribute("borrowingHistory", history);
                },
                () -> {
                    // If no matching member is found, provide an empty list.
                    model.addAttribute("borrowingHistory", Collections.emptyList());
                }
        );

        return "user/dashboard";
    }
}