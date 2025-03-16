package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for User.
 * Used for transferring user-related data between layers.
 */
public class UserDTO {

    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters. Please provide a valid username.")
    @NotEmpty(message = "Username cannot be empty. Please provide a valid username.")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters long. Please provide a stronger password.")
    @NotEmpty(message = "Password cannot be empty. Please enter a password.")
    private String password;

    @NotNull(message = "Role ID cannot be empty. Please enter a role ID.")
    @Min(value = 1, message = "Role ID must be a positive number.")
    private Long roleId;

    @NotNull(message = "Partner ID cannot be empty. Please enter a partner ID.")
    @Min(value = 1, message = "Partner ID must be a positive number.")
    private Long partnerId;

    private String email;

    // Constructors
    public UserDTO() {}

    public UserDTO(String username, String password, Long roleId, Long partnerId, String email) {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.partnerId = partnerId;
        this.email = email;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}