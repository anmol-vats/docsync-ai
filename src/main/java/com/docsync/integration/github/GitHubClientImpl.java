package com.docsync.integration.github;

import com.docsync.integration.dto.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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

    @Override
    public String getFileContent(
            String owner,
            String repo,
            String path,
            String commitSha
    ) {
        GitHubContentResponse response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/contents/{path}")
                        .queryParam("ref", commitSha)
                        .build(owner, repo, path))
                .retrieve()
                .body(GitHubContentResponse.class);

        if (response == null || response.getContent() == null) {
            throw new RuntimeException(
                    "Cannot find file: " + path
            );
        }

        if (!"base64".equalsIgnoreCase(response.getEncoding())) {
            throw new RuntimeException(
                    "Unsupported GitHub file encoding: "
                            + response.getEncoding()
            );
        }

        String encodedContent = response.getContent()
                .replaceAll("\\s", "");

        byte[] decodedContent =
                Base64.getDecoder().decode(encodedContent);

        return new String(
                decodedContent,
                StandardCharsets.UTF_8
        );
    }


    @Override
    public Optional<String> getDefaultBranchHeadSha(
            String owner,
            String repo
    ) {
        Optional<GitHubRepositoryResponse> repositoryResponse =
                getRepository(owner, repo);

        if (repositoryResponse.isEmpty()) {
            System.out.println("DEBUG: Repository metadata not found");
            return Optional.empty();
        }

        String defaultBranch =
                repositoryResponse.get().getDefaultBranch();
        System.out.println("DEBUG: Default branch = " + defaultBranch);

        if (defaultBranch == null || defaultBranch.isBlank()) {
            return Optional.empty();
        }

        Optional<GitHubBranchResponse> branchResponse =
                restClient.get()
                        .uri(
                                "/repos/{owner}/{repo}/branches/{branch}",
                                owner,
                                repo,
                                defaultBranch
                        )
                        .exchange((request, response) -> {
                            if (response.getStatusCode().value() == 404) {
                                System.out.println("response in 404");
                                return Optional.empty();
                            }

                            if (!response.getStatusCode().is2xxSuccessful()) {
                                throw new IllegalStateException(
                                        "GitHub API request failed: "
                                                + response.getStatusCode()
                                );
                            }

                            return Optional.ofNullable(
                                    response.bodyTo(
                                            GitHubBranchResponse.class
                                    )
                            );
                        });

        return branchResponse
                .map(GitHubBranchResponse::getCommit)
                .map(GitHubBranchResponse.Commit::getSha)
                .filter(sha -> !sha.isBlank());
    }


    @Override
    public void createBranch(
            String owner,
            String repo,
            String branchName,
            String sourceSha
    ) {
        if (branchName == null || branchName.isBlank()
                || sourceSha == null || sourceSha.isBlank()) {
            throw new IllegalArgumentException(
                    "Branch name and source SHA are required"
            );
        }

        GitHubCreateRefRequest request =
                new GitHubCreateRefRequest(
                        "refs/heads/" + branchName,
                        sourceSha
                );

        restClient.post()
                .uri("/repos/{owner}/{repo}/git/refs", owner, repo)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }


    @Override
    public Optional<GitHubContentResponse> getFileDetails(
            String owner,
            String repo,
            String path,
            String ref
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/contents/{path}")
                        .queryParam("ref", ref)
                        .build(owner, repo, path))
                .exchange((request, response) -> {
                    if (response.getStatusCode().value() == 404) {
                        return Optional.empty();
                    }

                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw new IllegalStateException(
                                "GitHub file lookup failed: HTTP "
                                        + response.getStatusCode().value()
                        );
                    }

                    GitHubContentResponse file =
                            response.bodyTo(GitHubContentResponse.class);

                    return Optional.ofNullable(file);
                });
    }

    @Override
    public void updateFile(
            String owner,
            String repo,
            String path,
            String branch,
            String content,
            String fileSha,
            String commitMessage
    ) {
        if (branch == null || branch.isBlank()
                || fileSha == null || fileSha.isBlank()
                || content == null
                || commitMessage == null || commitMessage.isBlank()) {
            throw new IllegalArgumentException(
                    "Branch, file SHA, content, and commit message are required"
            );
        }

        String encodedContent = Base64.getEncoder().encodeToString(
                content.getBytes(StandardCharsets.UTF_8)
        );

        GitHubUpdateFileRequest request = new GitHubUpdateFileRequest(
                commitMessage,
                encodedContent,
                branch,
                fileSha
        );

        restClient.put()
                .uri("/repos/{owner}/{repo}/contents/{path}",
                        owner, repo, path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }


}
