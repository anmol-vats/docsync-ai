package com.docsync.integration.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubRepositoryResponse {
    private Long id;
    private String name;
    private String url;

    @JsonProperty("default_branch")
    private String defaultBranch;
}
