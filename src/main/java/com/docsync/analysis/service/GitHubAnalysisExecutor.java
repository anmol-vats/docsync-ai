package com.docsync.analysis.service;

import com.docsync.analysis.entity.Analysis;
import com.docsync.analysis.entity.AnalysisJob;
import com.docsync.analysis.entity.AnalysisRepository;
import com.docsync.analysis.entity.ImpactedDocumentation;
import com.docsync.analysis.impact.ChangedFile;
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
    private final DocumentationProposalService documentationProposalService;

    public GitHubAnalysisExecutor(GitHubClient gitHubClient, AnalysisRepository analysisRepository, DocumentationImpactAnalyzer documentationImpactAnalyzer, DocumentationProposalService documentationProposalService) {
        this.gitHubClient = gitHubClient;
        this.analysisRepository = analysisRepository;
        this.documentationImpactAnalyzer = documentationImpactAnalyzer;
        this.documentationProposalService = documentationProposalService;
    }

    @Override
    public void execute(AnalysisJob analysisJob) {
        Repository repository = analysisJob.getWebhookEvent().getRepository();
        String owner = repository.getOwner();
        String repo = repository.getName();
        String commitSha = analysisJob.getCommitSha();

        List<String> repositoryFiles =
                gitHubClient.getRepositoryFiles(owner, repo, commitSha);
        repositoryFiles.forEach(file ->
                System.out.println("Repository file: " + file)
        );
        Optional<GitHubCommitResponse> response =
                gitHubClient.getCommit(owner, repo, commitSha);

        if (response.isEmpty()) {
            throw new RuntimeException("Cannot find commit: " + commitSha);
        }

        GitHubCommitResponse commit = response.get();

        List<GitHubCommitFileResponse> changedFiles =  commit.getFiles();

        changedFiles.forEach(file ->
                System.out.println(
                        "File: " + file.getFilename()
                                + "\nPatch: " + file.getPatch()
                )
        );

//        List<String> changedFilePaths = changedFiles.stream()
//                .map(GitHubCommitFileResponse::getFilename)
//                .toList();

        List<ChangedFile> changedFileDetails = changedFiles.stream()
                .map(file -> new ChangedFile(
                        file.getFilename(),
                        file.getPatch()
                ))
                .toList();

        List<ImpactedDocumentation> impactedDocumentation =
                documentationImpactAnalyzer.findImpactedDocumentation(changedFileDetails, repositoryFiles);

        boolean driftDetected = !impactedDocumentation.isEmpty();

        Analysis analysis = new Analysis(
                UUID.randomUUID(),
                repository,
                commitSha,
                driftDetected,
                Instant.now(),
                impactedDocumentation
        );
//        analysisRepository.save(analysis);
        Analysis savedAnalysis = analysisRepository.save(analysis);
        System.out.println(
                "Analysis saved. Drift detected: "
                        + savedAnalysis.isDriftDetected()
        );

        System.out.println(
                "Impacted docs: "
                        + savedAnalysis.getImpactedDocumentation().size()
        );


        documentationProposalService.generateProposal(savedAnalysis, changedFileDetails);
    }

}
