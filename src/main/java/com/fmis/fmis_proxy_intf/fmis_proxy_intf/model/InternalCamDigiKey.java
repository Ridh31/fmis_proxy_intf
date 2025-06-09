package com.fmis.fmis_proxy_intf.fmis_proxy_intf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

/**
 * Entity representing an internal FMIS system integrated with CamDigiKey.
 */
@Entity
@Table(name = "internal_camdigikey")
public class InternalCamDigiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotEmpty(message = "Name cannot be empty. Please provide a valid name.")
    private String name;

    @Column(name = "app_key", nullable = false, unique = true)
    @NotEmpty(message = "App key cannot be empty. Please provide a valid app key.")
    private String appKey;

    @Column(name = "ip_address", nullable = false, unique = true)
    @NotEmpty(message = "IP Address cannot be empty. Please provide a valid IP Address.")
    private String ipAddress;

    @Column(name = "access_url", nullable = false, unique = true)
    @NotEmpty(message = "Access URL cannot be empty. Please provide a valid Access URL.")
    private String accessURL;

    @Column(name = "created_by")
    @JsonIgnore
    private Integer createdBy;

    @Column(name = "created_date")
    @JsonIgnore
    private LocalDateTime createdDate = LocalDateTime.now();

    @JsonIgnore
    private Boolean status = true;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAccessURL() {
        return accessURL;
    }

    public void setAccessURL(String accessURL) {
        this.accessURL = accessURL;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}