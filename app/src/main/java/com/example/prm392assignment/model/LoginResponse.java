package com.example.prm392assignment.model;

public class LoginResponse {
    private String authToken;
    private String userId;

    // Getters and setters
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

