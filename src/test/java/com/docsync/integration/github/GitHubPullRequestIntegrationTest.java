package com.docsync.integration.github;

import com.docsync.integration.dto.GitHubPullRequestResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "JWT_SECRET=test-secret-for-integration-tests-only"
})
class GitHubPullRequestIntegrationTest {

    @Autowired
    private GitHubClient gitHubClient;

    @Test
    void shouldCreatePullRequestFromTestBranch() {
        String owner = "anmol-vats";
        String repo = "docsync-test-repo";

        String headBranch =
                "docsync-test-dd80153f-4d19-4257-9d8e-9eae924e5647";

        GitHubRepositoryResponse repository = gitHubClient
                .getRepository(owner, repo)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Repository not found"
                        )
                );

        String baseBranch = repository.getDefaultBranch();

        Optional<String> headSha = gitHubClient
                .getDefaultBranchHeadSha(owner, repo);

        assertTrue(headSha.isPresent(),
                "Default branch HEAD should exist");

        GitHubPullRequestResponse response =
                gitHubClient.createPullRequest(
                        owner,
                        repo,
                        "test(github): verify DocSync AI pull request creation",
                        headBranch,
                        baseBranch,
                        "Integration test verifying that DocSync AI can create "
                                + "a pull request through the GitHub API."
                );

        assertTrue(response.number() > 0);
        assertNotNull(response.htmlUrl());
        assertFalse(response.htmlUrl().isBlank());

        System.out.println("PR number: " + response.number());
        System.out.println("PR URL: " + response.htmlUrl());
        System.out.println("PR state: " + response.state());
    }
}
