package com.docsync.controller;

import com.docsync.dto.ConnectRepositoryRequest;
import com.docsync.dto.ConnectRepositoryResponse;
import com.docsync.service.RepositoryConnectionService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RepositoryConnectionController {
    private final RepositoryConnectionService repositoryConnectionService;

    public RepositoryConnectionController(RepositoryConnectionService repositoryConnectionService) {
        this.repositoryConnectionService = repositoryConnectionService;
    }

    @PostMapping("/repositories/connect")
    public ConnectRepositoryResponse connectRepository(@Valid @RequestBody ConnectRepositoryRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return repositoryConnectionService.connectRepository(userId, request);
    }
}
