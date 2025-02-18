package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.LocalDateTimeDeserializer;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankStatementDTO {
    private String partnerCode;
    private Long partnerId;
    private Long createdBy;

    @XmlElement(name = "Data")
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

    @XmlRootElement(name = "Data")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BankData {

        @XmlElement(name = "CMB_BANKSTM_STG")
        @JsonProperty("CMB_BANKSTM_STG")
        private List<BankStatement> cmbBankStmStg;

        public List<BankStatement> getCmbBankStmStg() {
            return cmbBankStmStg;
        }

        public void setCmbBankStmStg(List<BankStatement> cmbBankStmStg) {
            this.cmbBankStmStg = cmbBankStmStg;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BankStatement {

        @XmlElement(name = "CMB_BSP_STMT_DT")
        @JsonProperty("CMB_BSP_STMT_DT")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime cmbBspStmtDt;

        @XmlElement(name = "CMB_BANK_ACCOUNT_N")
        @JsonProperty("CMB_BANK_ACCOUNT_N")
        private String cmbBankAccountN;

        @XmlElement(name = "CMB_CURRENCY_CD")
        @JsonProperty("CMB_CURRENCY_CD")
        private String cmbCurrencyCd;

        @XmlElement(name = "CMB_VALUE_DT")
        @JsonProperty("CMB_VALUE_DT")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime cmbValueDt;

        @XmlElement(name = "CMB_BANK_STMT_TYPE")
        @JsonProperty("CMB_BANK_STMT_TYPE")
        private BigDecimal cmbBankStmtType;

        @XmlElement(name = "CMB_BSP_TRAN_AMT")
        @JsonProperty("CMB_BSP_TRAN_AMT")
        private BigDecimal cmbBspTranAmt;

        @XmlElement(name = "CMB_OPEN_BALANCE")
        @JsonProperty("CMB_OPEN_BALANCE")
        private BigDecimal cmbOpenBalance;

        @XmlElement(name = "CMB_END_BALANCE")
        @JsonProperty("CMB_END_BALANCE")
        private BigDecimal cmbEndBalance;

        @XmlElement(name = "CMB_IMMEDIATE_BAL")
        @JsonProperty("CMB_IMMEDIATE_BAL")
        private BigDecimal cmbImmediateBal;

        @XmlElement(name = "CMB_RECON_REF_ID")
        @JsonProperty("CMB_RECON_REF_ID")
        private String cmbReconRefId;

        @XmlElement(name = "CMB_CHECK_NUMBER")
        @JsonProperty("CMB_CHECK_NUMBER")
        private String cmbCheckNumber;

        @XmlElement(name = "CMB_DESCRLONG")
        @JsonProperty("CMB_DESCRLONG")
        private String cmbDescrlong;

        @XmlElement(name = "CMB_LETTER_NUMBER")
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
