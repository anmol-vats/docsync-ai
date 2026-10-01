package com.docsync.analysis.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ImpactedDocumentation {

    private String documentationPath;

    private String reason;
}