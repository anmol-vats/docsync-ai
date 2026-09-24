package com.docsync.analysis.service;

import com.docsync.analysis.entity.AnalysisJob;

public interface AnalysisExecutor {
    void execute(AnalysisJob job);
}
