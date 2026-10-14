package com.docsync.analysis;

import com.docsync.analysis.entity.Analysis;
import com.docsync.analysis.entity.DocumentationProposal;
import com.docsync.analysis.entity.ProposalChangeRepository;
import com.docsync.analysis.entity.ProposalStatus;
import com.docsync.analysis.repository.DocumentationProposalRepository;
import com.docsync.analysis.service.ProposalReviewService;
import com.docsync.integration.github.GitHubClient;
import com.docsync.repositorymanagement.entity.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalReviewServiceTest {

    @Mock
    private DocumentationProposalRepository proposalRepository;

    @Mock
    private ProposalChangeRepository changeRepository;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private Analysis analysis;

    @Mock
    private Repository repository;

    @InjectMocks
    private ProposalReviewService service;

    private final UUID proposalId = UUID.randomUUID();
    private final String sourceSha = "abc123";
    private DocumentationProposal proposal;

    @BeforeEach
    void setUp() {
        when(analysis.getRepository()).thenReturn(repository);
        when(repository.getOwner()).thenReturn("octocat");
        when(repository.getName()).thenReturn("git-consortium");

        proposal = new DocumentationProposal(
                proposalId,
                analysis,
                ProposalStatus.PENDING_REVIEW,
                sourceSha,
                Instant.now(),
                null
        );

        when(proposalRepository.findById(proposalId))
                .thenReturn(Optional.of(proposal));
    }

    @Test
    void shouldApproveProposalWhenSourceShaMatchesCurrentHead()
            throws Exception {

        when(gitHubClient.getDefaultBranchHeadSha(
                "octocat", "git-consortium"))
                .thenReturn(Optional.of(sourceSha));

        service.approveProposal(proposalId);

        assertEquals(ProposalStatus.APPROVED, proposal.getStatus());
        verify(proposalRepository).save(proposal);
    }

    @Test
    void shouldMarkProposalStaleWhenHeadShaDiffers() {
        when(gitHubClient.getDefaultBranchHeadSha(
                "octocat", "git-consortium"))
                .thenReturn(Optional.of("newer456"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.approveProposal(proposalId)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );
        assertEquals(ProposalStatus.STALE, proposal.getStatus());
        verify(proposalRepository).save(proposal);
    }

    @Test
    void shouldNotMarkProposalStaleWhenHeadCannotBeFetched() {
        when(gitHubClient.getDefaultBranchHeadSha(
                "octocat", "git-consortium"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.approveProposal(proposalId)
        );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );
        assertEquals(
                ProposalStatus.PENDING_REVIEW,
                proposal.getStatus()
        );
        verify(proposalRepository, never()).save(proposal);
    }

    @Test
    void shouldNotMarkProposalStaleWhenGitHubThrows() {
        when(gitHubClient.getDefaultBranchHeadSha(
                "octocat", "git-consortium"))
                .thenThrow(new IllegalStateException("GitHub unavailable"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.approveProposal(proposalId)
        );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );
        assertEquals(
                ProposalStatus.PENDING_REVIEW,
                proposal.getStatus()
        );
        verify(proposalRepository, never()).save(proposal);
    }

    @Test
    void shouldRejectApprovalWhenProposalWasAlreadyRejected() {
        proposal.reject();

        when(gitHubClient.getDefaultBranchHeadSha(
                "octocat", "git-consortium"))
                .thenReturn(Optional.of(sourceSha));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.approveProposal(proposalId)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );
        assertEquals(ProposalStatus.REJECTED, proposal.getStatus());
    }

    @Test
    void shouldRejectApprovalWhenProposalIsAlreadyStale() {
        proposal.markStale();

        when(gitHubClient.getDefaultBranchHeadSha(
                "octocat", "git-consortium"))
                .thenReturn(Optional.of(sourceSha));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.approveProposal(proposalId)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );
        assertEquals(ProposalStatus.STALE, proposal.getStatus());
    }
}
