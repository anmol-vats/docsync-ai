package com.docsync.analysis.repository;

import com.docsync.analysis.entity.AnalysisJob;
import com.docsync.analysis.entity.AnalysisJobStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AnalysisJob> findFirstByStatusOrderByCreatedAtAsc(
            AnalysisJobStatus status
    );
}
