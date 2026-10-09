package com.docsync.analysis.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "documentation_proposals")
public class DocumentationProposal {
    protected DocumentationProposal(){};

    public DocumentationProposal(
            UUID id,
            Analysis analysis,
            ProposalStatus status,
            String sourceCommitSha,
            Instant createdAt,
            Instant reviewedAt
    ) {
        this.id = id;
        this.analysis = analysis;
        this.status = status;
        this.sourceCommitSha = sourceCommitSha;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
    }

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @Column(name = "source_commit_sha", nullable = false)
    private String sourceCommitSha;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private Instant reviewedAt;
}
