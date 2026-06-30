package com.example.saive.admin.data.model;

import com.google.firebase.database.PropertyName;

/**
 * Maps 1:1 to the real "Users" node in Firebase Realtime Database.
 * NOTE: field names in the DB are lowerCamelCase (email, password, fullname,
 * role, provider, createdAt, avatarUrl) - NOT the same as the rest of the
 * admin models which use PascalCase. Do not "fix" the casing here, it must
 * match the database exactly or values will come back null.
 */
public class AdminUser {
    private String userId; // populated from the Firebase node key, see FirebaseConnector

    @PropertyName("Email")
    private String email;

    @PropertyName("Password")
    private String password;

    @PropertyName("DisplayName")
    private String fullname;

    @PropertyName("Role")
    private String role;

    @PropertyName("Provider")
    private String provider;

    @PropertyName("CreatedAt")
    private String createdAt;

    @PropertyName("AvatarUrl")
    private String avatarUrl;

    @PropertyName("IsActive")
    private boolean isActive = true;

    @PropertyName("Phone")
    private String phone;

    @PropertyName("UpdatedAt")
    private String updatedAt;

    public AdminUser() {
        // Required for Firebase
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("Email")
    public String getEmail() { return email; }
    @PropertyName("Email")
    public void setEmail(String email) { this.email = email; }

    @PropertyName("Password")
    public String getPassword() { return password; }
    @PropertyName("Password")
    public void setPassword(String password) { this.password = password; }

    @PropertyName("DisplayName")
    public String getFullname() { return fullname; }
    @PropertyName("DisplayName")
    public void setFullname(String fullname) { this.fullname = fullname; }

    @PropertyName("Role")
    public String getRole() { return role; }
    @PropertyName("Role")
    public void setRole(String role) { this.role = role; }

    @PropertyName("Provider")
    public String getProvider() { return provider; }
    @PropertyName("Provider")
    public void setProvider(String provider) { this.provider = provider; }


    @PropertyName("CreatedAt")
    public String getCreatedAt() { return createdAt; }
    @PropertyName("CreatedAt")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @PropertyName("AvatarUrl")
    public String getAvatarUrl() { return avatarUrl; }
    @PropertyName("AvatarUrl")
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    @PropertyName("IsActive")
    public boolean isActive() { return isActive; }
    @PropertyName("IsActive")
    public void setActive(boolean active) { this.isActive = active; }

    @PropertyName("Phone")
    public String getPhone() { return phone; }
    @PropertyName("Phone")
    public void setPhone(String phone) { this.phone = phone; }

    @PropertyName("UpdatedAt")
    public String getUpdatedAt() { return updatedAt; }
    @PropertyName("UpdatedAt")
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @com.google.firebase.database.Exclude
    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("admin");
    }
}