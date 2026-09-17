package com.docsync.service;

import com.docsync.dto.LoginRequest;
import com.docsync.dto.LoginResponse;
import com.docsync.entity.User;
import com.docsync.exception.InvalidCredentialsException;
import com.docsync.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).
                orElseThrow(InvalidCredentialsException::new);

        boolean matched = passwordHasher.matches(loginRequest.getPassword(), user.getPasswordHash());
        if (!matched) {
            throw new InvalidCredentialsException();
        }
        String token = tokenService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse(
                token,
                "bearer"
        );
        return loginResponse;
    }
}
