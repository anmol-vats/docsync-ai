package com.docsync.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectRepositoryRequest {
//    private Long githubRepositoryId;
    private String owner;
    private String repo;
}

