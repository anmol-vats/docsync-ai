
package com.docsync.integration.github;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "docsync.jwt.secret=test-secret-for-integration-tests-only"
})
class GitHubClientIntegrationTest {

    @Autowired
    private GitHubClient gitHubClient;

    @Test
    void shouldFetchDefaultBranchHeadSha() {
        Optional<String> sha =
                gitHubClient.getDefaultBranchHeadSha(
                        "octocat",
                        "git-consortium"
                );

        assertTrue(sha.isPresent(), "HEAD SHA should be present");
        assertFalse(sha.get().isBlank());

        System.out.println("Default branch HEAD: " + sha.get());
    }
}
