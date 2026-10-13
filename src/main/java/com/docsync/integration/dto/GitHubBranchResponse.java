package com.docsync.integration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubBranchResponse {

    private Commit commit;

    @Getter
    @Setter
    public static class Commit {
        private String sha;
    }
}