package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.Member;
import com.aw.librarysystem.entity.enums.MemberStatus;
import com.aw.librarysystem.repository.MemberRepository;
import com.aw.librarysystem.specification.MemberSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<Member> findAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Optional<Member> findMemberById(Integer id) {
        return memberRepository.findById(id);
    }

    public Page<Member> searchMembers(String name, String username, String email, Pageable pageable) {
        Specification<Member> spec = Specification.where(null);
        if (name != null && !name.isEmpty()) {
            spec = spec.and(MemberSpecification.hasName(name));
        }
        if (username != null && !username.isEmpty()) {
            spec = spec.and(MemberSpecification.hasUsername(username));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and(MemberSpecification.hasEmail(email));
        }
        return memberRepository.findAll(spec, pageable);
    }

    @Transactional
    public Member saveMember(Member member) {
        if (member.getId() == null) {
            memberRepository.findByUsername(member.getUsername()).ifPresent(m -> {
                throw new IllegalStateException("Username already exists: " + member.getUsername());
            });
            memberRepository.findByEmail(member.getEmail()).ifPresent(m -> {
                throw new IllegalStateException("Email already exists: " + member.getEmail());
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