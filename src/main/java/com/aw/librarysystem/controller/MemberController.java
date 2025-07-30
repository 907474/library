package com.aw.librarysystem.controller;

import com.aw.librarysystem.entity.Member;
import com.aw.librarysystem.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String listMembers(Model model) {
        model.addAttribute("members", memberService.findAllMembers());
        return "members/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new Member());
        return "members/form";
    }

    @PostMapping
    public String saveMember(@ModelAttribute("member") Member member) {
        memberService.saveMember(member);
        return "redirect:/members";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Member member = memberService.findMemberById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + id));
        model.addAttribute("member", member);
        return "members/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteMember(@PathVariable("id") Integer id) {
        memberService.deleteMemberById(id);
        return "redirect:/members";
    }
}