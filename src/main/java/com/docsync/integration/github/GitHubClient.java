package com.docsync.integration.github;

import com.docsync.integration.dto.GitHubCommitResponse;
import com.docsync.integration.dto.GitHubContentResponse;

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

    void createBranch(
            String owner,
            String repo,
            String branchName,
            String sourceSha
    );

    Optional<GitHubContentResponse> getFileDetails(
            String owner,
            String repo,
            String path,
            String ref
    );

    void updateFile(
            String owner,
            String repo,
            String path,
            String branch,
            String content,
            String fileSha,
            String commitMessage
    );


}
