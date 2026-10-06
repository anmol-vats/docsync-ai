package com.docsync.integration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubTreeItemResponse {
    private String path;
    private String type;
}
