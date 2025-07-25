package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.SystemAccount;
import com.aw.librarysystem.repository.SystemAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class LibraryUserDetailsService implements UserDetailsService {

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemAccount account = systemAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new User(
                account.getUsername(),
                account.getPassword(),
                account.isEnabled(), // 👈 Pass the enabled status
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
        );
    }
}