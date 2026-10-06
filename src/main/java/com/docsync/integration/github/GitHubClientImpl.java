package com.docsync.integration.github;

import com.docsync.integration.dto.GitHubCommitResponse;
import com.docsync.integration.dto.GitHubTreeItemResponse;
import com.docsync.integration.dto.GitHubTreeResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
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

    @Override
    public Optional<GitHubCommitResponse> getCommit(
            String owner,
            String repo,
            String commitSha
    ) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/commits/{commitSha}",
                        owner,
                        repo,
                        commitSha)
                .exchange((request, response) -> {
                    if(response.getStatusCode().value() == 404) {
                        return Optional.empty();
                    }else{
                        return Optional.of(response.bodyTo(GitHubCommitResponse.class));
                    }
                });
    }

    @Override
    public List<String> getRepositoryFiles(
            String owner,
            String repo,
            String commitSha
    ) {
        GitHubTreeResponse response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/git/trees/{commitSha}")
                        .queryParam("recursive", "1")
                        .build(owner, repo, commitSha))
                .retrieve()
                .body(GitHubTreeResponse.class);

        if (response == null || response.getTree() == null) {
            return List.of();
        }

        return response.getTree().stream()
                .filter(item -> "blob".equals(item.getType()))
                .map(GitHubTreeItemResponse::getPath)
                .toList();
    }
}
