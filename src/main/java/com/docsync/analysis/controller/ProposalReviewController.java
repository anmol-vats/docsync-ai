
package com.docsync.analysis.controller;

import com.docsync.analysis.dto.ProposalReviewResponse;
import com.docsync.analysis.service.ProposalReviewService;
import com.docsync.analysis.service.PullRequestCreationService;
import com.docsync.integration.dto.GitHubPullRequestResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/proposals")
public class ProposalReviewController {

    private final ProposalReviewService proposalReviewService;
    private final PullRequestCreationService pullRequestCreationService;

    public ProposalReviewController(
            ProposalReviewService proposalReviewService, PullRequestCreationService pullRequestCreationService
    ) {
        this.proposalReviewService = proposalReviewService;
        this.pullRequestCreationService = pullRequestCreationService;
    }

    @GetMapping("/{proposalId}")
    public ProposalReviewResponse getProposal(
            @PathVariable UUID proposalId
    ) {
        return proposalReviewService.getProposal(proposalId);
    }

    @PostMapping("/{proposalId}/approve")
    public void approveProposal(
            @PathVariable UUID proposalId
    ) {
        proposalReviewService.approveProposal(proposalId);
    }

    @PostMapping("/{proposalId}/reject")
    public void rejectProposal(
            @PathVariable UUID proposalId
    ) {
        proposalReviewService.rejectProposal(proposalId);
    }

    @PostMapping("/{proposalId}/pull-request")
    public GitHubPullRequestResponse createPullRequest(
            @PathVariable UUID proposalId
    ) {
        return pullRequestCreationService.createPullRequest(proposalId);
    }
}
