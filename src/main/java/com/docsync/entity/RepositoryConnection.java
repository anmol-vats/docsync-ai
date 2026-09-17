package com.docsync.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "repository_connections")
public class RepositoryConnection {

    protected RepositoryConnection() {}
    public RepositoryConnection(RepositoryConnectionId id, Instant connectedAt) {
        this.id = id;
        this.connectedAt = connectedAt;
    }

    @EmbeddedId
    private RepositoryConnectionId id;

    @Column(name = "connected_at",  nullable = false)
    private Instant connectedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("repositoryId")
    @JoinColumn(name = "repository_id",  nullable = false)
    private Repository repository;
}
