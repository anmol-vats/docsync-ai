package com.docsync.repository;

import com.docsync.entity.RepositoryConnection;
import com.docsync.entity.RepositoryConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositoryConnectionRepository extends JpaRepository<RepositoryConnection, RepositoryConnectionId> {
    boolean existsByIdUserIdAndIdRepositoryId(
            UUID userId,
            UUID repositoryId
    );
}
