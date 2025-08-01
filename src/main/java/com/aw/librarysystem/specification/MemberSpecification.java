package com.aw.librarysystem.specification;

import com.aw.librarysystem.entity.Member;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class MemberSpecification {

    public static Specification<Member> hasName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Member> hasUsername(String username) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<Member> hasEmail(String email) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
}