package com.docsync.integration.github;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest(properties = {
        "JWT_SECRET=test-secret-for-integration-tests-only"
})
class GitHubBranchIntegrationTest {

    @Autowired
    private GitHubClient gitHubClient;

    @Test
    void shouldCreateBranchFromExistingCommit() {
        String owner = "anmol-vats";
        String repo = "docsync-test-repo";

        String sourceSha = gitHubClient
                .getDefaultBranchHeadSha(owner, repo)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Could not fetch default branch HEAD"
                        )
                );

        String branchName =
                "docsync-test-" + UUID.randomUUID();

        gitHubClient.createBranch(
                owner,
                repo,
                branchName,
                sourceSha
        );

        System.out.println("Created branch: " + branchName);
        System.out.println("Source SHA: " + sourceSha);
    }
}
