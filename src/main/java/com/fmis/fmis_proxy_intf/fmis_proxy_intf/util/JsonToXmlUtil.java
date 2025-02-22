package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class to convert JSON to XML with dynamic key handling.
 */
public class JsonToXmlUtil {

    /**
     * Converts JSON data to XML format.
     *
     * @param jsonData The input JSON string.
     * @return The converted XML string.
     * @throws Exception if parsing or conversion fails.
     */
    public static String convertJsonToXml(String jsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonData);

        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<Data>");

        // Handle dynamic keys at the root level
        if (rootNode.isObject()) {
            rootNode.fields().forEachRemaining(field -> {
                String key = field.getKey();
                JsonNode value = field.getValue();

                if (value.isArray()) {
                    // Handle arrays of objects
                    value.forEach(item -> xmlBuilder.append(convertNodeToXml(item, key)));
                } else {
                    // Handle single objects or primitive values
                    xmlBuilder.append(convertNodeToXml(value, key));
                }
            });
        }

        xmlBuilder.append("</Data>");
        return xmlBuilder.toString();
    }

    /**
     * Recursively converts a JsonNode to XML format.
     *
     * @param jsonNode The current JSON node to process.
     * @param nodeName The XML element name for this node.
     * @return The XML string representation of the node.
     */
    private static String convertNodeToXml(JsonNode jsonNode, String nodeName) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<").append(nodeName).append(">");

        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(field -> {
                String key = field.getKey();
                JsonNode value = field.getValue();

                if (value.isObject()) {
                    xmlBuilder.append(convertNodeToXml(value, key));
                } else if (value.isArray()) {
                    value.forEach(item -> xmlBuilder.append(convertNodeToXml(item, key)));
                } else {
                    xmlBuilder.append("<").append(key).append(">")
                            .append(escapeXml(value.asText()))
                            .append("</").append(key).append(">");
                }
            });
        } else if (jsonNode.isValueNode()) {
            xmlBuilder.append(escapeXml(jsonNode.asText()));
        }

        xmlBuilder.append("</").append(nodeName).append(">");
        return xmlBuilder.toString();
    }

    /**
     * Escapes special XML characters in the input string.
     *
     * @param value The input string to escape.
     * @return The escaped XML-safe string.
     */
    private static String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
