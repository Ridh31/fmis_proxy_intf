package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

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
}
