package com.docsync.integration.github;

import java.util.Optional;

public interface GitHubClient {
    Optional<GitHubRepositoryResponse> getRepository(String owner, String repo);
}
