package com.docsync.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter

public class CreateUserResponse {
    public CreateUserResponse(UUID id,
                              String email,
                              boolean emailVerified,
                              Instant createdAt) {
        this.id = id;
        this.email = email;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
    }
    private UUID id;
    private String email;
    private boolean emailVerified;
    private Instant createdAt;
}
