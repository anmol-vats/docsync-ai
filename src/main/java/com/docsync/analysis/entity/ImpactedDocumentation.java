package com.docsync.analysis.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class ImpactedDocumentation {

    private String documentationPath;

    private String reason;

    protected ImpactedDocumentation() {
    }

    public ImpactedDocumentation(String documentationPath, String reason) {
        this.documentationPath = documentationPath;
        this.reason = reason;
    }
}