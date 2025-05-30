package com.example.quiz2.DTO;

public class LoginResponse {
    private String token;
    private String userRole;

    // Default constructor
    public LoginResponse() {
    }

    public LoginResponse(String token, String userRole) {
        this.token = token;
        this.userRole = userRole;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
