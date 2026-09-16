package com.docsync.service;

public interface PasswordHasher {
    String hash(String password);
}
