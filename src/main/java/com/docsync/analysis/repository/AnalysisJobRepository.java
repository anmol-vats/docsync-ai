package com.docsync.analysis.repository;

import com.docsync.analysis.entity.AnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, UUID> {
}
