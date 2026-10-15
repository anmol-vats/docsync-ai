package com.docsync.integration.dto;

public record GitHubUpdateFileRequest(
        String message,
        String content,
        String branch,
        String sha
) {}
