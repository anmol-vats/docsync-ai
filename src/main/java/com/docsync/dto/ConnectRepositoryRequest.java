package com.docsync.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectRepositoryRequest {
    @NotBlank
    private String owner;

    @NotBlank
    private String repo;
}

