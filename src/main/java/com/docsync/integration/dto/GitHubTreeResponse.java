package com.docsync.integration.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GitHubTreeResponse {
    List<GitHubTreeItemResponse> tree;
}
