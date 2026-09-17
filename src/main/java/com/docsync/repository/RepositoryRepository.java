package com.docsync.repository;

import com.docsync.entity.Repository;
import com.docsync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RepositoryRepository extends JpaRepository<Repository, UUID> {
    Optional<Repository> findByGithubRepositoryId(Long githubRepositoryId);
}
