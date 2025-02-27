package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle the processing of validation errors.
 */
public class ValidationErrorUtils {

    /**
     * Processes validation errors and returns a map of field-specific error messages.
     *
     * @param bindingResult the result of the validation containing errors.
     * @return a map with field names as keys and their corresponding error messages as values.
     */
    public static Map<String, String> extractValidationErrors(BindingResult bindingResult) {

        // Map to store field names and their error messages
        Map<String, String> fieldErrorMessages = new HashMap<>();

        // Check if there are any validation errors
        if (bindingResult.hasErrors()) {

            // Loop through each error in the binding result
            for (ObjectError error : bindingResult.getAllErrors()) {

                // Cast ObjectError to FieldError to access the field name
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;

                    // Map the field name to its respective error message
                    fieldErrorMessages.put(fieldError.getField(), error.getDefaultMessage());
                }
            }
        }
        return fieldErrorMessages;
    }
}