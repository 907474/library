package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.SystemAccount;
import com.aw.librarysystem.entity.enums.Role;
import com.aw.librarysystem.repository.SystemAccountRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final SystemAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(SystemAccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<SystemAccount> findAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public SystemAccount createAccount(SystemAccount newAccount) {
        accountRepository.findByUsername(newAccount.getUsername()).ifPresent(acc -> {
            throw new IllegalStateException("Username already exists: " + newAccount.getUsername());
        });
        newAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));
        newAccount.setEnabled(true);
        return accountRepository.save(newAccount);
    }

    public void changePassword(String initiatorUsername, String targetUsername, String newPassword) {
        SystemAccount initiator = findByUsername(initiatorUsername);
        SystemAccount target = findByUsername(targetUsername);

        if (!canChangePassword(initiator, target)) {
            throw new SecurityException("You do not have permission to change this user's password.");
        }

        target.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(target);
    }

    public void setAccountStatus(String initiatorUsername, String targetUsername, boolean isEnabled) {
        SystemAccount initiator = findByUsername(initiatorUsername);
        SystemAccount target = findByUsername(targetUsername);

        if (initiator.getRole() != Role.ADMIN) {
            throw new SecurityException("You must be an admin to change account status.");
        }

        target.setEnabled(isEnabled);
        accountRepository.save(target);
    }

    private boolean canChangePassword(SystemAccount initiator, SystemAccount target) {
        if (initiator.getId().equals(target.getId())) {
            return true;
        }

        if (initiator.getRole() == Role.ADMIN) {
            return true;
        }

        if (initiator.getRole() == Role.STAFF && target.getRole() == Role.USER) {
            return true;
        }

        return false;
    }

    private SystemAccount findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}