package com.docsync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "repositories")
public class Repository {
    protected Repository() {}
    public Repository(UUID id,
                      Long githubRepositoryId,
                      String name,
                      String url,
                      String defaultBranch,
                      Instant createdAt) {
        this.id = id;
        this.githubRepositoryId = githubRepositoryId;
        this.name = name;
        this.url = url;
        this.defaultBranch = defaultBranch;
        this.createdAt = createdAt;
    }


    @Id
    private UUID id;

    @Column(name = "github_repository_id",  nullable = false, unique = true)
    private Long githubRepositoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(name = "default_branch", nullable = false)
    private String defaultBranch;

    @Column(name = "created_at",nullable = false)
    private Instant createdAt;
}
