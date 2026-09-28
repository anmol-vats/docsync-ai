package com.docsync.integration.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GitHubCommitResponse {
    private String sha;
    private List<GitHubCommitFileResponse> files;
}
