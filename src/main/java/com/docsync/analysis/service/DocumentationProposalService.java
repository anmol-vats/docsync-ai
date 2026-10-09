package com.docsync.analysis.service;

import com.docsync.analysis.entity.*;
import com.docsync.analysis.repository.DocumentationProposalRepository;
import com.docsync.integration.github.GitHubClient;
import com.docsync.repositorymanagement.entity.Repository;
import com.docsync.repositorymanagement.repository.RepositoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class DocumentationProposalService {

    private final DocumentationProposalRepository documentationProposalRepository;
    private final ProposalChangeRepository proposalChangeRepository;
    private final GitHubClient gitHubClient;
    private final RepositoryRepository repositoryRepository;
    private final AnalysisRepository analysisRepository;

    public DocumentationProposalService(
            DocumentationProposalRepository documentationProposalRepository,
            ProposalChangeRepository proposalChangeRepository,
            GitHubClient gitHubClient, RepositoryRepository repositoryRepository, AnalysisRepository analysisRepository
    ) {
        this.documentationProposalRepository = documentationProposalRepository;
        this.proposalChangeRepository = proposalChangeRepository;
        this.gitHubClient = gitHubClient;
        this.repositoryRepository = repositoryRepository;
        this.analysisRepository = analysisRepository;
    }

    @Transactional
    public DocumentationProposal generateProposal(Analysis analysis) {
        System.out.println("GENERATE PROPOSAL CALLED");

        Analysis managedAnalysis = analysisRepository
                .findWithRepositoryById(analysis.getId())
                .orElseThrow(() ->
                        new RuntimeException("Analysis not found")
                );

        if (!analysis.isDriftDetected()) {
            return null;
        }

        DocumentationProposal proposal =
                new DocumentationProposal(
                        UUID.randomUUID(),
                        analysis,
                        ProposalStatus.GENERATED,
                        analysis.getSourceCommitSha(),
                        Instant.now(),
                        null
                );
        System.out.println("Before proposal save");

        proposal = documentationProposalRepository.save(proposal);

        System.out.println("After proposal save");

//        Repository repository = analysis.getRepository();

        Repository repository = repositoryRepository
                .findById(analysis.getRepository().getId())
                .orElseThrow(() ->
                        new RuntimeException("Repository not found")
                );
        String owner = repository.getOwner();
        String repo = repository.getName();
        String commitSha = analysis.getSourceCommitSha();

        System.out.println(
                "Impacted docs count: "
                        + analysis.getImpactedDocumentation().size()
        );

        for (ImpactedDocumentation impactedDocumentation
                : analysis.getImpactedDocumentation()) {

            System.out.println(
                    "Creating proposal change for: "
                            + impactedDocumentation.getDocumentationPath()
            );

            String documentationPath =
                    impactedDocumentation.getDocumentationPath();

            String originalContent =
                    gitHubClient.getFileContent(
                            owner,
                            repo,
                            documentationPath,
                            commitSha
                    );

            String proposedContent =
                    originalContent + "\n\n<!-- DocSync proposal -->";

            ProposalChange proposalChange =
                    new ProposalChange(
                            UUID.randomUUID(),
                            proposal,
                            documentationPath,
                            originalContent,
                            proposedContent
                    );


            proposalChangeRepository.save(proposalChange);
        }

        return proposal;
    }
}