package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class to validate bank statement data.
 * It checks if the required fields are present in the provided data.
 */
public class BodyValidationUtil {

    /**
     * Validates the bank statement data.
     * Ensures that the "CMB_BANKSTM_STG" field is present and is an array,
     * and that each entry in the array contains the necessary keys.
     *
     * @param data The bank statement data.
     * @throws IllegalArgumentException If any required field is missing.
     */
    public static void validateBankStatement(Map<String, Object> data) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.valueToTree(data);

        // Check if the "CMB_BANKSTM_STG" key is present
        if (!rootNode.has("CMB_BANKSTM_STG")) {
            throw new IllegalArgumentException("The bank statement field (CMB_BANKSTM_STG) is missing.");
        }

        JsonNode bankStmtNode = rootNode.get("CMB_BANKSTM_STG");

        // Check if "CMB_BANKSTM_STG" is an array
        if (!bankStmtNode.isArray()) {
            throw new IllegalArgumentException("The bank statement field (CMB_BANKSTM_STG) is expected to be an array.");
        }

        // List of required keys for each bank statement
        String[] requiredKeys = {
            "CMB_BSP_STMT_DT",
            "CMB_BANK_ACCOUNT_N",
            "CMB_CURRENCY_CD",
            "CMB_VALUE_DT",
            "CMB_BANK_STMT_TYPE",
            "CMB_BSP_TRAN_AMT",
            "CMB_OPEN_BALANCE",
            "CMB_END_BALANCE",
            "CMB_IMMEDIATE_BAL",
            "CMB_RECON_REF_ID",
            "CMB_CHECK_NUMBER",
            "CMB_DESCRLONG",
            "CMB_LETTER_NUMBER"
        };

        String[] numericFields = {
            "CMB_BSP_TRAN_AMT",
            "CMB_OPEN_BALANCE",
            "CMB_END_BALANCE",
            "CMB_IMMEDIATE_BAL"
        };

        // Check each statement in the array
        for (int i = 0; i < bankStmtNode.size(); i++) {
            JsonNode stmt = bankStmtNode.get(i);

            // Ensure all required keys are present in the statement
            for (String key : requiredKeys) {
                if (!stmt.has(key)) {
                    // Adding the entry number (i+1 to start from 1 instead of 0)
                    throw new IllegalArgumentException("The bank statement field (" + key + ") is missing. (Entry: " + (i + 1) + ")");
                }
            }

            // Validate date format
            String[] dateFields = {"CMB_BSP_STMT_DT", "CMB_VALUE_DT"};
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (String dateKey : dateFields) {
                JsonNode dateNode = stmt.get(dateKey);
                if (!dateNode.isTextual()) {
                    throw new IllegalArgumentException("The bank statement field (" + dateKey + ") must be a string in YYYY-MM-DD format. (Entry: " + (i + 1) + ")");
                }
                try {
                    LocalDate.parse(dateNode.asText(), dateFormatter);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("The bank statement field (" + dateKey + ") has an invalid format. Expected YYYY-MM-DD. (Entry: " + (i + 1) + ")");
                }
            }

            // Check numeric fields
            for (String numericKey : numericFields) {
                JsonNode valueNode = stmt.get(numericKey);

                double value;
                if (valueNode.isNumber()) {
                    value = valueNode.asDouble();
                } else if (valueNode.isTextual()) {
                    try {
                        value = Double.parseDouble(valueNode.asText());
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The bank statement field (" + numericKey + ") must be numeric. (Entry: " + (i + 1) + ")");
                    }
                } else {
                    throw new IllegalArgumentException("The bank statement field (" + numericKey + ") must be numeric. (Entry: " + (i + 1) + ")");
                }
            }
        }
    }

    /**
     * Validates the FMIS batch purchase order callback data.
     * Ensures that the "interface_code" field is present and non-blank.
     *
     * @param data The FMIS callback data.
     * @throws IllegalArgumentException If the "interface_code" field is missing or blank.
     */
    public static void validateBatchPOCallback(Map<String, Object> data) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.valueToTree(data);

        // Check if the "interface_code" field is present and non-blank
        if (!rootNode.has("interface_code") || rootNode.get("interface_code").asText().isBlank()) {
            throw new IllegalArgumentException(ResponseMessageUtil.invalid("interface_code"));
        }
    }
}