package com.docsync.analysis.repository;

import com.docsync.analysis.entity.DocumentationProposal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentationProposalRepository extends JpaRepository<DocumentationProposal, UUID> {
    @EntityGraph(attributePaths = {"analysis", "analysis.repository"})
    Optional<DocumentationProposal> findWithAnalysisAndRepositoryById(UUID id);
}
