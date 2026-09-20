package com.docsync.webhook.entity;

import com.docsync.repositorymanagement.entity.Repository;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "webhook_events")
public class WebhookEvent {
    protected WebhookEvent() {}

    public WebhookEvent(
            UUID id,
            String deliveryId,
            String eventType,
            Repository repository,
            String payload,
            Instant receivedAt,
            WebhookEventStatus status
    ) {
        this.id = id;
        this.deliveryId = deliveryId;
        this.eventType = eventType;
        this.repository = repository;
        this.payload = payload;
        this.receivedAt = receivedAt;
        this.status = status;
    }

    @Id
    private UUID id;

    @Column(name = "delivery_id", nullable = false, unique = true)
    private String deliveryId;

    @Column(name = "event_type",  nullable = false)
    private String eventType;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(name = "payload",  nullable = false)
    private String payload;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookEventStatus status;
}
