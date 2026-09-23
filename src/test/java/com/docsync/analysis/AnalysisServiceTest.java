package com.docsync.analysis;

import com.docsync.analysis.entity.AnalysisJob;
import com.docsync.analysis.entity.AnalysisJobStatus;
import com.docsync.analysis.repository.AnalysisJobRepository;
import com.docsync.analysis.service.AnalysisJobService;
import com.docsync.webhook.entity.WebhookEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class AnalysisServiceTest {
    @Mock
    private AnalysisJobRepository analysisJobRepository;

    @Mock
    private WebhookEvent webhookEvent;

    private AnalysisJobService analysisJobService;


    @BeforeEach
    public void setUp(){
        analysisJobService = new AnalysisJobService(analysisJobRepository);
    }

    @Test
    void shouldReturnEmptyWhenNoPendingJobExists(){
        when(analysisJobRepository
                .findFirstByStatusOrderByCreatedAtAsc(AnalysisJobStatus.PENDING))
                .thenReturn(Optional.empty());

        Optional<AnalysisJob> result = analysisJobService.claimNextPendingJob();
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldClaimPendingJobAndMarkItRunning(){
        AnalysisJob job = new AnalysisJob(
                UUID.randomUUID(),
                webhookEvent,
                AnalysisJobStatus.PENDING,
                0,
                null,
                Instant.now(),
                null,
                null
        );

        when(analysisJobRepository
                .findFirstByStatusOrderByCreatedAtAsc(AnalysisJobStatus.PENDING))
                .thenReturn(Optional.of(job));

        Optional<AnalysisJob> result =
                analysisJobService.claimNextPendingJob();
        assertTrue(result.isPresent());
        AnalysisJob claimedJob = result.get();
        assertEquals(AnalysisJobStatus.RUNNING, claimedJob.getStatus());
        assertEquals(1, claimedJob.getAttemptCount());
        assertNotNull(claimedJob.getStartedAt());
    }
}
