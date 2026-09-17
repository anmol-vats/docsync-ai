package com.docsync.repository;

import com.docsync.entity.RepositoryConnection;
import com.docsync.entity.RepositoryConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryConnectionRepository extends JpaRepository<RepositoryConnection, RepositoryConnectionId> {
}
