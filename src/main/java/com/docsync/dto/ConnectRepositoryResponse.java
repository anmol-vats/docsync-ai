package com.docsync.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConnectRepositoryResponse {
    private UUID repositoryId;
    private String name;
    private String url;
    private String defaultBranch;
    private String message;
}
