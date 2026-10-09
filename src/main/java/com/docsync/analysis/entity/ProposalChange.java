package com.docsync.analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "proposal_changes")
public class ProposalChange {

    protected ProposalChange() {
    }

    public ProposalChange(
            UUID id,
            DocumentationProposal proposal,
            String documentationPath,
            String originalContent,
            String proposedContent
    ) {
        this.id = id;
        this.proposal = proposal;
        this.documentationPath = documentationPath;
        this.originalContent = originalContent;
        this.proposedContent = proposedContent;
    }

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proposal_id", nullable = false)
    private DocumentationProposal proposal;

    @Column(name = "documentation_path", nullable = false)
    private String documentationPath;

    @Column(name = "original_content", nullable = false, columnDefinition = "TEXT")
    private String originalContent;

    @Column(name = "proposed_content", nullable = false, columnDefinition = "TEXT")
    private String proposedContent;
}