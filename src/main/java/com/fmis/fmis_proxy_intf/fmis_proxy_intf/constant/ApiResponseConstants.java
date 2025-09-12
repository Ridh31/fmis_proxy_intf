package com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant;

/**
 * This class contains constants for common success and error messages,
 * as well as their corresponding HTTP status codes.
 * These constants ensure consistency across the codebase and avoid hardcoding values in multiple places.
 */
public final class ApiResponseConstants {

    // Response type labels
    public static final String RESPONSE_TYPE_SUCCESS = "Successful Response";
    public static final String RESPONSE_TYPE_BAD_REQUEST = "Bad Request Response";
    public static final String RESPONSE_TYPE_MISSING_PARTNER_TOKEN_HEADER = "Missing X-Partner-Token Header";
    public static final String RESPONSE_TYPE_VALIDATION_ERROR = "Validation Error Response";
    public static final String RESPONSE_TYPE_UNAUTHORIZED = "Unauthorized Access";
    public static final String RESPONSE_TYPE_FORBIDDEN = "Forbidden Access";
    public static final String RESPONSE_TYPE_NOT_FOUND = "Resource Not Found";
    public static final String RESPONSE_TYPE_FMIS_FAILURE = "FMIS Integration Failure";
    public static final String RESPONSE_TYPE_SERVER_ERROR = "Internal Server Error";

    // Additional specific bad request response types
    public static final String RESPONSE_TYPE_FILE_MISSING_OR_EMPTY = "Missing or Empty File";
    public static final String RESPONSE_TYPE_INVALID_FILE_TYPE = "Invalid File Type";
    public static final String RESPONSE_TYPE_FAILED_TO_PARSE_JSON = "Malformed or Unreadable JSON";

    /*
    |--------------------------------------------------------------------------
    | Success Messages and Status Codes
    |--------------------------------------------------------------------------
    */
    /** HTTP Status Code [200] - OK: Processed */
    public static final int SUCCESS_CODE = 200;
    public static final String SUCCESS_CODE_STRING = "200";
    public static final String SUCCESS = "The request has been processed successfully.";

    /** HTTP Status Code [201] - Created */
    public static final int CREATED_CODE = 201;
    public static final String CREATED_CODE_STRING = "201";
    public static final String CREATED = "The resource has been created successfully.";

    /** HTTP Status Code [200] - Updated */
    public static final int UPDATED_CODE = 200;
    public static final String UPDATED_CODE_STRING = "200";
    public static final String UPDATED = "The resource has been updated successfully.";

    /** HTTP Status Code [200] - Deleted */
    public static final int DELETED_CODE = 200;
    public static final String DELETED_CODE_STRING = "200";
    public static final String DELETED = "The resource has been deleted successfully.";

    /** Entity-based Success Messages */
    public static final String ENTITY_PROCESSED_SUCCESS = "%s has been processed successfully";
    public static final String ENTITY_CREATED_SUCCESS = "%s has been created successfully";
    public static final String ENTITY_SAVED_SUCCESS = "%s has been saved successfully.";
    public static final String ENTITY_IMPORTED_SUCCESS = "%s has been imported successfully.";
    public static final String ENTITY_FETCHED_SUCCESS = "%s has been fetched successfully.";
    public static final String ENTITY_UPDATED_SUCCESS = "%s has been updated successfully.";
    public static final String ENTITY_DELETED_SUCCESS = "%s has been deleted successfully.";
    public static final String ENTITY_RESET_SUCCESS = "%s has been reset successfully.";

    /** 2xx Success - Document-based Additional Messages */
    public static final String LOGIN_SUCCESS = "Login successful.";
    public static final String ACCESS_GRANTED = "Access granted: Admin privileges verified.";
    public static final String ACCESS_DENIED = "Access denied: Insufficient privileges.";
    public static final String BANK_STATEMENT_IMPORTED = "Bank statement has been imported successfully.";

