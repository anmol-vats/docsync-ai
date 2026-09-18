package com.docsync.integration.github;

import org.springframework.http.HttpStatus;
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
        return restClient
                .get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .exchange((request, response) -> {
                    if(response.getStatusCode().value() == 404) {
                        return Optional.empty();
                    }else{
                        return Optional.of(response.bodyTo(GitHubRepositoryResponse.class));
                    }
                });
    }
}
