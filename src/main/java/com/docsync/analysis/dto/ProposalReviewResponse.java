package com.docsync.analysis.dto;
import java.util.List;
import java.util.UUID;

public record ProposalReviewResponse(
        UUID id,
        String status,
        String sourceCommitSha,
        String createdAt,
        List<ProposalChangeResponse> changes
) {

    public record ProposalChangeResponse(
            UUID id,
            String documentationPath,
            String originalContent,
            String proposedContent
    ) {}
}
