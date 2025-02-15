package com.fmis.fmis_proxy_intf.fmis_proxy_intf.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entity representing a partner.
 */
@Entity
@Data
@Getter
@Setter
@Table(name = "partner_intf", uniqueConstraints = @UniqueConstraint(columnNames = {"base64", "sha256", "rsa_public_key", "rsa_private_key"}))
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    private String description;

    @Lob
    @NotNull(message = "Code cannot be null")
    @Column(nullable = false, unique = true)
    private String code;

    @Lob
    @Column(name = "base64", nullable = false, unique = true)
    private String base64;

    @Lob
    @Column(name = "sha256", nullable = false, unique = true)
    private String sha256;

    @Lob
    @Column(name = "rsa_public_key", nullable = false, unique = true)
    private String rsaPublicKey;

    @Lob
    @Column(name = "rsa_private_key", nullable = false, unique = true)
    private String rsaPrivateKey;

    private Long createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    private Boolean status = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // Constructors
    public Partner() {
    }

    /**
     * Custom constructor for creating a new Partner entity.
     *
     * @param name         The name of the partner.
     * @param description  The partner's description.
     * @param code         Unique identifier for the partner.
     * @param base64       Base64 representation.
     * @param sha256       SHA-256 hash.
     * @param rsaPublicKey RSA public key.
     * @param rsaPrivateKey RSA private key.
     * @param createdBy    ID of the user who created the partner.
     */
    public Partner(String name, String description, String code, String base64, String sha256, String rsaPublicKey, String rsaPrivateKey, Long createdBy) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.base64 = base64;
        this.sha256 = sha256;
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
        this.createdBy = createdBy;
        this.createdDate = LocalDateTime.now();
    }

    // Getters and setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getRsaPublicKey() {
        return rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public String getRsaPrivateKey() {
        return rsaPrivateKey;
    }

    public void setRsaPrivateKey(String rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
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