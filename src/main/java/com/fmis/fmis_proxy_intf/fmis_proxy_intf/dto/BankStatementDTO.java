package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for BankStatement.
 */
public class BankStatementDTO {

    private Long id;

    @NotEmpty(message = "Partner code cannot be empty. Please provide a valid partner code.")
    private String partnerCode;

    private Long partnerId;

    @NotEmpty(message = "Data cannot be empty. Please provide a valid data.")
    private Map<String, Object> data;
    private String method;
    private String endpoint;
    private String payload;
    private String xml;
    private Long createdBy;
    private LocalDateTime createdDate;
    private Boolean status;
    private Boolean isDeleted;

    // Constructors
    public BankStatementDTO() {
    }

    public BankStatementDTO(Long id, String partnerCode, String method, Long partnerId, String endpoint, String payload,
                            String xml, Long createdBy, LocalDateTime createdDate, Boolean status, Boolean isDeleted) {
        this.id = id;
        this.partnerCode = partnerCode;
        this.partnerId = partnerId;
        this.method = method;
        this.endpoint = endpoint;
        this.payload = payload;
        this.xml = xml;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.status = status;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
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
