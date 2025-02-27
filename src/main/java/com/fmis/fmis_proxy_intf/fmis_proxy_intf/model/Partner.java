package com.fmis.fmis_proxy_intf.fmis_proxy_intf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
@Table(
        name = "partner_intf",
        uniqueConstraints = @UniqueConstraint(columnNames = {"code", "public_key", "private_key"})
)
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Name cannot be empty. Please provide a valid name.")
    private String name;

    @Lob
    private String description;

    @Lob
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Code cannot be empty. Please provide a valid code.")
    @JsonIgnore
    private String code;

    @Lob
    @Column(name = "public_key", nullable = false, unique = true)
    @JsonIgnore
    private String publicKey;

    @Lob
    @Column(name = "private_key", nullable = false, unique = true)
    @JsonIgnore
    private String privateKey;

    @JsonIgnore
    private Long createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime createdDate = LocalDateTime.now();

    @JsonIgnore
    private Boolean status = true;

    @Column(name = "is_deleted")
    @JsonIgnore
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
     * @param publicKey    RSA public key.
     * @param privateKey   RSA private key.
     * @param createdBy    ID of the user who created the partner.
     */
    public Partner(String name, String description, String code, String base64, String sha256, String publicKey, String privateKey, Long createdBy) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
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