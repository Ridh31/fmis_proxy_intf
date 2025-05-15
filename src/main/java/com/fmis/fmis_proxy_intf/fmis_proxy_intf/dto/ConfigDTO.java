package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating FMIS configuration.
 */
public class ConfigDTO {

    @NotEmpty(message = "Base URL cannot be empty. Please provide a valid Base URL.")
    private String baseURL;

    @NotEmpty(message = "Username cannot be empty. Please provide a valid username.")
    private String username;

    @Size(min = 6, max = 64, message = "Password must be at least 6 characters long. Please provide a stronger password.")
    @NotEmpty(message = "Password cannot be empty. Please enter a password.")
    private String password;
    private String contentType;
    private String description;

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}