package com.github.sourcecity.application;

import java.util.Base64;

public class RepositoryId {

    public static String generate(String repositoryUrl) {
        return Base64.getEncoder().encodeToString(repositoryUrl.getBytes());
    }
}
