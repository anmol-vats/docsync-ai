package com.docsync.analysis.service;

import com.docsync.analysis.entity.Analysis;
import com.docsync.analysis.entity.AnalysisJob;
import com.docsync.analysis.entity.AnalysisRepository;
import com.docsync.analysis.entity.ImpactedDocumentation;
import com.docsync.analysis.impact.DocumentationImpactAnalyzer;
import com.docsync.integration.dto.GitHubCommitFileResponse;
import com.docsync.integration.dto.GitHubCommitResponse;
import com.docsync.integration.github.GitHubClient;
import com.docsync.repositorymanagement.entity.Repository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GitHubAnalysisExecutor implements  AnalysisExecutor {
    private final GitHubClient gitHubClient;
    private final AnalysisRepository analysisRepository;
    private final DocumentationImpactAnalyzer documentationImpactAnalyzer;

    public GitHubAnalysisExecutor(GitHubClient gitHubClient, AnalysisRepository analysisRepository, DocumentationImpactAnalyzer documentationImpactAnalyzer) {
        this.gitHubClient = gitHubClient;
        this.analysisRepository = analysisRepository;
        this.documentationImpactAnalyzer = documentationImpactAnalyzer;
    }

    @Override
    public void execute(AnalysisJob analysisJob) {
        Repository repository = analysisJob.getWebhookEvent().getRepository();
        String owner = repository.getOwner();
        String repo = repository.getName();
        String commitSha = analysisJob.getCommitSha();
        Optional<GitHubCommitResponse> response =
                gitHubClient.getCommit(owner, repo, commitSha);

        if (response.isEmpty()) {
            throw new RuntimeException("Cannot find commit: " + commitSha);
        }

        GitHubCommitResponse commit = response.get();

        List<GitHubCommitFileResponse> changedFiles =  commit.getFiles();

        List<String> changedFilePaths = changedFiles.stream()
                .map(GitHubCommitFileResponse::getFilename)
                .toList();

        List<ImpactedDocumentation> impactedDocumentation =
                documentationImpactAnalyzer.findImpactedDocumentation(changedFilePaths);

        Analysis analysis = new Analysis(
                UUID.randomUUID(),
                repository,
                commitSha,
                false,
                Instant.now(),
                impactedDocumentation
        );
        analysisRepository.save(analysis);
    }

}
