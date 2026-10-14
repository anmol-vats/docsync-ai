package com.docsync.integration.dto;

public record GitHubCreateRefRequest(
        String ref,
        String sha
) {}
