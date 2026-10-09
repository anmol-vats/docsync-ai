package com.docsync.analysis.entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    @EntityGraph(attributePaths = "repository")
    Optional<Analysis> findWithRepositoryById(UUID id);
}