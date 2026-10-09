package com.docsync.integration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubContentResponse {
    private String content;
    private String encoding;
}