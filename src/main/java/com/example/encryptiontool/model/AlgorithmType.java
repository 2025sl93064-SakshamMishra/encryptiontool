package com.example.encryptiontool.model;

public enum AlgorithmType {
    AES_128("AES-128-CBC"),
    AES_256("AES-256-CBC"),
    TRIPLE_DES("3DES"),
    RSA("RSA-2048");

    private final String displayName;

    AlgorithmType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
