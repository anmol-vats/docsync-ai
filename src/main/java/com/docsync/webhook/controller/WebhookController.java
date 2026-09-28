package com.docsync.webhook.controller;

import com.docsync.exception.InvalidWebhookPayloadException;
import com.docsync.webhook.dto.GitHubWebhookPayload;
import com.docsync.webhook.dto.GitHubWebhookRequest;
import com.docsync.webhook.dto.WebhookResponse;
import com.docsync.webhook.service.WebhookService;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final ObjectMapper objectMapper;
    private final WebhookService webhookService;

    public WebhookController(ObjectMapper objectMapper, WebhookService webhookService) {
        this.objectMapper = objectMapper;
        this.webhookService = webhookService;
    }

    @PostMapping("/github")
    public WebhookResponse handleGitHubWebhook(
            @RequestHeader("X-GitHub-Delivery") String deliveryId,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody String rawPayload
    ) {
        GitHubWebhookPayload payload;
        try{
            payload = objectMapper.readValue(rawPayload, GitHubWebhookPayload.class);
        } catch(JacksonException e){
            throw new InvalidWebhookPayloadException("Invalid GitHub webhook payload");
        }

        if (payload.getRepository() == null ||
                payload.getRepository().getId() == null) {
            throw new InvalidWebhookPayloadException("Invalid GitHub webhook payload");
        }

        Long githubRepositoryId = payload.getRepository().getId();
        GitHubWebhookRequest request = new GitHubWebhookRequest();
        request.setGithubRepositoryId(githubRepositoryId);
        request.setDeliveryId(deliveryId);
        request.setPayload(rawPayload);
        request.setEventType(eventType);
        request.setCommitSha(payload.getAfter());
        WebhookResponse result = webhookService.handleWebhook(request);
        return result;
    }
}
