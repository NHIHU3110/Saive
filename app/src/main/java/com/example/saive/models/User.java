package com.example.saive.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private String avatarUrl;

    public User(String id, String name, String email, String role, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
