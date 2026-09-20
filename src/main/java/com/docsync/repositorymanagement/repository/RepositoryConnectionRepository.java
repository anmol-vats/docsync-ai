package com.docsync.repositorymanagement.repository;

import com.docsync.repositorymanagement.entity.RepositoryConnection;
import com.docsync.repositorymanagement.entity.RepositoryConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositoryConnectionRepository extends JpaRepository<RepositoryConnection, RepositoryConnectionId> {
    boolean existsByIdUserIdAndIdRepositoryId(
            UUID userId,
            UUID repositoryId
    );
}
