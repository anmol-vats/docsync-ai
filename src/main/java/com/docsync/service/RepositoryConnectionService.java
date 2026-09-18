package com.docsync.service;

import com.docsync.dto.ConnectRepositoryRequest;
import com.docsync.entity.Repository;
import com.docsync.entity.RepositoryConnection;
import com.docsync.entity.RepositoryConnectionId;
import com.docsync.entity.User;
import com.docsync.integration.github.GitHubClient;
import com.docsync.integration.github.GitHubRepositoryResponse;
import com.docsync.repository.RepositoryConnectionRepository;
import com.docsync.repository.RepositoryRepository;
import com.docsync.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
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

    public void connectRepository(UUID userId, ConnectRepositoryRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        GitHubRepositoryResponse gitHubRepository = gitHubClient
                .getRepository(request.getOwner(), request.getRepo()).
                orElseThrow(() -> new RuntimeException("Github repository not found"));

        Repository repository = repositoryRepository.
                findByGithubRepositoryId(gitHubRepository.getId())
                .orElseGet(() -> {
                    Repository newRepository = new Repository(
                            UUID.randomUUID(),
                            gitHubRepository.getId(),
                            gitHubRepository.getName(),
                            gitHubRepository.getUrl(),
                            gitHubRepository.getDefaultBranch(),
                            Instant.now()
                    );
                    return repositoryRepository.save(newRepository);
                });

        boolean alreadyConnected = repositoryConnectionRepository.existsByIdUserIdAndIdRepositoryId(userId, repository.getId());
        if(alreadyConnected){
            throw new RuntimeException("Repository already connected");
        }
        RepositoryConnectionId repositoryConnectionId = new RepositoryConnectionId(user.getId(), repository.getId());
        RepositoryConnection repositoryConnection = new
                RepositoryConnection(repositoryConnectionId, Instant.now(), user, repository);

        repositoryConnectionRepository.save(repositoryConnection);
    }
}
