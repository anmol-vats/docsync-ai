
package com.docsync.integration.github;

import com.docsync.integration.dto.GitHubContentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "JWT_SECRET=test-secret-for-integration-tests-only"
})
class GitHubFileUpdateIntegrationTest {

    @Autowired
    private GitHubClient gitHubClient;

    @Test
    void shouldUpdateFileOnTargetBranch() {
        String owner = "anmol-vats";
        String repo = "docsync-test-repo";
        String branch =
                "docsync-test-dd80153f-4d19-4257-9d8e-9eae924e5647";

        String path = "README.md";

        GitHubContentResponse file = gitHubClient
                .getFileDetails(owner, repo, path, branch)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "File not found on target branch: " + path
                        )
                );

        assertNotNull(file.getSha(), "GitHub must return the file SHA");

        String updatedContent = """
                # DocSync AI Integration Test

                This file was updated through the GitHub Contents API.

                Test marker: %s
                """.formatted(UUID.randomUUID());

        gitHubClient.updateFile(
                owner,
                repo,
                path,
                branch,
                updatedContent,
                file.getSha(),
                "test(github): verify documentation file update"
        );

        GitHubContentResponse updatedFile = gitHubClient
                .getFileDetails(owner, repo, path, branch)
                .orElseThrow();

        assertNotEquals(
                file.getSha(),
                updatedFile.getSha(),
                "The file SHA should change after a successful update"
        );

        System.out.println("Updated file: " + path);
        System.out.println("Branch: " + branch);
        System.out.println("New file SHA: " + updatedFile.getSha());
    }
}
