package com.docsync.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class RepositoryConnectionId {
    protected RepositoryConnectionId() {}
    public RepositoryConnectionId(UUID userId, UUID repositoryId) {
        this.userId=userId;
        this.repositoryId=repositoryId;
    }

    private UUID userId;

    private UUID repositoryId;
}
