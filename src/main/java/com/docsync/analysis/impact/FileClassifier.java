package com.docsync.analysis.impact;

import org.springframework.stereotype.Service;

@Service
public class FileClassifier {
    public FileType classify(String filePath) {
        if(filePath.endsWith(".java")) {
            return FileType.CODE;
        }else if(filePath.endsWith(".md")) {
            return FileType.DOCUMENTATION;
        }else{
            return FileType.OTHER;
        }
    }
}