    /**
     * 204 No Content Found - Content not found
     * HTTP Status Code [204] - No Content Found
     */
    public static final int NO_CONTENT_CODE = 204;
    public static final String NO_CONTENT_CODE_STRING = "204";
    public static final String NO_CONTENT_ERROR = "No content received from %s response.";

    /*
    |--------------------------------------------------------------------------
    | Error Messages and Status Codes
    |--------------------------------------------------------------------------
    */
    /**
     * 400 Bad Request Errors - Invalid input or parameters
     * HTTP Status Code [400] - Bad Request: Invalid request due to incorrect input parameters
     */
    public static final int BAD_REQUEST_CODE = 400;
    public static final String BAD_REQUEST_CODE_STRING = "400";
    public static final String BAD_REQUEST = "Bad request. Please check the input parameters.";
    public static final String BAD_REQUEST_INVALID_ENTITY = "Bad Request: Missing or invalid %s was provided.";
    public static final String BAD_REQUEST_ENTITY_TAKEN = "Bad Request: %s is already taken.";
    public static final String BAD_REQUEST_VALIDATION_FAILED = "Bad Request: The following fields are invalid: %s.";
    public static final String BAD_REQUEST_FAILED_PROCESS = "Bad Request: Failed to process %s.";
    public static final String BAD_REQUEST_INVALID_FIELD_CONDITION = "Bad Request: Invalid %s. Must be a %s value.";
    public static final String BAD_REQUEST_CONFIGURATION_NOT_FOUND = "No configuration found or available. (%s)";
    public static final String BAD_REQUEST_EXTERNAL = "The request sent to the external server for '%s' was invalid.";
    public static final String BAD_REQUEST_ERROR_FIELD_REQUIRED = "Bad Request: Missing or empty required parameter. (%s)";
    public static final String BAD_REQUEST_ERROR_HEADER_REQUIRED = "Bad Request: '%s' header cannot be missing or empty.";
    public static final String BAD_REQUEST_ERROR_JWT_VALIDATION_FAILED = "JWT validation failed: no response, token expired or invalid.";

    /** 400 Bad Request Errors - Document-based Additional Messages */
    public static final String BAD_REQUEST_NO_VALID_BANK_STATEMENT = "Bad Request: No valid bank statement data provided.";
    public static final String BAD_REQUEST_PARTNER_NAME_TAKEN = "Bad Request: Partner name is already taken.";
    public static final String BAD_REQUEST_PARTNER_IDENTIFIER_TAKEN = "Bad Request: Partner identifier is already taken.";
    public static final String BAD_REQUEST_PARTNER_CODE_TAKEN = "Bad Request: Partner code is already taken.";
    public static final String BAD_REQUEST_MISSING_PARTNER_TOKEN = "Bad Request: '" + HeaderConstants.X_PARTNER_TOKEN + "' header cannot be missing or empty.";
    public static final String BAD_REQUEST_FILE_MISSING_OR_EMPTY = "Bad Request: File is missing or empty.";
    public static final String BAD_REQUEST_INVALID_FILE_TYPE = "Bad Request: Invalid file type. Only JSON is allowed.";
    public static final String BAD_REQUEST_FAILED_PROCESS_FILE = "Bad Request: Failed to process file.";
    public static final String BAD_REQUEST_NO_BANK_STATEMENT_RECORDS = "There are no statement records. (Entry: *)";

    /**
     * 401 Unauthorized Errors - Authentication issues
     * HTTP Status Code [401] - Unauthorized: The user is not authorized to perform the action
     */
    public static final int UNAUTHORIZED_CODE = 401;
    public static final String UNAUTHORIZED_CODE_STRING = "401";
    public static final String UNAUTHORIZED = "Unauthorized: You must be logged in to %s.";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access. Please provide valid credentials.";
    public static final String UNAUTHORIZED_LOGIN_REQUIRED = "Unauthorized: You must be logged in to perform the action.";
    public static final String UNAUTHORIZED_INVALID_CREDENTIALS = "Unauthorized: Invalid username or password.";
    public static final String UNAUTHORIZED_INVALID_PARTNER_TOKEN = "Unauthorized: Invalid partner token.";
    public static final String UNAUTHORIZED_ACCESS_TOKEN_INVALID_OR_EXPIRED = "Unauthorized: Access token is invalid or expired.";

