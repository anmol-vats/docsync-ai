package com.docsync.integration.dto;

public record GitHubCreatePullRequestRequest(
        String title,
        String head,
        String base,
        String body
) {}
