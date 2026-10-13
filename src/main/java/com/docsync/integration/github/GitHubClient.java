package com.docsync.integration.github;

import com.docsync.integration.dto.GitHubCommitResponse;

import java.util.List;
import java.util.Optional;

public interface GitHubClient {
    Optional<GitHubRepositoryResponse> getRepository(String owner, String repo);
    Optional<GitHubCommitResponse> getCommit(String owner, String repo, String commitSha);
    List<String> getRepositoryFiles(
            String owner,
            String repo,
            String commitSha
    );
    String getFileContent(
            String owner,
            String repo,
            String path,
            String commitSha
    );
    Optional<String> getDefaultBranchHeadSha(
            String owner,
            String repo
    );
}
