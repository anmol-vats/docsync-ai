package com.docsync.analysis.service;

import org.springframework.stereotype.Service;

@Service
public class DeterministicDocumentationProposalGenerator implements DocumentationProposalGenerator{
    @Override
    public String generateProposal(
            String documentationPath,
            String originalContent,
            String codeChanges
    ) {
        return originalContent
                + "\n\n<!-- DocSync proposal for "
                + documentationPath
                + " -->";
    }
}
