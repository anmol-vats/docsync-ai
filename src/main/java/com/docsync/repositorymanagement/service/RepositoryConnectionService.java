package com.docsync.repositorymanagement.service;

import com.docsync.repositorymanagement.dto.ConnectRepositoryRequest;
import com.docsync.repositorymanagement.dto.ConnectRepositoryResponse;
import com.docsync.repositorymanagement.entity.Repository;
import com.docsync.repositorymanagement.entity.RepositoryConnection;
import com.docsync.repositorymanagement.entity.RepositoryConnectionId;
import com.docsync.user.entity.User;
import com.docsync.exception.RepositoryAlreadyConnectedException;
import com.docsync.exception.RepositoryNotFoundException;
import com.docsync.exception.UserNotFoundException;
import com.docsync.integration.github.GitHubClient;
import com.docsync.integration.github.GitHubRepositoryResponse;
import com.docsync.repositorymanagement.repository.RepositoryConnectionRepository;
import com.docsync.repositorymanagement.repository.RepositoryRepository;
import com.docsync.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RepositoryConnectionService {
    private final RepositoryConnectionRepository repositoryConnectionRepository;
    private final UserRepository userRepository;
    private final RepositoryRepository repositoryRepository;
    private final GitHubClient gitHubClient;

    public RepositoryConnectionService(RepositoryConnectionRepository repositoryConnectionRepository, UserRepository userRepository, RepositoryRepository repositoryRepository, GitHubClient gitHubClient) {
        this.repositoryConnectionRepository = repositoryConnectionRepository;
        this.userRepository = userRepository;
        this.repositoryRepository = repositoryRepository;
        this.gitHubClient = gitHubClient;
    }

    public ConnectRepositoryResponse connectRepository(UUID userId, ConnectRepositoryRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        GitHubRepositoryResponse gitHubRepository = gitHubClient
                .getRepository(request.getOwner(), request.getRepo()).
                orElseThrow(() -> new RepositoryNotFoundException("Github repository not found"));

        Repository repository = repositoryRepository.
                findByGithubRepositoryId(gitHubRepository.getId())
                .orElseGet(() -> {
                    Repository newRepository = new Repository(
                            UUID.randomUUID(),
                            gitHubRepository.getId(),
                            gitHubRepository.getName(),
                            gitHubRepository.getUrl(),
                            gitHubRepository.getDefaultBranch(),
                            Instant.now(),
                            request.getOwner()
                    );
                    return repositoryRepository.save(newRepository);
                });
        boolean alreadyConnected = repositoryConnectionRepository.existsByIdUserIdAndIdRepositoryId(userId, repository.getId());

        if(alreadyConnected){
            throw new RepositoryAlreadyConnectedException("Repository already connected");
        }
        RepositoryConnectionId repositoryConnectionId = new RepositoryConnectionId(user.getId(), repository.getId());
        RepositoryConnection repositoryConnection = new
                RepositoryConnection(repositoryConnectionId, Instant.now(), user, repository);

        repositoryConnectionRepository.save(repositoryConnection);

        ConnectRepositoryResponse response = new ConnectRepositoryResponse();
        response.setRepositoryId(repository.getId());
        response.setName(repository.getName());
        response.setUrl(repository.getUrl());
        response.setDefaultBranch(repository.getDefaultBranch());
        response.setMessage("Repository connected successfully");
        return response;
    }
}
