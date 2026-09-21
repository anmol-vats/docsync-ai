package com.docsync.webhook.service;

import com.docsync.exception.RepositoryNotConnectedException;
import com.docsync.repositorymanagement.repository.RepositoryRepository;
import com.docsync.webhook.dto.GitHubWebhookRequest;
import com.docsync.webhook.dto.WebhookResponse;
import com.docsync.webhook.entity.WebhookEvent;
import com.docsync.webhook.entity.WebhookEventStatus;
import com.docsync.webhook.repository.WebhookEventRepository;
import com.docsync.repositorymanagement.entity.Repository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class WebhookService {
    private final WebhookEventRepository webhookEventRepository;
    private final RepositoryRepository repositoryRepository;

    public WebhookService(WebhookEventRepository webhookEventRepository, RepositoryRepository repositoryRepository) {
        this.webhookEventRepository = webhookEventRepository;
        this.repositoryRepository = repositoryRepository;
    }

    public WebhookResponse handleWebhook(GitHubWebhookRequest request){
        if(webhookEventRepository.existsByDeliveryId(request.getDeliveryId())){
            WebhookResponse webhookResponse = new WebhookResponse();
            webhookResponse.setStatus("DUPLICATE");
            webhookResponse.setMessage("Webhook already processed");
            return webhookResponse;
        }

        Repository repository = repositoryRepository
                .findByGithubRepositoryId(request.getGithubRepositoryId())
                .orElseThrow(() ->
                        new RepositoryNotConnectedException(
                                "Repository is not connected to DocSync"
                        ));

        WebhookEvent webhookEvent = new WebhookEvent(
                UUID.randomUUID(),
                request.getDeliveryId(),
                request.getEventType(),
                repository,
                request.getPayload(),
                Instant.now(),
                WebhookEventStatus.RECEIVED
        );
        webhookEventRepository.save(webhookEvent);
        WebhookResponse webhookResponse = new WebhookResponse();
        webhookResponse.setStatus("SUCCESS");
        webhookResponse.setMessage("Webhook received successfully");
        return webhookResponse;
    }
}
