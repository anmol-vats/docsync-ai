package com.docsync.service;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptPasswordHasher implements PasswordHasher {
    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordHasher(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }
    @Override
    public String hash(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
