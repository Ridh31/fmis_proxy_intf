package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom deserializer for converting date strings into LocalDateTime objects.
 * Handles both date-only strings (e.g., "YYYY-MM-DD") and full datetime strings (e.g., "YYYY-MM-DDT00:00:00").
 * Does not perform any time zone adjustments.
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    /**
     * Deserialize a JSON string into a LocalDateTime object.
     * Does not adjust the time zone, directly preserves the local time.
     *
     * @param p The JSON parser.
     * @param ctxt The deserialization context.
     * @return A LocalDateTime object parsed from the JSON string.
     * @throws IOException If there is an issue reading the JSON value.
     */
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        // Get the string representation of the date
        String date = p.getText();

        // If the string is not null or empty, process it
        if (date != null && !date.isEmpty()) {

            // If the string is a date-only format (e.g., "YYYY-MM-DD"), append the time part
            if (date.length() == 10) {
                date += "T00:00:00";  // Append time "00:00:00" to make it a valid LocalDateTime format
            }

            // Define the date-time formatter that matches the expected format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            // Parse the date string into a LocalDateTime object and return it
            return LocalDateTime.parse(date, formatter);
        }

        // If the date string is empty or null, return null
        return null;
    }
}
