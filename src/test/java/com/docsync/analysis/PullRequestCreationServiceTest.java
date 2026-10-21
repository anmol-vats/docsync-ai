package com.docsync.analysis;

import com.docsync.analysis.entity.DocumentationProposal;
import com.docsync.analysis.entity.ProposalStatus;
import com.docsync.analysis.repository.DocumentationProposalRepository;
import com.docsync.analysis.repository.ProposalChangeRepository;
import com.docsync.analysis.service.PullRequestCreationService;
import com.docsync.integration.dto.GitHubContentResponse;
import com.docsync.integration.github.GitHubClient;
import com.docsync.integration.github.GitHubRepositoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PullRequestCreationServiceTest {

    @Mock
    private DocumentationProposalRepository proposalRepository;

    @Mock
    private ProposalChangeRepository changeRepository;

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private PullRequestCreationService pullRequestCreationService;

    @Test
    void shouldRejectProposalThatIsNotApproved() {
        UUID proposalId = UUID.randomUUID();

        DocumentationProposal proposal =
                mock(DocumentationProposal.class);

        when(proposalRepository
                .findWithAnalysisAndRepositoryById(proposalId))
                .thenReturn(Optional.of(proposal));

        when(proposal.getStatus())
                .thenReturn(ProposalStatus.PENDING_REVIEW);

        assertThrows(
                ResponseStatusException.class,
                () -> pullRequestCreationService
                        .createPullRequest(proposalId)
        );

        verifyNoInteractions(gitHubClient, changeRepository);
    }


    @Test
    void shouldRejectStaleApprovedProposal() {
        UUID proposalId = UUID.randomUUID();

        DocumentationProposal proposal =
                mock(DocumentationProposal.class);

        when(proposalRepository
                .findWithAnalysisAndRepositoryById(proposalId))
                .thenReturn(Optional.of(proposal));

        when(proposal.getStatus())
                .thenReturn(ProposalStatus.APPROVED);

        when(proposal.getSourceCommitSha())
                .thenReturn("old-source-sha");

        when(proposal.getAnalysis())
                .thenReturn(mock(com.docsync.analysis.entity.Analysis.class));

        var analysis = proposal.getAnalysis();
        var repository = mock(
                com.docsync.repositorymanagement.entity.Repository.class
        );

        when(analysis.getRepository()).thenReturn(repository);
        when(repository.getOwner()).thenReturn("anmol-vats");
        when(repository.getName()).thenReturn("docsync-test-repo");

        when(gitHubClient.getRepository(
                "anmol-vats", "docsync-test-repo"
        )).thenReturn(Optional.of(
                repositoryResponse("main")
        ));

        when(gitHubClient.getDefaultBranchHeadSha(
                "anmol-vats", "docsync-test-repo"
        )).thenReturn(Optional.of("newer-source-sha"));

        assertThrows(
                ResponseStatusException.class,
                () -> pullRequestCreationService
                        .createPullRequest(proposalId)
        );

        verify(proposal).markStale();
        verify(proposalRepository).save(proposal);
        verify(gitHubClient, never()).createBranch(
                anyString(), anyString(), anyString(), anyString()
        );
    }

    private GitHubRepositoryResponse repositoryResponse(String branch) {
        GitHubRepositoryResponse response = new GitHubRepositoryResponse();
        response.setDefaultBranch(branch);
        return response;
    }


    @Test
    void shouldRejectWhenDocumentationContentHasChanged() {
        UUID proposalId = UUID.randomUUID();

        DocumentationProposal proposal =
                mock(DocumentationProposal.class);

        var analysis = mock(
                com.docsync.analysis.entity.Analysis.class
        );
        var repository = mock(
                com.docsync.repositorymanagement.entity.Repository.class
        );

        when(proposalRepository
                .findWithAnalysisAndRepositoryById(proposalId))
                .thenReturn(Optional.of(proposal));

        when(proposal.getStatus())
                .thenReturn(ProposalStatus.APPROVED);
        when(proposal.getAnalysis()).thenReturn(analysis);
        when(analysis.getRepository()).thenReturn(repository);
        when(repository.getOwner()).thenReturn("anmol-vats");
        when(repository.getName()).thenReturn("docsync-test-repo");
        when(proposal.getSourceCommitSha()).thenReturn("source-sha");

        when(gitHubClient.getRepository(
                "anmol-vats", "docsync-test-repo"
        )).thenReturn(Optional.of(repositoryResponse("main")));

        when(gitHubClient.getDefaultBranchHeadSha(
                "anmol-vats", "docsync-test-repo"
        )).thenReturn(Optional.of("source-sha"));

        var change = mock(
                com.docsync.analysis.entity.ProposalChange.class
        );
        when(change.getDocumentationPath()).thenReturn("README.md");
        when(change.getOriginalContent()).thenReturn("Original content");

        when(changeRepository.findAllByProposal_Id(proposalId))
                .thenReturn(java.util.List.of(change));

        GitHubContentResponse response = mock(GitHubContentResponse.class);

        when(gitHubClient.getFileDetails(
                eq("anmol-vats"),
                eq("docsync-test-repo"),
                eq("README.md"),
                startsWith("docsync-ai/proposal-")
        )).thenReturn(Optional.of(response));


        when(gitHubClient.getFileContent(
                eq("anmol-vats"),
                eq("docsync-test-repo"),
                eq("README.md"),
                startsWith("docsync-ai/proposal-")
        )).thenReturn("Someone else's newer content");


        assertThrows(
                ResponseStatusException.class,
                () -> pullRequestCreationService
                        .createPullRequest(proposalId)
        );

        verify(gitHubClient, never()).updateFile(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()
        );

        verify(gitHubClient, never()).createPullRequest(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString()
        );
    }


    @Test
    void shouldCreatePullRequestForApprovedFreshProposal() {
        UUID proposalId = UUID.randomUUID();

        DocumentationProposal proposal = mock(DocumentationProposal.class);
        var analysis = mock(com.docsync.analysis.entity.Analysis.class);
        var repository = mock(
                com.docsync.repositorymanagement.entity.Repository.class
        );

        when(proposalRepository
                .findWithAnalysisAndRepositoryById(proposalId))
                .thenReturn(Optional.of(proposal));

        when(proposal.getStatus()).thenReturn(ProposalStatus.APPROVED);
        when(proposal.getAnalysis()).thenReturn(analysis);
        when(analysis.getRepository()).thenReturn(repository);
        when(repository.getOwner()).thenReturn("anmol-vats");
        when(repository.getName()).thenReturn("docsync-test-repo");
        when(proposal.getSourceCommitSha()).thenReturn("source-sha");

        when(gitHubClient.getRepository(
                "anmol-vats", "docsync-test-repo"
        )).thenReturn(Optional.of(repositoryResponse("main")));

        when(gitHubClient.getDefaultBranchHeadSha(
                "anmol-vats", "docsync-test-repo"
        )).thenReturn(Optional.of("source-sha"));

        var change = mock(com.docsync.analysis.entity.ProposalChange.class);
        when(change.getDocumentationPath()).thenReturn("README.md");
        when(change.getOriginalContent()).thenReturn("Original content");
        when(change.getProposedContent()).thenReturn("Proposed content");

        when(changeRepository.findAllByProposal_Id(proposalId))
                .thenReturn(java.util.List.of(change));

        GitHubContentResponse file = mock(GitHubContentResponse.class);
        when(file.getSha()).thenReturn("file-sha");

        when(gitHubClient.getFileDetails(
                eq("anmol-vats"),
                eq("docsync-test-repo"),
                eq("README.md"),
                startsWith("docsync-ai/proposal-")
        )).thenReturn(Optional.of(file));

        when(gitHubClient.getFileContent(
                eq("anmol-vats"),
                eq("docsync-test-repo"),
                eq("README.md"),
                startsWith("docsync-ai/proposal-")
        )).thenReturn("Original content");

        var expectedResponse =
                new com.docsync.integration.dto.GitHubPullRequestResponse(
                        42L,
                        "https://github.com/anmol-vats/docsync-test-repo/pull/42",
                        "docs: update documentation via DocSync AI",
                        "open"
                );

        when(gitHubClient.createPullRequest(
                eq("anmol-vats"),
                eq("docsync-test-repo"),
                anyString(),
                startsWith("docsync-ai/proposal-"),
                eq("main"),
                anyString()
        )).thenReturn(expectedResponse);

        var actualResponse =
                pullRequestCreationService.createPullRequest(proposalId);

        assertEquals(42L, actualResponse.number());
        assertEquals(
                expectedResponse.htmlUrl(),
                actualResponse.htmlUrl()
        );

        verify(gitHubClient).createBranch(
                eq("anmol-vats"),
                eq("docsync-test-repo"),
                startsWith("docsync-ai/proposal-"),
                eq("source-sha")
        );

        verify(gitHubClient).updateFile(
                eq("anmol-vats"),
                eq("docsync-test-repo"),
                eq("README.md"),
                startsWith("docsync-ai/proposal-"),
                eq("Proposed content"),
                eq("file-sha"),
                anyString()
        );

        verify(proposal).markPrCreated(expectedResponse.htmlUrl());
        verify(proposalRepository).save(proposal);
    }

}
