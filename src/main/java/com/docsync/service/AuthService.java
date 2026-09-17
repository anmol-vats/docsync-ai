package com.docsync.service;

import com.docsync.dto.LoginRequest;
import com.docsync.dto.LoginResponse;
import com.docsync.entity.User;
import com.docsync.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
    }
}
