package com.docsync.analysis.entity;

import com.docsync.repositorymanagement.entity.Repository;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "analyses")
public class Analysis {
    protected Analysis() {}

    public Analysis(
            UUID id,
            Repository repository,
            String sourceCommitSha,
            boolean driftDetected,
            Instant createdAt,
            List<ImpactedDocumentation> impactedDocumentation
    ) {
        this.id = id;
        this.repository = repository;
        this.sourceCommitSha = sourceCommitSha;
        this.driftDetected = driftDetected;
        this.createdAt = createdAt;
        this.impactedDocumentation = impactedDocumentation;
    }

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(name = "source_commit_sha", nullable = false)
    private String sourceCommitSha;

    @Column(name = "drift_detected", nullable = false)
    private boolean driftDetected;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ElementCollection
    @CollectionTable(
            name = "analysis_impacted_documentation",
            joinColumns = @JoinColumn(name = "analysis_id")
    )
    private List<ImpactedDocumentation> impactedDocumentation;
}
