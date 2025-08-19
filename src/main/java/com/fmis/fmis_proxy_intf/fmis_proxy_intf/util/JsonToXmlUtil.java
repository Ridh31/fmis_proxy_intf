package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

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
    public static String convertBankStatementJsonToXml(String jsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonData);

        StringBuilder xmlBuilder = new StringBuilder();

        // Adding the XML declaration at the top
        xmlBuilder.append("<?xml version=\"1.0\"?>\n");
        xmlBuilder.append("<Data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"src/conf/xml-resources/jaxb/AccountStatement/CMB_BANKSTM_STG.XSD.xsd\">\n");

        // Handle dynamic keys at the root level
        if (rootNode.isObject()) {
            rootNode.fields().forEachRemaining(field -> {
                String key = field.getKey();
                JsonNode value = field.getValue();

                if (value.isArray()) {
                    // Handle arrays of objects
                    value.forEach(item -> xmlBuilder.append(convertBankStatementNodeToXml(item, key)));
                } else {
                    // Handle single objects or primitive values
                    xmlBuilder.append(convertBankStatementNodeToXml(value, key));
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
    private static String convertBankStatementNodeToXml(JsonNode jsonNode, String nodeName) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("\t<").append(nodeName).append(">\n");

        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(field -> {
                String key = field.getKey();
                JsonNode value = field.getValue();

                if (value.isObject()) {
                    xmlBuilder.append(convertBankStatementNodeToXml(value, key));
                } else if (value.isArray()) {
                    value.forEach(item -> xmlBuilder.append(convertBankStatementNodeToXml(item, key)));
                } else {
                    xmlBuilder.append("\t\t<").append(key).append(">")
                            .append(escapeBankStatementXml(value.asText()))
                            .append("</").append(key).append(">\n");
                }
            });
        } else if (jsonNode.isValueNode()) {
            xmlBuilder.append("\t\t").append(escapeBankStatementXml(jsonNode.asText())).append("\n");
        }

        xmlBuilder.append("\t</").append(nodeName).append(">\n");
        return xmlBuilder.toString();
    }

    /**
     * Escapes special XML characters in the input string.
     *
     * @param value The input string to escape.
     * @return The escaped XML-safe string.
     */
    private static String escapeBankStatementXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Recursively replaces all null values in a Map or List with empty strings ("").
     * Ensures null fields appear in XML as empty elements instead of being skipped.
     *
     * @param obj The object to process. Can be a Map, List, or nested combination thereof.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void replaceNullsWithEmptyString(Object obj) {
        if (obj instanceof Map map) {
            for (Object key : map.keySet()) {
                Object value = map.get(key);
                if (value == null) {
                    map.put(key, "");
                } else {
                    replaceNullsWithEmptyString(value);
                }
            }
        } else if (obj instanceof List list) {
            for (int i = 0; i < list.size(); i++) {
                Object value = list.get(i);
                if (value == null) {
                    list.set(i, "");
                } else {
                    replaceNullsWithEmptyString(value);
                }
            }
        }
    }
}
