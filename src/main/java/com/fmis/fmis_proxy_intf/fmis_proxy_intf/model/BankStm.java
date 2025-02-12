package com.fmis.fmis_proxy_intf.fmis_proxy_intf.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a bank statement record.
 */
@Entity
@Data
@Getter
@Setter
@Table(name = "bankstm_stg")
public class BankStm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String code;

    @Lob
    @Column(name = "bsp_stmt_dt")
    private LocalDateTime bspStmtDt;

    @Lob
    @Column(name = "bank_account_n")
    private String bankAccountNumber;

    @Lob
    @Column(name = "currency_cd")
    private String currencyCd;

    @Lob
    @Column(name = "value_dt")
    private LocalDateTime valueDate;

    @Lob
    @Column(name = "bank_stmt_type")
    private BigDecimal bankStmtType;

    @Lob
    @Column(name = "bsp_tran_amt")
    private BigDecimal bspTranAmt;

    @Lob
    @Column(name = "open_balance")
    private BigDecimal openBalance;

    @Lob
    @Column(name = "end_balance")
    private BigDecimal endBalance;

    @Lob
    @Column(name = "immediate_bal")
    private BigDecimal immediateBal;

    @Lob
    @Column(name = "recon_ref_id")
    private String reconRefId;

    @Lob
    @Column(name = "check_number")
    private String checkNumber;

    @Lob
    @Column(name = "letter_number")
    private String letterNumber;

    @Lob
    @Column(name = "descrlong")
    private String descrLong;

    private Integer createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    private Boolean status = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getBspStmtDt() {
        return bspStmtDt;
    }

    public void setBspStmtDt(LocalDateTime bspStmtDt) {
        this.bspStmtDt = bspStmtDt;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getCurrencyCd() {
        return currencyCd;
    }

    public void setCurrencyCd(String currencyCd) {
        this.currencyCd = currencyCd;
    }

    public LocalDateTime getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDateTime valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getBankStmtType() {
        return bankStmtType;
    }

    public void setBankStmtType(BigDecimal bankStmtType) {
        this.bankStmtType = bankStmtType;
    }

    public BigDecimal getBspTranAmt() {
        return bspTranAmt;
    }

    public void setBspTranAmt(BigDecimal bspTranAmt) {
        this.bspTranAmt = bspTranAmt;
    }

    public BigDecimal getOpenBalance() {
        return openBalance;
    }

    public void setOpenBalance(BigDecimal openBalance) {
        this.openBalance = openBalance;
    }

    public BigDecimal getEndBalance() {
        return endBalance;
    }

    public void setEndBalance(BigDecimal endBalance) {
        this.endBalance = endBalance;
    }

    public BigDecimal getImmediateBal() {
        return immediateBal;
    }

    public void setImmediateBal(BigDecimal immediateBal) {
        this.immediateBal = immediateBal;
    }

    public String getReconRefId() {
        return reconRefId;
    }

    public void setReconRefId(String reconRefId) {
        this.reconRefId = reconRefId;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getLetterNumber() {
        return letterNumber;
    }

    public void setLetterNumber(String letterNumber) {
        this.letterNumber = letterNumber;
    }

    public String getDescrLong() {
        return descrLong;
    }

    public void setDescrLong(String descrLong) {
        this.descrLong = descrLong;
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
