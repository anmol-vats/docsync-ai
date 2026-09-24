package com.docsync.analysis;

import com.docsync.analysis.entity.AnalysisJob;
import com.docsync.analysis.service.AnalysisExecutor;
import com.docsync.analysis.service.AnalysisJobService;
import com.docsync.analysis.service.AnalysisJobWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalysisJobWorkerTest {
    @Mock
    private AnalysisJobService analysisJobService;

    @Mock
    private AnalysisExecutor analysisExecutor;

    private AnalysisJobWorker analysisJobWorker;

    @BeforeEach
    void setUp() {
        analysisJobWorker =
                new AnalysisJobWorker(analysisJobService, analysisExecutor);
    }

    @Test
    void shouldExecuteJobAndMarkItSuccessful() {
        AnalysisJob job = mock(AnalysisJob.class);
        UUID jobId = UUID.randomUUID();

        when(job.getId()).thenReturn(jobId);

        when(analysisJobService.claimNextPendingJob())
                .thenReturn(Optional.of(job));
        analysisJobWorker.processNextJob();
        verify(analysisExecutor).execute(job);
        verify(analysisJobService).markJobSuccess(jobId);
    }

    @Test
    void shouldMarkJobFailedWhenExecutionThrowsException(){
        AnalysisJob job = mock(AnalysisJob.class);
        UUID jobId = UUID.randomUUID();

        when(job.getId()).thenReturn(jobId);

        when(analysisJobService.claimNextPendingJob())
                .thenReturn(Optional.of(job));

        doThrow(new RuntimeException("Analysis failed"))
                .when(analysisExecutor)
                .execute(job);

        analysisJobWorker.processNextJob();
        verify(analysisJobService).markJobFailed(jobId, "Analysis failed");
    }

    @Test
    void shouldDoNothingWhenNoPendingJobExists(){
        when(analysisJobService.claimNextPendingJob())
                .thenReturn(Optional.empty());

        analysisJobWorker.processNextJob();

        verifyNoInteractions(analysisExecutor);
    }
}
