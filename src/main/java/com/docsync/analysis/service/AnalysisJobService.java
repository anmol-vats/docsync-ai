package com.docsync.analysis.service;

import com.docsync.analysis.entity.AnalysisJob;
import com.docsync.analysis.entity.AnalysisJobStatus;
import com.docsync.analysis.repository.AnalysisJobRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AnalysisJobService {
    private final AnalysisJobRepository analysisJobRepository;

    public AnalysisJobService(AnalysisJobRepository analysisJobRepository) {
        this.analysisJobRepository = analysisJobRepository;
    }

    @Transactional
    public Optional<AnalysisJob> claimNextPendingJob(){
        Optional<AnalysisJob> job = analysisJobRepository
                .findFirstByStatusOrderByCreatedAtAsc(AnalysisJobStatus.PENDING);

        if(job.isEmpty()){
            return Optional.empty();
        }

        AnalysisJob analysisJob = job.get();
        analysisJob.markRunning(Instant.now());
        return Optional.of(analysisJob);
    }
}
