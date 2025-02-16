package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.LocalDateTimeDeserializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BankStatementDTO {

    private String partnerCode;
    private Long partnerId;
    private Long createdBy;

    @JsonProperty("Data")
    private BankData data;

    // Getters and Setters
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public BankData getData() {
        return data;
    }

    public void setData(BankData data) {
        this.data = data;
    }

    // Nested BankData class
    public static class BankData {

        @JsonProperty("CMB_BANKSTM_STG")
        private List<BankStatement> cmbBankStmStg;

        // Getters and Setters
        public List<BankStatement> getCmbBankStmStg() {
            return cmbBankStmStg;
        }

        public void setCmbBankStmStg(List<BankStatement> cmbBankStmStg) {
            this.cmbBankStmStg = cmbBankStmStg;
        }
    }

    // Nested BankStatement class
    public static class BankStatement {

        @JsonProperty("CMB_BSP_STMT_DT")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime cmbBspStmtDt;

        @JsonProperty("CMB_BANK_ACCOUNT_N")
        private String cmbBankAccountN;

        @JsonProperty("CMB_CURRENCY_CD")
        private String cmbCurrencyCd;

        @JsonProperty("CMB_VALUE_DT")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime cmbValueDt;

        @JsonProperty("CMB_BANK_STMT_TYPE")
        private BigDecimal cmbBankStmtType;

        @JsonProperty("CMB_BSP_TRAN_AMT")
        private BigDecimal cmbBspTranAmt;

        @JsonProperty("CMB_OPEN_BALANCE")
        private BigDecimal cmbOpenBalance;

        @JsonProperty("CMB_END_BALANCE")
        private BigDecimal cmbEndBalance;

        @JsonProperty("CMB_IMMEDIATE_BAL")
        private BigDecimal cmbImmediateBalance;

        @JsonProperty("CMB_RECON_REF_ID")
        private String cmbReconRefId;

        @JsonProperty("CMB_CHECK_NUMBER")
        private String cmbCheckNumber;

        @JsonProperty("CMB_DESCRLONG")
        private String cmbDescrlong;

        @JsonProperty("CMB_LETTER_NUMBER")
        private String cmbLetterNumber;

        // Getters and Setters
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

        public BigDecimal getCmbImmediateBalance() {
            return cmbImmediateBalance;
        }

        public void setCmbImmediateBalance(BigDecimal cmbImmediateBalance) {
            this.cmbImmediateBalance = cmbImmediateBalance;
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

        public String getCmbDescrlong() {
            return cmbDescrlong;
        }

        public void setCmbDescrlong(String cmbDescrlong) {
            this.cmbDescrlong = cmbDescrlong;
        }

        public String getCmbLetterNumber() {
            return cmbLetterNumber;
        }

        public void setCmbLetterNumber(String cmbLetterNumber) {
            this.cmbLetterNumber = cmbLetterNumber;
        }
    }
}
