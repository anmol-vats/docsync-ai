package com.docsync.webhook.controller;
import com.docsync.exception.InvalidWebhookPayloadException;
import com.docsync.webhook.service.WebhookService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.docsync.webhook.dto.GitHubWebhookRequest;
import com.docsync.webhook.dto.WebhookResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.verify;

@WebMvcTest(WebhookController.class)
public class WebhookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookService webhookService;

    @Test
    void shouldReturnBadRequestForInvalidJson() throws Exception {
        mockMvc.perform(
                        post("/webhooks/github")
                                .header("X-GitHub-Delivery", "123")
                                .header("X-GitHub-Event", "push")
                                .content("{\"repository\":")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRepositoryIsMissing() throws Exception {
        mockMvc.perform(
                post("/webhooks/github")
                        .header("X-GitHub-Delivery", "123")
                        .header("X-GitHub-Event", "push")
                        .content("{\"something\": \"else\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRepositoryIdIsMissing() throws Exception {
        mockMvc.perform(
                post("/webhooks/github")
                        .header("X-GitHub-Delivery", "123")
                        .header("X-GitHub-Event", "push")
                        .content("{\"repository\": {}}")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleValidWebhookSuccessfully() throws Exception {

        WebhookResponse response = new WebhookResponse();
        response.setStatus("SUCCESS");
        response.setMessage("Webhook received successfully");

        when(webhookService.handleWebhook(any(GitHubWebhookRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/webhooks/github")
                                .header("X-GitHub-Delivery", "123")
                                .header("X-GitHub-Event", "push")
                                .content("""
                            {
                              "repository": {
                                "id": 123456
                              }
                            }
                            """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Webhook received successfully"));

        ArgumentCaptor<GitHubWebhookRequest> captor =
                ArgumentCaptor.forClass(GitHubWebhookRequest.class);

        verify(webhookService).handleWebhook(captor.capture());

        GitHubWebhookRequest capturedRequest = captor.getValue();
        assertEquals("123", capturedRequest.getDeliveryId());
        assertEquals("push", capturedRequest.getEventType());
        assertEquals(123456L, capturedRequest.getGithubRepositoryId());

        assertEquals(
                """
                {
                  "repository": {
                    "id": 123456
                  }
                }
                """,
                capturedRequest.getPayload()
        );
    }
}
