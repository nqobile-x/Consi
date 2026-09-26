package com.storetemplate.store.service;

import com.storetemplate.store.model.AppUser;
import com.storetemplate.store.model.Role;
import com.storetemplate.store.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public boolean emailTaken(String email) {
        return users.existsByEmailIgnoreCase(email);
    }

    public AppUser register(String email, String rawPassword, String fullName) {
        AppUser u = new AppUser();
        u.setEmail(email.trim().toLowerCase());
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setFullName(fullName);
        u.setRole(Role.USER);
        return users.save(u);
    }

    public Optional<AppUser> findByEmail(String email) {
        return users.findByEmailIgnoreCase(email);
    }
}
