package com.docsync.service;

import com.docsync.dto.ConnectRepositoryRequest;
import com.docsync.dto.ConnectRepositoryResponse;
import com.docsync.entity.Repository;
import com.docsync.entity.RepositoryConnection;
import com.docsync.entity.User;
import com.docsync.exception.RepositoryNotFoundException;
import com.docsync.integration.github.GitHubClient;
import com.docsync.integration.github.GitHubRepositoryResponse;
import com.docsync.repository.RepositoryConnectionRepository;
import com.docsync.repository.RepositoryRepository;
import com.docsync.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepositoryConnectionServiceTest {
    private RepositoryConnectionService repositoryConnectionService;
    @BeforeEach
    void setUp() {
        repositoryConnectionService = new RepositoryConnectionService(
                repositoryConnectionRepository,
                userRepository,
                repositoryRepository,
                gitHubClient
        );
    }

    @Mock
    private RepositoryConnectionRepository repositoryConnectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RepositoryRepository repositoryRepository;

    @Mock
    private GitHubClient gitHubClient;

    @Test
    void shouldConnectRepositorySuccessfully(){
        UUID userId =  UUID.randomUUID();
        User user = new User(userId, "anmol@example.com", "test12345", false, Instant.now());
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        GitHubRepositoryResponse response = new GitHubRepositoryResponse();
        response.setName("Hello-World");
        response.setUrl("https://github.com/octocat/Hello-World");
        response.setId(123456L);
        response.setDefaultBranch("master");

        when(gitHubClient.getRepository("octocat", "Hello-World"))
                .thenReturn(Optional.of(response));

        when(repositoryRepository.findByGithubRepositoryId(response.getId()))
                .thenReturn(Optional.empty());

        Repository repository = new Repository(
                UUID.randomUUID(),
                response.getId(),
                response.getName(),
                response.getUrl(),
                response.getDefaultBranch(),
                Instant.now()
        );

        when(repositoryRepository.save(any(Repository.class)))
                .thenReturn(repository);

        when(repositoryConnectionRepository
                .existsByIdUserIdAndIdRepositoryId(
                        userId,
                        repository.getId()
                ))
                .thenReturn(false);

        ConnectRepositoryRequest request = new ConnectRepositoryRequest();
        request.setOwner("octocat");
        request.setRepo("Hello-World");

        ConnectRepositoryResponse result =
                repositoryConnectionService.connectRepository(userId, request);

        assertEquals(repository.getId(), result.getRepositoryId());
        assertEquals(repository.getName(), result.getName());
        assertEquals(repository.getUrl(), result.getUrl());
        assertEquals(repository.getDefaultBranch(), result.getDefaultBranch());
        assertEquals("Repository connected successfully", result.getMessage());

        verify(repositoryConnectionRepository)
                .save(any(RepositoryConnection.class));
    }

    @Test
    void shouldThrowWhenGithubRepositoryNotFound(){
        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                "anmol@example.com",
                "test12345",
                false,
                Instant.now()
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        ConnectRepositoryRequest request = new ConnectRepositoryRequest();
        request.setOwner("octocat");
        request.setRepo("DoesNotExist");

        when(gitHubClient.getRepository("octocat", "DoesNotExist"))
                .thenReturn(Optional.empty());

        assertThrows(
                RepositoryNotFoundException.class,
                () -> repositoryConnectionService.connectRepository(userId, request)
        );

        verify(repositoryRepository, never())
                .save(any(Repository.class));

        verify(repositoryConnectionRepository, never())
                .save(any(RepositoryConnection.class));

    }


}
