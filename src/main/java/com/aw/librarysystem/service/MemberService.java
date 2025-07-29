package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.Member;
import com.aw.librarysystem.entity.enums.MemberStatus;
import com.aw.librarysystem.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findMemberById(Integer id) {
        return memberRepository.findById(id);
    }

    @Transactional
    public Member saveMember(Member member) {
        if (member.getId() == null) {
            memberRepository.findByUsername(member.getUsername()).ifPresent(m -> {
                throw new IllegalStateException("Username already exists: " + member.getUsername());
            });
            memberRepository.findByEmail(member.getEmail()).ifPresent(m -> {
                throw new IllegalStateException("Email already exists: ".concat(member.getEmail()));
            });
            member.setRegistrationDate(LocalDate.now());
        }
        return memberRepository.save(member);
    }

    public void deleteMemberById(Integer id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalStateException("Member not found with ID: " + id);
        }
        memberRepository.deleteById(id);
    }

    @Transactional
    public void updateMemberStatus(Integer memberId, MemberStatus newStatus) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Member not found with ID: " + memberId));
        member.setStatus(newStatus);
        memberRepository.save(member);
    }
}