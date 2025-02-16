package com.fmis.fmis_proxy_intf.fmis_proxy_intf.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "cmb_bankstm_stg")
public class BankStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "partner_intf_id", referencedColumnName = "id")
    @JsonIgnore
    private Partner partner;  // Reference to Partner

    @Lob
    @Column(name = "cmb_bsp_stmt_dt")
    @JsonProperty("CMB_BSP_STMT_DT")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime cmbBspStmtDt;

    @Lob
    @Column(name = "cmb_bank_account_n")
    @JsonProperty("CMB_BANK_ACCOUNT_N")
    private String cmbBankAccountN;

    @Lob
    @Column(name = "cmb_currency_cd")
    @JsonProperty("CMB_CURRENCY_CD")
    private String cmbCurrencyCd;

    @Lob
    @Column(name = "cmb_value_dt")
    @JsonProperty("CMB_VALUE_DT")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime cmbValueDt;

    @Lob
    @Column(name = "cmb_bank_stmt_type")
    @JsonProperty("CMB_BANK_STMT_TYPE")
    private BigDecimal cmbBankStmtType;

    @Lob
    @Column(name = "cmb_bsp_tran_amt")
    @JsonProperty("CMB_BSP_TRAN_AMT")
    private BigDecimal cmbBspTranAmt;

    @Lob
    @Column(name = "cmb_open_balance")
    @JsonProperty("CMB_OPEN_BALANCE")
    private BigDecimal cmbOpenBalance;

    @Lob
    @Column(name = "cmb_end_balance")
    @JsonProperty("CMB_END_BALANCE")
    private BigDecimal cmbEndBalance;

    @Lob
    @Column(name = "cmb_immediate_bal")
    @JsonProperty("CMB_IMMEDIATE_BAL")
    private BigDecimal cmbImmediateBal;

    @Lob
    @Column(name = "cmb_recon_ref_id")
    @JsonProperty("CMB_RECON_REF_ID")
    private String cmbReconRefId;

    @Lob
    @Column(name = "cmb_check_number")
    @JsonProperty("CMB_CHECK_NUMBER")
    private String cmbCheckNumber;

    @Lob
    @Column(name = "cmb_letter_number")
    @JsonProperty("CMB_LETTER_NUMBER")
    private String cmbLetterNumber;

    @Lob
    @Column(name = "cmb_descrlong")
    @JsonProperty("CMB_DESCRLONG")
    private String cmbDescrLong;

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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public LocalDateTime getCmbBspStmtDt() {
        return cmbBspStmtDt;
    }

    public void setCmbBspStmtDt(LocalDateTime cmbBspStmtDt) {
        this.cmbBspStmtDt = cmbBspStmtDt;
    }

    public String getCmbBankAccountN() {
        return cmbBankAccountN;
    }

    public void setCmbBankAccountN(String cmbBankAccountN) {
        this.cmbBankAccountN = cmbBankAccountN;
    }

    public String getCmbCurrencyCd() {
        return cmbCurrencyCd;
    }

    public void setCmbCurrencyCd(String cmbCurrencyCd) {
        this.cmbCurrencyCd = cmbCurrencyCd;
    }

    public LocalDateTime getCmbValueDt() {
        return cmbValueDt;
    }

    public void setCmbValueDt(LocalDateTime cmbValueDt) {
        this.cmbValueDt = cmbValueDt;
    }

    public BigDecimal getCmbBankStmtType() {
        return cmbBankStmtType;
    }

    public void setCmbBankStmtType(BigDecimal cmbBankStmtType) {
        this.cmbBankStmtType = cmbBankStmtType;
    }

    public BigDecimal getCmbBspTranAmt() {
        return cmbBspTranAmt;
    }

    public void setCmbBspTranAmt(BigDecimal cmbBspTranAmt) {
        this.cmbBspTranAmt = cmbBspTranAmt;
    }

    public BigDecimal getCmbOpenBalance() {
        return cmbOpenBalance;
    }

    public void setCmbOpenBalance(BigDecimal cmbOpenBalance) {
        this.cmbOpenBalance = cmbOpenBalance;
    }

    public BigDecimal getCmbEndBalance() {
        return cmbEndBalance;
    }

    public void setCmbEndBalance(BigDecimal cmbEndBalance) {
        this.cmbEndBalance = cmbEndBalance;
    }

    public BigDecimal getCmbImmediateBal() {
        return cmbImmediateBal;
    }

    public void setCmbImmediateBal(BigDecimal cmbImmediateBal) {
        this.cmbImmediateBal = cmbImmediateBal;
    }

    public String getCmbReconRefId() {
        return cmbReconRefId;
    }

    public void setCmbReconRefId(String cmbReconRefId) {
        this.cmbReconRefId = cmbReconRefId;
    }

    public String getCmbCheckNumber() {
        return cmbCheckNumber;
    }

    public void setCmbCheckNumber(String cmbCheckNumber) {
        this.cmbCheckNumber = cmbCheckNumber;
    }

    public String getCmbLetterNumber() {
        return cmbLetterNumber;
    }

    public void setCmbLetterNumber(String cmbLetterNumber) {
        this.cmbLetterNumber = cmbLetterNumber;
    }

    public String getCmbDescrLong() {
        return cmbDescrLong;
    }

    public void setCmbDescrLong(String cmbDescrLong) {
        this.cmbDescrLong = cmbDescrLong;
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
