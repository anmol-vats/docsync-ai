package com.docsync.analysis.service;

public interface DocumentationProposalGenerator {
    String generateProposal(
            String documentationPath,
            String originalContent,
            String codeChanges
    );
}