    /**
     * 403 Forbidden Errors - Insufficient permissions
     * HTTP Status Code [403] - Forbidden: The user does not have permission to access the resource
     */
    public static final int FORBIDDEN_CODE = 403;
    public static final String FORBIDDEN_CODE_STRING = "403";
    public static final String FORBIDDEN = "Forbidden: You do not have permission to %s.";

    /** 403 Forbidden Errors - Document-based Additional Messages */
    public static final String FORBIDDEN_PARTNER_TOKEN = "Forbidden: Partner token validation failed.";

    /**
     * 404 Not Found Errors - Resource not found
     * HTTP Status Code [404] - Not Found
     */
    public static final int NOT_FOUND_CODE = 404;
    public static final String NOT_FOUND_CODE_STRING = "404";
    public static final String NOT_FOUND_ENTITY = "%s was not found. It may have been removed or never existed.";
    public static final String NOT_FOUND_EXTERNAL_RESOURCE = "The requested resource '%s' was not found on the external server.";

    /** 404 Not Found Errors - Document-based Additional Messages */
    public static final String NOT_FOUND = "Requested resource not found.";
    public static final String NOT_FOUND_RESOURCE = "Resource not found. The requested URL does not exist.";
    public static final String NOT_FOUND_USER = "User not found.";
    public static final String NOT_FOUND_FMIS_CONFIG = "FMIS Configuration Not Found.";

    /**
     * 409 Conflict - Resource conflict
     * HTTP Status Code [409] - Conflict:
     */
    public static final int CONFLICT = 409;
    public static final String CONFLICT_STRING = "409";
    public static final String CONFLICT_ENTITY = "Conflict: %s already exists or conflicts with existing data.";

    /**
     * 415 Unsupported Media Type - Content Type
     * HTTP Status Code [415] - Unsupported Media Type:
     */
    public static final int UNSUPPORTED_MEDIA_TYPE_CODE = 415;
    public static final String UNSUPPORTED_MEDIA_TYPE_CODE_STRING = "415";
    public static final String UNSUPPORTED_MEDIA_TYPE = "Unsupported media type: %s.";

    /**
     * 500 Internal Server Error - Server issues
     * HTTP Status Code [500] - Internal Server Error: An unexpected error occurred
     */
    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final String INTERNAL_SERVER_ERROR_CODE_STRING = "500";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String INTERNAL_SERVER_ERROR_OCCURRED = "An error occurred while processing %s. Please try again later.";
    public static final String INTERNAL_SERVER_ERROR_FETCHING_ENTITY = "An error occurred while fetching %s. Please try again later.";
    public static final String EXTERNAL_CLIENT_ERROR = "An error occurred while communicating with the external server for '%s'.";

    /**
     * 502 Bad Gateway Error - Issues communicating with external services
     * HTTP Status Code [502] - Bad Gateway: Issues communicating with external services
     */
    public static final int BAD_GATEWAY_CODE = 502;
    public static final String BAD_GATEWAY_CODE_STRING = "502";
    public static final String BAD_GATEWAY_NOT_CONNECT = "Could not connect to the target host '%s'.";
    public static final String BAD_GATEWAY_UPSTREAM_SERVICE_ERROR_MESSAGE = "Bad gateway: upstream service returned an invalid response.";

    /**
     * 503 Service Unavailable Error - Temporary service issues
     * HTTP Status Code [503] - Service Unavailable: Temporary issues with the service
     */
    public static final int SERVICE_UNAVAILABLE_CODE = 503;
    public static final String SERVICE_UNAVAILABLE_CODE_STRING = "503";
    public static final String SERVICE_UNAVAILABLE = "Service is temporarily unavailable. Please try again later.";

    // Private constructor to prevent instantiation
    private ApiResponseConstants() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }
}