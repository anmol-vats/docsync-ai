package com.docsync.service;

public interface PasswordHasher {
    String hash(String password);
    boolean matches(String rawPassword, String hashedPassword);
}
