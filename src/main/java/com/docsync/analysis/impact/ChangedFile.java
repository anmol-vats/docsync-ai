package com.docsync.analysis.impact;

public record ChangedFile(
        String path,
        String patch
) {}