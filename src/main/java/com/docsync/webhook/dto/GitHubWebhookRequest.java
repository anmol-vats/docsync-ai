package com.docsync.webhook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubWebhookRequest {
    private String deliveryId;
    private String eventType;
    private Long githubRepositoryId;
    private String payload;
}
