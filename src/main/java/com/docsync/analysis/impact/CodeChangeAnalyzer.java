package com.docsync.analysis.impact;

import org.springframework.stereotype.Service;

@Service
public class CodeChangeAnalyzer {
    public boolean canAffectDocumentation(String patch) {

        if (patch == null || patch.isBlank()) {
            return false;
        }

        String[] lines = patch.split("\n");

        for (String line : lines) {

            if (!isChangedCodeLine(line)) {
                continue;
            }

            String changedLine = line.substring(1).trim();

            if (isIgnorableChange(changedLine)) {
                continue;
            }

            return true;
        }

        return false;
    }

    private boolean isIgnorableChange(String line) {
        return line.contains("System.out.println");
    }

    private boolean isChangedCodeLine(String line) {
        return (line.startsWith("+") && !line.startsWith("+++"))
                || (line.startsWith("-") && !line.startsWith("---"));
    }
}
