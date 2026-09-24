package com.docsync.analysis.service;

import com.docsync.analysis.entity.AnalysisJob;
import org.springframework.stereotype.Service;

@Service
public class FakeAnalysisExecutor implements AnalysisExecutor{

    @Override
    public void execute(AnalysisJob job) {

    }
}
