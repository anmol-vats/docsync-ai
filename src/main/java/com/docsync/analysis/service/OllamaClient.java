package com.docsync.analysis.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OllamaClient {

    private final RestClient restClient;

    public OllamaClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generate(String prompt) {

        Map<String, Object> request = Map.of(
                "model", "qwen2.5:3b",
                "prompt", prompt,
                "stream", false
        );

        GenerateResponse response = restClient.post()
                .uri("/api/generate")
                .body(request)
                .retrieve()
                .body(GenerateResponse.class);

        if (response == null || response.response() == null) {
            throw new IllegalStateException(
                    "Ollama returned an empty response"
            );
        }

        return response.response();
    }

    public record GenerateResponse(
            String response,
            boolean done
    ) {}
}
