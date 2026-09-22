package com.docsync.webhook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubWebhookPayload {
    private GitHubRepositoryPayload repository;
}
