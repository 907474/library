package com.aw.librarysystem.repository;

import com.aw.librarysystem.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer>, JpaSpecificationExecutor<Member> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmail(String email);
}