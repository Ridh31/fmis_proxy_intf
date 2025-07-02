package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
        String telegramStatementDate = statementDate.format(outputFormatter);
        String telegramImportedDate = LocalDate.now().format(outputFormatter);
        String telegramImportedStatus = (responseCode == 201) ? "Processed" : "Failed";
        String announcementEmoji = (responseCode == 201) ? "🔔" : "⚠️";
        String statusEmoji = (responseCode == 201) ? "✅" : "❌";

        String requestMessage =
                "📨 <b>New Request</b>\n\n" +
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
}