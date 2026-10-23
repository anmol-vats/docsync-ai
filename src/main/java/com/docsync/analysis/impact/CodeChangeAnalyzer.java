package com.docsync.analysis.impact;

import org.springframework.stereotype.Service;

@Service
public class CodeChangeAnalyzer {

    public boolean canAffectDocumentation(String patch) {
        if (patch == null || patch.isBlank()) {
            return false;
        }

        String[] lines = patch.split("\n");
        boolean foundMeaningfulChange = false;

        for (String line : lines) {
            if (!isChangedCodeLine(line)) {
                continue;
            }

            String changedLine = line.substring(1).trim();

            if (changedLine.isBlank() || isIgnorableChange(changedLine)) {
                continue;
            }

            foundMeaningfulChange = true;
        }

        return foundMeaningfulChange;
    }

    private boolean isIgnorableChange(String line) {
        return line.contains("System.out.println(")
                || line.contains("System.err.println(")
                || line.matches(".*\\b(log|logger)\\.(debug|info|warn|error|trace)\\s*\\(.*");
    }

    private boolean isChangedCodeLine(String line) {
        return (line.startsWith("+") && !line.startsWith("+++"))
                || (line.startsWith("-") && !line.startsWith("---"));
    }
}
