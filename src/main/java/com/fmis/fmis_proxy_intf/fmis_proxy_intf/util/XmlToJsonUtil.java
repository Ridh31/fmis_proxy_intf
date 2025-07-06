package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import org.json.JSONObject;
import org.json.XML;

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
}
