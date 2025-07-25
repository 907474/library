package com.aw.librarysystem;

import com.aw.librarysystem.entity.SystemAccount;
import com.aw.librarysystem.entity.enums.Role;
import com.aw.librarysystem.repository.SystemAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SystemAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SystemAccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createAccountIfNotFound("admin", "admin", Role.ADMIN);
        createAccountIfNotFound("staff", "staff", Role.STAFF);
        createAccountIfNotFound("user", "user", Role.USER);
    }

    private void createAccountIfNotFound(String username, String password, Role role) {
        if (accountRepository.findByUsername(username).isEmpty()) {
            SystemAccount account = new SystemAccount();
            account.setUsername(username);
            account.setPassword(passwordEncoder.encode(password));
            account.setRole(role);
            account.setEnabled(true);
            accountRepository.save(account);
        }
    }
}