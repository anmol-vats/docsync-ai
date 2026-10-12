
package com.docsync.analysis.service;

import com.docsync.analysis.dto.ProposalReviewResponse;
import com.docsync.analysis.entity.DocumentationProposal;
import com.docsync.analysis.entity.ProposalChange;
import com.docsync.analysis.entity.ProposalChangeRepository;
import com.docsync.analysis.repository.DocumentationProposalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProposalReviewService {

    private final DocumentationProposalRepository proposalRepository;
    private final ProposalChangeRepository changeRepository;

    public ProposalReviewService(
            DocumentationProposalRepository proposalRepository,
            ProposalChangeRepository changeRepository
    ) {
        this.proposalRepository = proposalRepository;
        this.changeRepository = changeRepository;
    }

    @Transactional(readOnly = true)
    public ProposalReviewResponse getProposal(UUID proposalId) {

        DocumentationProposal proposal = proposalRepository
                .findById(proposalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Documentation proposal not found"
                ));

        List<ProposalReviewResponse.ProposalChangeResponse> changes =
                changeRepository.findAllByProposal_Id(proposalId)
                        .stream()
                        .map(change ->
                                new ProposalReviewResponse.ProposalChangeResponse(
                                        change.getId(),
                                        change.getDocumentationPath(),
                                        change.getOriginalContent(),
                                        change.getProposedContent()
                                ))
                        .toList();

        return new ProposalReviewResponse(
                proposal.getId(),
                proposal.getStatus().name(),
                proposal.getSourceCommitSha(),
                proposal.getCreatedAt().toString(),
                changes
        );
    }
}
