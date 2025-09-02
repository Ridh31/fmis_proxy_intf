package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utility class for escaping special characters in Telegram messages
 * to ensure proper rendering with Markdown or HTML parse modes.
 */
public final class TelegramUtil {

    private TelegramUtil() {
        // Prevent instantiation
    }

    /**
     * Escapes HTML special characters to avoid malformed tags in Telegram messages.
     *
     * @param input the raw input string
     * @return the escaped string safe for HTML parse mode
     */
    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * Escapes Markdown special characters to prevent parse errors in Telegram messages.
     *
     * @param input the raw input string
     * @return the escaped string safe for Markdown parse mode
     */
    public static String escapeMarkdown(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }

    /**
     * Builds a formatted Telegram notification message for bank statement imports.
     * Uses HTML parse mode and includes partner details, account info, dates, status, and message.
     *
     * @param partner           Optional partner information (name, identifier, system code)
     * @param bankAccountNumber Bank account number involved in the import
     * @param statementDate     Date of the bank statement
     * @param responseCode      Response code from FMIS or internal processing
     * @param responseMessage   Response or error message from FMIS
     * @return Formatted Telegram message string (HTML-safe)
     */
    public static String buildBankStatementNotification(
            Optional<Partner> partner,
            String bankAccountNumber,
            LocalDate statementDate,
            int responseCode,
            String responseMessage
    ) {
        String partnerName = partner.map(Partner::getName).orElse("Unknown Partner");
        String partnerIdentifier = partner.map(Partner::getIdentifier).orElse("Unknown Identifier");
        String partnerSystemCode = partner.map(Partner::getSystemCode).orElse("Unknown System Code");

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String telegramStatementDate = (statementDate != null) ? statementDate.format(outputFormatter) : "N/A";
        String telegramImportedDate = LocalDate.now().format(outputFormatter);
        String telegramImportedStatus = (responseCode == 201) ? "Processed" : "Failed";
        String announcementEmoji = (responseCode == 201) ? "🔔" : "⚠️";
        String statusEmoji = (responseCode == 201) ? "✅" : "❌";

        String requestMessage =
                "📨 <b>Bank Interface</b>\n\n" +
                "From: <b>" + escapeHtml(partnerName) + "</b>\n" +
                "Action: Import Bank Statement\n" +
                "Time: <code>" + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mma")) + "</code>";

        String telegramMessage =
                requestMessage + "\n\n" +
                announcementEmoji + " <b>Bank Statement Update</b>\n\n" +
                "• Reference: <code>" + escapeHtml(partnerSystemCode) + "</code>" +
                " (" + escapeHtml(partnerIdentifier) + ")\n" +
                "• Account Number: <code>" + escapeHtml(bankAccountNumber != null ? bankAccountNumber : "N/A") + "</code>\n" +
                "• Statement Date: <code>" + telegramStatementDate + "</code>\n" +
                "• Imported On: <code>" + telegramImportedDate + "</code>\n" +
                "• Status: <b>" + telegramImportedStatus + "</b> " + statusEmoji + "\n" +
                "• Message: " + escapeHtml(responseMessage != null ? responseMessage : "");

        return telegramMessage;
    }

