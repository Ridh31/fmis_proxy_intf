package com.fmis.fmis_proxy_intf.fmis_proxy_intf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entity representing a user.
 */
@Entity
@Data
@Getter
@Setter
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @Schema(hidden = true)
    private Role role;  // Reference to Role

    @ManyToOne
    @JoinColumn(name = "partner_intf_id", referencedColumnName = "id")
    @Schema(hidden = true)
    private Partner partner;  // Reference to Partner

    @Column(nullable = false, unique = true)
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters. Please provide a valid username.")
    @NotEmpty(message = "Username cannot be empty. Please provide a valid username.")
    private String username;

    @Column(nullable = false)
    @Size(min = 6, max = 64, message = "Password must be at least 6 characters long. Please provide a stronger password.")
    @NotEmpty(message = "Password cannot be empty. Please enter a password.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Schema(hidden = true)
    private String email;

    @Column(nullable = false)
    @Schema(hidden = true)
    @JsonIgnore
    private boolean enabled = true;

    @Column(name = "account_non_expired", nullable = false)
    @Schema(hidden = true)
    @JsonIgnore
    private boolean accountNonExpired = true;

    @Column(name = "credentials_non_expired", nullable = false)
    @Schema(hidden = true)
    @JsonIgnore
    private boolean credentialsNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    @Schema(hidden = true)
    @JsonIgnore
    private boolean accountNonLocked = true;

    @Column(name = "created_date", nullable = false, updatable = false)
    @Schema(hidden = true)
    @JsonIgnore
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_modified_date", nullable = false)
    @Schema(hidden = true)
    @JsonIgnore
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}