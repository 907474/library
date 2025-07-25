package com.aw.librarysystem.repository;

import com.aw.librarysystem.entity.SystemAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, Integer> {
    Optional<SystemAccount> findByUsername(String username);
}