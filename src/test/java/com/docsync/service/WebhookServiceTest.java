package com.docsync.service;

import com.docsync.repositorymanagement.entity.Repository;
import com.docsync.repositorymanagement.repository.RepositoryRepository;
import com.docsync.webhook.dto.GitHubWebhookRequest;
import com.docsync.webhook.dto.WebhookResponse;
import com.docsync.webhook.entity.WebhookEvent;
import com.docsync.webhook.repository.WebhookEventRepository;
import com.docsync.webhook.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebhookServiceTest {
    private WebhookService webhookService;
    @BeforeEach
    public void setUp() {
        webhookService = new WebhookService(
                webhookEventRepository,
                repositoryRepository
        );
    }

    @Mock
    private WebhookEventRepository  webhookEventRepository;

    @Mock
    private RepositoryRepository  repositoryRepository;

    @Test
    void shouldHandleNewWebhookSuccessfully(){
        GitHubWebhookRequest request = new GitHubWebhookRequest();
        request.setDeliveryId("123456");
        request.setEventType("push");
        request.setGithubRepositoryId(123456L);
        request.setPayload("{\"test\":\"payload\"}");

        when(webhookEventRepository.existsByDeliveryId(request.getDeliveryId()))
                .thenReturn(false);

        Repository repository = new Repository(
                UUID.randomUUID(),
                request.getGithubRepositoryId(),
                "Hello-World",
                "https://github.com/octocat/Hello-World",
                "master",
                Instant.now()
        );

        when(repositoryRepository.findByGithubRepositoryId(request.getGithubRepositoryId()))
                .thenReturn(Optional.of(repository));

        WebhookResponse result = webhookService.handleWebhook(request);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("Webhook received successfully", result.getMessage());
        verify(webhookEventRepository)
                .save(any(WebhookEvent.class));

    }

    @Test
    void shouldReturnDuplicateWhenWebhookAlreadyExists(){
        GitHubWebhookRequest request = new GitHubWebhookRequest();
        request.setDeliveryId("123456");
        request.setEventType("push");
        request.setGithubRepositoryId(123456L);
        request.setPayload("{\"test\":\"payload\"}");

        when(webhookEventRepository.existsByDeliveryId(request.getDeliveryId()))
                .thenReturn(true);

        WebhookResponse result = webhookService.handleWebhook(request);
        assertEquals("DUPLICATE", result.getStatus());
        assertEquals("Webhook already processed", result.getMessage());
        verify(webhookEventRepository, never()).save(any(WebhookEvent.class));
    }
}