    /**
     * Builds a formatted Telegram notification message for the Sarmis Batch Purchase Order Callback.
     * The message includes the interface code, purchase order IDs, PO-level validation errors,
     * and item-level validation errors (if any), formatted with HTML tags for Telegram.
     *
     * @param jsonBody the JsonNode representing the parsed JSON callback payload from FMIS/Sarmis
     * @return a formatted String message ready to be sent via Telegram, with HTML escaping applied
     */
    public static String buildBatchPOCallbackNotification(JsonNode jsonBody) {
        StringBuilder message = new StringBuilder();
        String interfaceCode = jsonBody.has("interface_code") ? jsonBody.get("interface_code").asText() : "N/A";

        // Updated titles
        message.append("📨 <b>SARMIS Interface</b>\n\n");
        message.append("<b>Batch Purchase Order Callback</b>\n");

        // Add current time
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mma"));
        message.append("<b>Time</b>: <code>").append(currentTime).append("</code>\n\n");

        // Interface code
        message.append("🔗 <b>Interface Code</b>: <code>").append(escapeHtml(interfaceCode)).append("</code>\n\n");

        // Handle top-level validation errors
        JsonNode rootErrors = jsonBody.get("validation_errors");
        if (rootErrors != null && rootErrors.isArray() && rootErrors.size() > 0) {
            message.append("⚠️ <b>Interface-Level Errors:</b>\n");
            for (JsonNode err : rootErrors) {
                String msg = err.has("message") ? err.get("message").asText() : "Unknown";
                String code = err.has("error") ? err.get("error").asText() : "-";
                message.append("• ").append(escapeHtml(msg))
                        .append(" (<code>").append(code).append("</code>)\n");
            }
            message.append("\n");
        }

        // Handle purchase orders
        JsonNode purchaseOrders = jsonBody.get("purchase_orders");
        if (purchaseOrders == null || !purchaseOrders.isArray() || purchaseOrders.size() == 0) {
            message.append("ℹ️ No purchase orders found.\n");
        } else {
            for (JsonNode po : purchaseOrders) {
                String poId = po.has("purchase_order_id") ? po.get("purchase_order_id").asText() : "N/A";
                message.append("🧾 <b>Purchase Order ID</b>: <code>").append(escapeHtml(poId)).append("</code>\n");

                // Validation errors
                JsonNode poErrors = po.get("validation_errors");
                if (poErrors != null && poErrors.isArray() && poErrors.size() > 0) {
                    message.append("❗ <b>Validation Errors:</b>\n");
                    for (JsonNode err : poErrors) {
                        String msg = err.has("message") ? err.get("message").asText() : "Unknown";
                        String code = err.has("error") ? err.get("error").asText() : "-";
                        message.append("• ").append(escapeHtml(msg))
                                .append(" (<code>").append(code).append("</code>)\n");
                    }
                }

                // Item-level errors
                JsonNode items = po.get("items");
                if (items != null && items.isArray() && items.size() > 0) {
                    List<JsonNode> itemList = new ArrayList<>();
                    items.forEach(itemList::add);
                    itemList.sort(Comparator.comparingInt(item -> item.has("index") ? item.get("index").asInt() : -1));

                    for (JsonNode item : itemList) {
                        int index = item.has("index") ? item.get("index").asInt() : -1;
                        int displayIndex = (index >= 0) ? index + 1 : -1;
                        message.append("📦 <b>Line Item: ").append(displayIndex).append("</b>\n");

                        JsonNode itemErrors = item.get("validation_errors");
                        if (itemErrors != null && itemErrors.isArray() && itemErrors.size() > 0) {
                            Map<String, Integer> errorCountMap = new HashMap<>();

                            for (JsonNode err : itemErrors) {
                                String key = (err.has("message") ? err.get("message").asText() : "Unknown")
                                        + " (" + (err.has("error") ? err.get("error").asText() : "-") + ")";
                                errorCountMap.put(key, errorCountMap.getOrDefault(key, 0) + 1);
                            }

                            for (Map.Entry<String, Integer> entry : errorCountMap.entrySet()) {
                                message.append("• ").append(escapeHtml(entry.getKey()));
                                if (entry.getValue() > 1) {
                                    message.append(" ×").append(entry.getValue());
                                }
                                message.append("\n");
                            }
                        }
                    }
                }
                message.append("\n");
            }
        }

        return message.toString().trim();
    }

    /**
     * Creates a Telegram message for FMIS Batch PO callback validation errors.
     *
     * @param errorMessage Description of the validation error.
     * @return Formatted and escaped message string for Telegram.
     */
    public static String buildBatchPOCallbackErrorNotification(String errorMessage) {
        String time = java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("hh:mma"));

        return String.format(
                "📨 <b>SARMIS Interface</b>\n\n" +
                "⚠️ Batch Purchase Order Callback\n" +
                "⏰ Time: <code>%s</code>\n" +
                "❗ Error: %s",
                time,
                escapeHtml(errorMessage)
        );
    }
}