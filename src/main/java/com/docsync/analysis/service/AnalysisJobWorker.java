package com.docsync.analysis.service;

import com.docsync.analysis.entity.AnalysisJob;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnalysisJobWorker {
    private final AnalysisJobService analysisJobService;
    private final AnalysisExecutor analysisExecutor;
    public AnalysisJobWorker(AnalysisJobService analysisJobService, AnalysisExecutor analysisExecutor) {
        this.analysisJobService = analysisJobService;
        this.analysisExecutor = analysisExecutor;
    }

    public void processNextJob(){
        Optional<AnalysisJob> job = analysisJobService.claimNextPendingJob();
        if (job.isEmpty()) {
            return;
        }
        AnalysisJob analysisJob = job.get();

        try {
            analysisExecutor.execute(analysisJob);
            analysisJobService.markJobSuccess(analysisJob.getId());
        } catch (Exception e) {
            analysisJobService.markJobFailed(
                    analysisJob.getId(),
                    e.getMessage()
            );
        }

    }
}
