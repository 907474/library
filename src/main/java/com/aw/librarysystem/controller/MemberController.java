package com.aw.librarysystem.controller;

import com.aw.librarysystem.entity.Member;
import com.aw.librarysystem.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String listMembers(@RequestParam(required = false) String name,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String email,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Member> memberPage;

        boolean isSearching = (name != null && !name.isEmpty()) ||
                (username != null && !username.isEmpty()) ||
                (email != null && !email.isEmpty());

        if (isSearching) {
            memberPage = memberService.searchMembers(name, username, email, pageable);
        } else {
            memberPage = memberService.findAllMembers(pageable);
        }

        model.addAttribute("memberPage", memberPage);
        model.addAttribute("name", name);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
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