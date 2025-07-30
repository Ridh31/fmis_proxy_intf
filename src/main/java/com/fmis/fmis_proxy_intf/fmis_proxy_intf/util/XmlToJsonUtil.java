package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.time.LocalDate;

public class XmlToJsonUtil {

    /**
     * Converts XML string to formatted JSON string.
     *
     * @param xmlData XML input
     * @return Pretty-printed JSON string
     * @throws Exception on error
     */
    public static String convertXmlToJson(String xmlData) throws Exception {
        JSONObject jsonObject = XML.toJSONObject(xmlData);
        return jsonObject.toString(2);
    }

    /**
     * Converts XML string to JSONObject directly for internal processing.
     *
     * @param xmlData XML input
     * @return JSONObject
     * @throws Exception on error
     */
    public static JSONObject convertXmlToJsonObject(String xmlData) throws Exception {
        return XML.toJSONObject(xmlData);
    }

    /**
     * Converts Sarmis Batch PO XML to a normalized JSONObject.
     * Ensures "purchase_orders" and "items" are always arrays,
     * formats dates, converts certain fields to strings,
     * and prefixes "budget_ref" with 'B' if missing.
     *
     * @param xmlData XML input string
     * @return Normalized JSONObject
     * @throws Exception on parsing or processing errors
     */
    public static JSONObject convertSarmisBatchPOXmlToJson(String xmlData) throws Exception {
        JSONObject rootJson = XML.toJSONObject(xmlData);
        JSONObject root = rootJson.optJSONObject("data");
        if (root == null) root = rootJson;

        Object poWrapperObj = root.opt("purchase_orders");

        JSONArray purchaseOrders;

        if (poWrapperObj instanceof JSONObject) {
            JSONObject poWrapper = (JSONObject) poWrapperObj;
            Object poObj = poWrapper.opt("purchase_order");

            if (poObj instanceof JSONArray) {
                purchaseOrders = (JSONArray) poObj;
            } else if (poObj != null) {
                purchaseOrders = new JSONArray();
                purchaseOrders.put(poObj);
            } else {
                purchaseOrders = new JSONArray();
            }

            root.put("purchase_orders", purchaseOrders);

        } else if (poWrapperObj instanceof JSONArray) {
            purchaseOrders = (JSONArray) poWrapperObj;
        } else {
            purchaseOrders = new JSONArray();
            root.put("purchase_orders", purchaseOrders);
        }

        for (int i = 0; i < purchaseOrders.length(); i++) {
            JSONObject po = purchaseOrders.getJSONObject(i);

            // Format receipt_date
            if (po.has("receipt_date")) {
                String formattedDate = normalizeSarmisPOReceiptDate(po.getString("receipt_date"));
                po.put("receipt_date", formattedDate);
            }

            JSONObject itemsWrapper = po.optJSONObject("items");
            if (itemsWrapper != null && itemsWrapper.has("item")) {
                Object itemObj = itemsWrapper.get("item");

                JSONArray items;
                if (itemObj instanceof JSONArray) {
                    items = (JSONArray) itemObj;
                } else {
                    items = new JSONArray();
                    items.put(itemObj);
                }

                po.put("items", items);

                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    forceSarmisPOItemFieldsToString(item, "quantity_received", "khr_amount", "currency_amount", "program", "fund", "geography");

                    if (item.has("budget_ref")) {
                        String budgetRef = item.get("budget_ref").toString();
                        if (!budgetRef.startsWith("B")) {
                            item.put("budget_ref", "B" + budgetRef);
                        }
                    }
                }
            }
        }

        return root;
    }

    /**
     * Converts the specified fields in a SarmisBatchPO item to string,
     * ensuring consistent serialization for numeric or mixed-type fields.
     *
     * @param item The JSONObject representing an item in the PO
     * @param keys The field names to force as strings
     */
    private static void forceSarmisPOItemFieldsToString(JSONObject item, String... keys) {
        for (String key : keys) {
            if (item.has(key)) {
                item.put(key, item.get(key).toString());
            }
        }
    }

    /**
     * Validates that a SarmisBatchPO receipt_date is in 'yyyy-MM-dd' format.
     * Returns the original date if valid, or unchanged if invalid.
     *
     * @param input The raw date string
     * @return A valid yyyy-MM-dd date string, or original input
     */
    private static String normalizeSarmisPOReceiptDate(String input) {
        try {
            LocalDate parsed = LocalDate.parse(input);
            return parsed.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
