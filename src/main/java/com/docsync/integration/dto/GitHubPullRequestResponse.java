package com.docsync.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubPullRequestResponse(
        long number,

        @JsonProperty("html_url")
        String htmlUrl,

        String title,
        String state
) {}
