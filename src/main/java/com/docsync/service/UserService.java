package com.docsync.service;

import com.docsync.dto.CreateUserRequest;
import com.docsync.dto.CreateUserResponse;
import com.docsync.entity.User;
import com.docsync.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserService(UserRepository userRepository,
                       PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        String passwordHash = passwordHasher.hash(request.getPassword());
        User user = new User(
                UUID.randomUUID(),
                request.getEmail(),
                passwordHash,
                false,
                Instant.now());
        userRepository.save(user);

        CreateUserResponse response = new CreateUserResponse(
                user.getId(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getCreatedAt());

        return response;
    }
}
