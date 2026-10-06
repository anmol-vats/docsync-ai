package com.docsync.analysis.impact;

import com.docsync.analysis.entity.ImpactedDocumentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentationImpactAnalyzer {
    private final FileClassifier fileClassifier;

    public DocumentationImpactAnalyzer(FileClassifier fileClassifier) {
        this.fileClassifier = fileClassifier;
    }

    public List<ImpactedDocumentation> findImpactedDocumentation(
            List<String> changedFiles,
            List<String> repositoryFiles) {

        List<String> codeFiles = new ArrayList<>();
        List<String> codeDomains = new ArrayList<>();

        List<ImpactedDocumentation> impactedDocumentation = new ArrayList<>();

        for (String changedFile : changedFiles) {
            FileType fileType = fileClassifier.classify(changedFile);

            if (fileType == FileType.CODE) {
                codeFiles.add(changedFile);
            }
        }

        for (String codeFile : codeFiles) {
            String className = extractClassName(codeFile);
            String domain = extractDomain(className);
            System.out.println("Code domain: " + domain);
            codeDomains.add(domain);
        }

        for (String repositoryFile : repositoryFiles) {

            FileType fileType = fileClassifier.classify(repositoryFile);

            if (fileType != FileType.DOCUMENTATION) {
                continue;
            }

            String documentationName =
                    extractDocumentationName(repositoryFile);
            System.out.println("Documentation name: " + documentationName);

            for (String domain : codeDomains) {

                if (documentationName
                        .toLowerCase()
                        .contains(domain.toLowerCase())) {

                    impactedDocumentation.add(
                            new ImpactedDocumentation(
                                    repositoryFile,
                                    "Documentation potentially impacted by code change"
                            )
                    );

                    break;
                }
            }
        }

        return impactedDocumentation;
    }

    private String extractDocumentationName(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private String extractClassName(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private String extractDomain(String className) {

        if (className.endsWith("Service")) {
            return className.substring(
                    0,
                    className.length() - "Service".length()
            );
        }

        if (className.endsWith("Controller")) {
            return className.substring(
                    0,
                    className.length() - "Controller".length()
            );
        }

        return className;
    }
}
