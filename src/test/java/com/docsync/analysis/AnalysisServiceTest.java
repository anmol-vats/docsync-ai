package com.docsync.analysis;

import com.docsync.analysis.entity.AnalysisJob;
import com.docsync.analysis.entity.AnalysisJobStatus;
import com.docsync.analysis.repository.AnalysisJobRepository;
import com.docsync.analysis.service.AnalysisJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalysisServiceTest {
    @Mock
    private AnalysisJobRepository analysisJobRepository;

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
}
