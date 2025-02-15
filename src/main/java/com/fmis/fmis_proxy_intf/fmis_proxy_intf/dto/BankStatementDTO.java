package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankStatementDTO {
    private String partnerCode;
    private Data data;

    // Getters and Setters
    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Inner class representing bank statement data.
     */
    public static class Data {
        private String bank_type;

        public String getBankType() {
            return bank_type;
        }

        public void setBankType(String bank_type) {
            this.bank_type = bank_type;
        }
    }
}
