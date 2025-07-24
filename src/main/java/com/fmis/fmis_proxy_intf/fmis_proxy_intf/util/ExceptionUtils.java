package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionUtils {

    // Regex to match the full URL (used in extractTargetHostFromMessage)
    private static final Pattern FULL_URL_PATTERN = Pattern.compile("(https?://[^\\s\"']+)");

    // Regex to match only the base URL (protocol + domain)
    private static final Pattern BASE_URL_PATTERN = Pattern.compile("(https?://[a-zA-Z0-9.-]+)");

    /**
     * Extracts the first HTTP or HTTPS URL found in the given exception message.
     *
     * @param message The exception message to parse.
     * @return The extracted URL, or "unknown" if none found.
     */
    public static String extractTargetHost(String message) {
        if (message == null || message.isBlank()) {
            return "unknown";
        }

        Matcher matcher = FULL_URL_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "unknown";
    }

    /**
     * Extracts only the base URL (scheme + domain) from the input string.
     *
     * @param input The input string to search.
     * @return The base URL, or empty string if not found.
     */
    public static String extractBaseUrl(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        Matcher matcher = BASE_URL_PATTERN.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    /**
     * Convenience method to extract a base URL from the given input
     * and format it as " (url)" if present, or return an empty string.
     *
     * @param content the raw string content (e.g., response body)
     * @return a formatted host string
     */
    public static String formatHostFromContent(String content) {
        String safeContent = Optional.ofNullable(content).orElse("");
        String baseUrl = extractBaseUrl(safeContent);
        return !baseUrl.isEmpty() ? " (" + baseUrl + ")" : "";
    }

    /**
     * Attempts to extract a valid JSON object embedded within an exception message.
     *
     * @param message       The raw exception message.
     * @param objectMapper  The Jackson ObjectMapper instance.
     * @return A JsonNode representing the parsed JSON object, or a node with the raw error if parsing fails.
     */
    public static JsonNode extractJsonFromErrorMessage(String message, ObjectMapper objectMapper) {
        try {
            if (message == null || message.isBlank()) {
                return objectMapper.createObjectNode().put("error", "Empty exception message");
            }

            int jsonStart = message.indexOf('{');
            int jsonEnd = message.lastIndexOf('}') + 1;

            if (jsonStart != -1 && jsonEnd > jsonStart) {
                String jsonPart = message.substring(jsonStart, jsonEnd);
                return objectMapper.readTree(jsonPart);
            }

        } catch (Exception e) {
            // Fall through to return raw error below
        }

        return objectMapper.createObjectNode().put("error", message);
    }
}