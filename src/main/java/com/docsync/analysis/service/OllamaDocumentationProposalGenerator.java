
package com.docsync.analysis.service;

import org.springframework.stereotype.Service;

@Service
public class OllamaDocumentationProposalGenerator
        implements DocumentationProposalGenerator {

    private final OllamaClient ollamaClient;

    public OllamaDocumentationProposalGenerator(
            OllamaClient ollamaClient
    ) {
        this.ollamaClient = ollamaClient;
    }

    @Override
    public String generateProposal(
            String documentationPath,
            String originalContent,
            String codeChanges
    ) {
        String prompt = """
                You are a technical documentation engineer.

                Update the existing documentation based on the
                supplied code changes.

                Rules:
                - Preserve accurate existing information.
                - Make only changes supported by the code evidence.
                - Do not invent endpoints, behavior, or configuration.
                - Return only the complete updated Markdown document.
                - Do not include explanations or Markdown code fences.

                Documentation path:
                %s

                Existing documentation:
                <documentation>
                %s
                </documentation>

                Code changes:
                <code_changes>
                %s
                </code_changes>

                Updated documentation:
                """.formatted(
                documentationPath,
                originalContent,
                codeChanges
        );

        return ollamaClient.generate(prompt);
    }
}
