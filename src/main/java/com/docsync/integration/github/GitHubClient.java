package com.docsync.integration.github;

import com.docsync.integration.dto.GitHubCommitResponse;

import java.util.Optional;

public interface GitHubClient {
    Optional<GitHubRepositoryResponse> getRepository(String owner, String repo);
    Optional<GitHubCommitResponse> getCommit(String owner, String repo, String commitSha);
}
