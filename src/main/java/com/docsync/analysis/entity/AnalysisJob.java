package com.docsync.analysis.entity;

import com.docsync.webhook.entity.WebhookEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "analysis_jobs")
public class AnalysisJob {
    protected AnalysisJob() {}
    public AnalysisJob(UUID id, WebhookEvent webhookEvent, AnalysisJobStatus status, Integer attemptCount,
                       String lastError, Instant createdAt, Instant startedAt, Instant completedAt) {
        this.id = id;
        this.webhookEvent = webhookEvent;
        this.status = status;
        this.attemptCount = attemptCount;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public void markRunning(Instant startedAt){
        this.status = AnalysisJobStatus.RUNNING;
        this.attemptCount++;
        this.startedAt = startedAt;
    }


    @Id
    @Column(nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "webhook_event_id", nullable = false, unique = true)
    private WebhookEvent webhookEvent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisJobStatus status;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    private String lastError;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private Instant startedAt;

    private Instant completedAt;
}
