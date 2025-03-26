package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

public class ApiResponseExamples {

    /*
    |--------------------------------------------------------------------------
    | Partner Creation Responses
    |--------------------------------------------------------------------------
    */
    /**
     * Example response for a successful partner creation (201 Created).
     * This response indicates that the partner was successfully created in the system.
     */
    public static final String CREATE_PARTNER_SUCCESS = """
        {
            "code": "201",
            "message": "Partner created successfully!",
            "data": {
                "id": 1,
                "code": "PART123",
                "publicKey": "MIIBIjANBgkqh...",  // Public key associated with the partner
                "createdBy": 42  // ID of the user who created the partner
            }
        }
    """;

    /**
     * Example response for a bad request (400 Bad Request).
     * This response occurs when the partner creation fails due to invalid data, e.g., duplicate partner code.
     */
    public static final String CREATE_PARTNER_BAD_REQUEST = """
        {
            "code": "400",
            "message": "Bad Request: Partner code 'PART123' is already taken."
        }
    """;

    /**
     * Example response for an unauthorized request (401 Unauthorized).
     * This response indicates that the request was made by an unauthenticated user.
     */
    public static final String CREATE_PARTNER_UNAUTHORIZED = """
        {
            "code": "401",
            "message": "Unauthorized: You must be logged in to create a partner."
        }
    """;

    /**
     * Example response for a not found error (404 Not Found).
     * This response occurs when the user referenced in the request cannot be found in the system.
     */
    public static final String CREATE_PARTNER_NOT_FOUND = """
        {
            "code": "404",
            "message": "Not Found: User not found"
        }
    """;

    /**
     * Example response for a server error (500 Internal Server Error).
     * This response occurs when an unexpected error happens on the server while processing the request.
     */
    public static final String CREATE_PARTNER_SERVER_ERROR = """
        {
            "code": "500",
            "message": "Internal Server Error: Unexpected error occurred."
        }
    """;

    /*
    |--------------------------------------------------------------------------
    | Bank Statement Import Responses
    |--------------------------------------------------------------------------
    */
    /**
     * Example response for a successful bank statement import (201 Created).
     * This response indicates that the bank statement was successfully imported into the system.
     */
    public static final String IMPORT_BANK_STATEMENT_SUCCESS = """
        {
            "status": "201",
            "message": "Bank statement saved successfully.",
            "data": "<FMIS response body here>"  // Replace with actual FMIS response body
        }
    """;

    /**
     * Example response for a missing X-Partner-Token (400 Bad Request).
     * This response occurs when the 'X-Partner-Token' header is missing or empty in the request.
     */
    public static final String IMPORT_BANK_STATEMENT_MISSING_PARTNER_TOKEN = """
        {
            "status": "400",
            "message": "Bad Request: 'X-Partner-Token' header cannot be missing or empty."
        }
    """;

    /**
     * Example response for a validation error (400 Bad Request).
     * This response occurs when the request fails due to invalid data, such as incorrect formats or missing fields.
     */
    public static final String IMPORT_BANK_STATEMENT_VALIDATION_ERROR = """
        {
            "status": "400",
            "message": {
                "field1": "This field is required",  // Example validation message for field1
                "field2": "Invalid format"  // Example validation message for field2
            }
        }
    """;

    /**
     * Example response for an unauthorized request (401 Unauthorized).
     * This response indicates that the request was made with invalid or missing credentials.
     */
    public static final String IMPORT_BANK_STATEMENT_UNAUTHORIZED = """
        {
            "status": "401",
            "message": "Unauthorized: Invalid partner code."
        }
    """;

    /**
     * Example response for a forbidden request (403 Forbidden).
     * This response indicates that the partner code is not authorized to perform the requested action.
     */
    public static final String IMPORT_BANK_STATEMENT_FORBIDDEN = """
        {
            "status": "403",
            "message": "Forbidden: Partner code validation failed."
        }
    """;

    /**
     * Example response for a missing FMIS base URL (404 Not Found).
     * This response occurs when the FMIS base URL cannot be found, meaning the target endpoint is unavailable.
     */
    public static final String IMPORT_BANK_STATEMENT_NOT_FOUND = """
        {
            "status": "404",
            "message": "Base URL not found"
        }
    """;

    /**
     * Example response for a failed FMIS request (502 Bad Gateway).
     * This response indicates that an error occurred while attempting to communicate with the FMIS system.
     */
    public static final String IMPORT_BANK_STATEMENT_FMIS_FAILURE = """
        {
            "status": "502",
            "message": "Failed to send data to FMIS: <FMIS error response>"
        }
    """;

    /**
     * Example response for a general server error (500 Internal Server Error).
     * This response indicates that a general server error occurred while processing the bank statement import.
     */
    public static final String IMPORT_BANK_STATEMENT_SERVER_ERROR = """
        {
            "status": "500",
            "message": "Internal Server Error: An unexpected error occurred."
        }
    """;
}
