package com.docsync.service;

import com.docsync.dto.ConnectRepositoryRequest;
import com.docsync.entity.Repository;
import com.docsync.entity.RepositoryConnection;
import com.docsync.entity.RepositoryConnectionId;
import com.docsync.entity.User;
import com.docsync.repository.RepositoryConnectionRepository;
import com.docsync.repository.RepositoryRepository;
import com.docsync.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RepositoryConnectionService {
    private final RepositoryConnectionRepository repositoryConnectionRepository;
    private final UserRepository userRepository;
    private final RepositoryRepository repositoryRepository;

    public RepositoryConnectionService(RepositoryConnectionRepository repositoryConnectionRepository, UserRepository userRepository, RepositoryRepository repositoryRepository) {
        this.repositoryConnectionRepository = repositoryConnectionRepository;
        this.userRepository = userRepository;
        this.repositoryRepository = repositoryRepository;
    }

    public void connectRepository(UUID userId, ConnectRepositoryRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Repository repository = repositoryRepository.
                findByGithubRepositoryId(request.getGithubRepositoryId()).
                orElseThrow(() -> new RuntimeException("Repository not found"));

        RepositoryConnectionId repositoryConnectionId = new RepositoryConnectionId(user.getId(), repository.getId());
        RepositoryConnection repositoryConnection = new
                RepositoryConnection(repositoryConnectionId, Instant.now(), user, repository);

        repositoryConnectionRepository.save(repositoryConnection);
    }
}
