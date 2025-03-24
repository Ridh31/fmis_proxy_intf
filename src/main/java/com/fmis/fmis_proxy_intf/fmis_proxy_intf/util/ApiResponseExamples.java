package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

public class ApiResponseExamples {

    // Partner Creation Responses
    /**
     * Example response for a successful partner creation (201 Created)
     */
    public static final String CREATE_PARTNER_SUCCESS = """
        {
            "code": "201",
            "message": "Partner created successfully!",
            "data": {
                "id": 1,
                "code": "PART123",
                "publicKey": "MIIBIjANBgkqh...",
                "createdBy": 42
            }
        }
    """;

    /**
     * Example response for a bad request (400 Bad Request)
     */
    public static final String CREATE_PARTNER_BAD_REQUEST = """
        {
            "code": "400",
            "message": "Bad Request: Partner code 'PART123' is already taken."
        }
    """;

    /**
     * Example response for an unauthorized request (401 Unauthorized)
     */
    public static final String CREATE_PARTNER_UNAUTHORIZED = """
        {
            "code": "401",
            "message": "Unauthorized: You must be logged in to create a partner."
        }
    """;

    /**
     * Example response for a not found error (404 Not Found)
     */
    public static final String CREATE_PARTNER_NOT_FOUND = """
        {
            "code": "404",
            "message": "Not Found: User not found"
        }
    """;

    /**
     * Example response for a server error (500 Internal Server Error)
     */
    public static final String CREATE_PARTNER_SERVER_ERROR = """
        {
            "code": "500",
            "message": "Internal Server Error: Unexpected error occurred."
        }
    """;

    // Bank Statement Import Responses
    /**
     * Example response for a successful bank statement import (201 Created)
     */
    public static final String IMPORT_BANK_STATEMENT_SUCCESS = """
        {
            "status": "201",
            "message": "Bank statement saved successfully.",
            "data": "<FMIS response body here>"
        }
    """;

    /**
     * Example response for a missing X-Partner-Token (400 Bad Request)
     */
    public static final String IMPORT_BANK_STATEMENT_MISSING_PARTNER_TOKEN = """
        {
            "status": "400",
            "message": "Bad Request: 'X-Partner-Token' header cannot be missing or empty."
        }
    """;

    /**
     * Example response for a validation error (400 Bad Request)
     */
    public static final String IMPORT_BANK_STATEMENT_VALIDATION_ERROR = """
        {
            "status": "400",
            "message": {
                "field1": "This field is required",
                "field2": "Invalid format"
            }
        }
    """;

    /**
     * Example response for an unauthorized request (401 Unauthorized)
     */
    public static final String IMPORT_BANK_STATEMENT_UNAUTHORIZED = """
        {
            "status": "401",
            "message": "Unauthorized: Invalid partner code."
        }
    """;

    /**
     * Example response for a forbidden request (403 Forbidden)
     */
    public static final String IMPORT_BANK_STATEMENT_FORBIDDEN = """
        {
            "status": "403",
            "message": "Forbidden: Partner code validation failed."
        }
    """;

    /**
     * Example response for a missing FMIS base URL (404 Not Found)
     */
    public static final String IMPORT_BANK_STATEMENT_NOT_FOUND = """
        {
            "status": "404",
            "message": "Base URL not found"
        }
    """;

    /**
     * Example response for a failed FMIS request (502 Bad Gateway)
     */
    public static final String IMPORT_BANK_STATEMENT_FMIS_FAILURE = """
        {
            "status": "502",
            "message": "Failed to send data to FMIS: <FMIS error response>"
        }
    """;

    /**
     * Example response for a general server error (500 Internal Server Error)
     */
    public static final String IMPORT_BANK_STATEMENT_SERVER_ERROR = """
        {
            "status": "500",
            "message": "Internal Server Error: An unexpected error occurred."
        }
    """;
}
