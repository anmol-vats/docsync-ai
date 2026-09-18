package com.docsync.exception;

public class RepositoryAlreadyConnectedException extends RuntimeException {
    public RepositoryAlreadyConnectedException(String message) {
        super(message);
    }
}
