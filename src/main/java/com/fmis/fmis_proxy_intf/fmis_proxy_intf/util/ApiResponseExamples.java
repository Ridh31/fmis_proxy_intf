package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;

public class ApiResponseExamples {

    /*
    |--------------------------------------------------------------------------
    | Partner Creation Responses
    |--------------------------------------------------------------------------
    */
    public static final String CREATE_PARTNER_DATA = """
        {
            "name": "FMIS",
            "description": "Financial Management Information System"
        }
    """;

    /**
     * Example response for a successful partner creation (201 Created).
     * This response indicates that the partner was successfully created in the system.
     */
    public static final String CREATE_PARTNER_SUCCESS = "{\n" +
        "    \"code\": " + ApiResponseConstants.CREATED_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.CREATED + "\",\n" +
        "    \"data\": " + ApiResponseExamples.CREATE_PARTNER_DATA + "\n" +
        "}";

    /**
     * Example response for a bad request (400 Bad Request).
     * This response occurs when the partner creation fails due to invalid data, e.g., duplicate partner code.
     */
    public static final String CREATE_PARTNER_BAD_REQUEST = "{\n" +
        "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.PARTNER_CODE_TAKEN + "\"\n" +
        "}";

    /**
     * Example response for an unauthorized request (401 Unauthorized).
     * This response indicates that the request was made by an unauthenticated user.
     */
    public static final String CREATE_PARTNER_UNAUTHORIZED = "{\n" +
        "    \"code\": " + ApiResponseConstants.UNAUTHORIZED_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.UNAUTHORIZED_LOGIN_REQUIRED + "\"\n" +
        "}";

    /**
     * Example response for a not found error (404 Not Found).
     * This response occurs when the user referenced in the request cannot be found in the system.
     */
    public static final String CREATE_PARTNER_NOT_FOUND = "{\n" +
        "    \"code\": " + ApiResponseConstants.NOT_FOUND_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.USER_NOT_FOUND + "\"\n" +
        "}";

    /**
     * Example response for a server error (500 Internal Server Error).
     * This response occurs when an unexpected error happens on the server while processing the request.
     */
    public static final String CREATE_PARTNER_SERVER_ERROR = "{\n" +
        "    \"code\": " + ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.INTERNAL_SERVER_ERROR + "\"\n" +
        "}";

    /*
    |--------------------------------------------------------------------------
    | Bank Statement Import Responses
    |--------------------------------------------------------------------------
    */
    /**
     * Example response for a successful bank statement import (201 Created).
     * This response indicates that the bank statement was successfully imported into the system.
     */
    public static final String IMPORT_BANK_STATEMENT_SUCCESS = "{\n" +
        "    \"code\": " + ApiResponseConstants.CREATED_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.CREATED + "\"\n" +
        "}";

    /**
     * Example response for a missing X-Partner-Token (400 Bad Request).
     * This response occurs when the 'X-Partner-Token' header is missing or empty in the request.
     */
    public static final String IMPORT_BANK_STATEMENT_MISSING_PARTNER_TOKEN = "{\n" +
        "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_MISSING_PARTNER_TOKEN + "\"\n" +
        "}";

    /**
     * Example response for a validation error (400 Bad Request).
     * This response occurs when the request fails due to invalid data, such as incorrect formats or missing fields.
     */
    public static final String IMPORT_BANK_STATEMENT_VALIDATION_ERROR = "{\n" +
        "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.NO_VALID_BANK_STATEMENT + "\"\n" +
        "}";

    /**
     * Example response for an unauthorized request (401 Unauthorized).
     * This response indicates that the request was made with invalid or missing credentials.
     */
    public static final String IMPORT_BANK_STATEMENT_UNAUTHORIZED = "{\n" +
        "    \"code\": " + ApiResponseConstants.UNAUTHORIZED_CODE_STRING + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.INVALID_PARTNER_TOKEN + "\"\n" +
        "}";

    /**
     * Example response for a forbidden request (403 Forbidden).
     * This response indicates that the partner code is not authorized to perform the requested action.
     */
    public static final String IMPORT_BANK_STATEMENT_FORBIDDEN = "{\n" +
        "    \"code\": " + ApiResponseConstants.FORBIDDEN_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.FORBIDDEN_PARTNER_TOKEN + "\"\n" +
        "}";

    /**
     * Example response for a missing FMIS base URL (404 Not Found).
     * This response occurs when the FMIS base URL cannot be found, meaning the target endpoint is unavailable.
     */
    public static final String IMPORT_BANK_STATEMENT_NOT_FOUND = "{\n" +
        "    \"code\": " + ApiResponseConstants.NOT_FOUND_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.NO_FMIS_CONFIG_FOUND + "\"\n" +
        "}";

    /**
     * Example response for a failed FMIS request (502 Bad Gateway).
     * This response indicates that an error occurred while attempting to communicate with the FMIS system.
     */
    public static final String IMPORT_BANK_STATEMENT_FMIS_FAILURE = "{\n" +
        "    \"code\": " + ApiResponseConstants.BAD_GATEWAY_CODE + ",\n" +
        "    \"message\": \"" + ApiResponseConstants.ERROR_SENDING_TO_FMIS + "\"\n" +
        "}";

    /**
     * Example response for a general server error (500 Internal Server Error).
     * This response indicates that a general server error occurred while processing the bank statement import.
     */
    public static final String IMPORT_BANK_STATEMENT_SERVER_ERROR = "{\n" +
            "    \"code\": " + ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.INTERNAL_SERVER_ERROR + "\"\n" +
            "}";
}
