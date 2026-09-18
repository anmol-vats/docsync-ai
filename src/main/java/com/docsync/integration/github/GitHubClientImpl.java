package com.docsync.integration.github;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class GitHubClientImpl implements GitHubClient {
    private final RestClient restClient;
    public GitHubClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Optional<GitHubRepositoryResponse> getRepository(String owner, String repo) {
        GitHubRepositoryResponse response = restClient
                .get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .body(GitHubRepositoryResponse.class);

        return Optional.ofNullable(response);
    }
}
