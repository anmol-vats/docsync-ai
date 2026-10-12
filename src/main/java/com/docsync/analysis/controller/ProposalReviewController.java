
package com.docsync.analysis.controller;

import com.docsync.analysis.dto.ProposalReviewResponse;
import com.docsync.analysis.service.ProposalReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/proposals")
public class ProposalReviewController {

    private final ProposalReviewService proposalReviewService;

    public ProposalReviewController(
            ProposalReviewService proposalReviewService
    ) {
        this.proposalReviewService = proposalReviewService;
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
}
