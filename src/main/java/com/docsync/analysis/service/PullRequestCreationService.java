
package com.docsync.analysis.service;

import com.docsync.analysis.entity.DocumentationProposal;
import com.docsync.analysis.entity.ProposalChange;
import com.docsync.analysis.entity.ProposalStatus;
import com.docsync.analysis.repository.DocumentationProposalRepository;
import com.docsync.analysis.repository.ProposalChangeRepository;
import com.docsync.integration.dto.GitHubPullRequestResponse;
import com.docsync.integration.github.GitHubClient;
import com.docsync.repositorymanagement.entity.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PullRequestCreationService {

    private final DocumentationProposalRepository proposalRepository;
    private final ProposalChangeRepository changeRepository;
    private final GitHubClient gitHubClient;

    public PullRequestCreationService(
            DocumentationProposalRepository proposalRepository,
            ProposalChangeRepository changeRepository,
            GitHubClient gitHubClient
    ) {
        this.proposalRepository = proposalRepository;
        this.changeRepository = changeRepository;
        this.gitHubClient = gitHubClient;
    }

    public GitHubPullRequestResponse createPullRequest(UUID proposalId) {

        DocumentationProposal proposal = proposalRepository
                .findWithAnalysisAndRepositoryById(proposalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Proposal not found"
                ));

        if (proposal.getStatus() != ProposalStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only approved proposals can create a pull request"
            );
        }

        Repository repository = proposal.getAnalysis().getRepository();

        String owner = repository.getOwner();
        String repo = repository.getName();
        String sourceSha = proposal.getSourceCommitSha();

        String defaultBranch = getDefaultBranch(owner, repo);
        verifyFreshness(proposal, owner, repo, sourceSha);

        List<ProposalChange> changes =
                changeRepository.findAllByProposal_Id(proposalId);

        if (changes.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Proposal contains no documentation changes"
            );
        }

        String branchName = "docsync-ai/proposal-" + proposalId;

        gitHubClient.createBranch(
                owner, repo, branchName, sourceSha
        );

        for (ProposalChange change : changes) {
            String path = change.getDocumentationPath();

            var file = gitHubClient.getFileDetails(
                    owner, repo, path, branchName
            ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Documentation file not found on branch: " + path
            ));

            String currentContent = gitHubClient.getFileContent(
                    owner, repo, path, branchName
            );

            if (!currentContent.equals(change.getOriginalContent())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Documentation changed since proposal generation: "
                                + path
                );
            }

            gitHubClient.updateFile(
                    owner,
                    repo,
                    path,
                    branchName,
                    change.getProposedContent(),
                    file.getSha(),
                    "docs: update " + path + " via DocSync AI"
            );
        }

        verifyFreshness(proposal, owner, repo, sourceSha);

        GitHubPullRequestResponse response =
                gitHubClient.createPullRequest(
                        owner,
                        repo,
                        "docs: update documentation via DocSync AI",
                        branchName,
                        defaultBranch,
                        "Generated from approved DocSync AI proposal "
                                + proposalId
                );

        proposal.markPrCreated(response.htmlUrl());
        proposalRepository.save(proposal);

        return response;
    }

    private String getDefaultBranch(String owner, String repo) {
        return gitHubClient.getRepository(owner, repo)
                .map(com.docsync.integration.github
                        .GitHubRepositoryResponse::getDefaultBranch)
                .filter(branch -> !branch.isBlank())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Could not determine the repository default branch"
                ));
    }

    private void verifyFreshness(
            DocumentationProposal proposal,
            String owner,
            String repo,
            String sourceSha
    ) {
        String currentHead = gitHubClient
                .getDefaultBranchHeadSha(owner, repo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Could not verify the current default-branch HEAD"
                ));

        if (!currentHead.equals(sourceSha)) {
            proposal.markStale();
            proposalRepository.save(proposal);

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Proposal is stale because the default branch has advanced"
            );
        }
    }
}
