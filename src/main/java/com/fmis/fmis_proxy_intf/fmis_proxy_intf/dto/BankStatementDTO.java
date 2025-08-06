package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Data Transfer Object for BankStatement.
 */
public class BankStatementDTO {

    @Schema(hidden = true)
    private Long id;

    @NotEmpty(message = "Partner code cannot be empty. Please provide a valid partner code.")
    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String partnerCode;

    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long partnerId;

    @Schema(hidden = true)
    @JsonProperty(value = "systemCode", access = JsonProperty.Access.READ_ONLY)
    private String partnerSystemCode;

    @NotEmpty(message = "Data cannot be empty. Please provide a valid data.")
    @JsonProperty("Data")
    @Schema(description = "The root wrapper for bank statement data.")
    private Map<String, Object> data;

    @Schema(hidden = true)
    private String method;

    @Schema(hidden = true)
    private String endpoint;

    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String filename;

    private String bankAccountNumber;
    private String statementId;
    private LocalDateTime statementDate;

    @JsonIgnore
    private String payload;

    @JsonIgnore
    private JsonNode payloadJson;

    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String xml;

    @Schema(hidden = true)
    private String message;

    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long createdBy;

    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private LocalDateTime createdDate;

    @Schema(hidden = true)
    private Boolean status;

    @Schema(hidden = true)
    @JsonIgnore
    private Boolean isDeleted;

    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String importedBy;

    // Constructors
    public BankStatementDTO() {
    }

    public BankStatementDTO(
            Long id,
            String partnerCode,
            String method,
            Long partnerId,
            String endpoint,
            String payload,
            JsonNode payloadJson,
            String xml,
            Long createdBy,
            LocalDateTime createdDate,
            Boolean status,
            Boolean isDeleted
    ) {
        this.id = id;
        this.partnerCode = partnerCode;
        this.partnerId = partnerId;
        this.method = method;
        this.endpoint = endpoint;
        this.payload = payload;
        this.payloadJson = payloadJson;
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

    public String getPartnerSystemCode() {
        return partnerSystemCode;
    }

    public void setPartnerSystemCode(String partnerSystemCode) {
        this.partnerSystemCode = partnerSystemCode;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public LocalDateTime getStatementDate() {
        return statementDate;
    }

    public void setStatementDate(LocalDateTime statementDate) {
        this.statementDate = statementDate;
    }

    @JsonProperty("statementDate")
    public String getFormattedStatementDate() {
        return statementDate != null
                ? statementDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                : null;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public JsonNode getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(JsonNode payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    @JsonProperty("status")
    public String getStatusText() {
        return Boolean.TRUE.equals(status) ? "Processed" : "Failed";
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getImportedBy() {
        return importedBy;
    }

    public void setImportedBy(String importedBy) {
        this.importedBy = importedBy;
    }

    @JsonProperty("createdDateFormatted")
    public String getCreatedDateFormatted() {
        return createdDate != null
                ? createdDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                : null;
    }
}