package com.docsync.integration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubCommitFileResponse {
    private String filename;
    private String status;
    private Integer additions;
    private Integer deletions;
    private Integer changes;
}
