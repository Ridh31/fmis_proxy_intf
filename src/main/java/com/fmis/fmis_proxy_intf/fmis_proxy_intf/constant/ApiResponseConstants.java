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

    /*
    |--------------------------------------------------------------------------
    | Success Messages and Status Codes
    |--------------------------------------------------------------------------
    */
    /** HTTP Status Code [200] - OK: The request has been successfully processed */
    public static final int SUCCESS_CODE = 200;
    public static final String SUCCESS_CODE_STRING = "200";
    public static final String SUCCESS = "The request has been processed successfully.";

    /** HTTP Status Code [201] - Created: The resource has been created successfully */
    public static final int CREATED_CODE = 201;
    public static final String CREATED_CODE_STRING = "201";
    public static final String CREATED = "The resource has been created successfully.";

    /** HTTP Status Code [200] - OK: The resource has been updated successfully */
    public static final int UPDATED_CODE = 200;
    public static final String UPDATED_CODE_STRING = "200";
    public static final String UPDATED = "The resource has been updated successfully.";

    /** HTTP Status Code [200] - OK: The resource has been deleted successfully */
    public static final int DELETED_CODE = 200;
    public static final String DELETED_CODE_STRING = "200";
    public static final String DELETED = "The resource has been deleted successfully.";

    /** Success message for password reset */
    public static final String PASSWORD_RESET_SUCCESS = "Password reset successfully for user: ";
    public static final String BANK_STATEMENT_SAVED = "Bank statement saved successfully.";
    public static final String BANK_STATEMENTS_FETCHED = "Bank statements fetched successfully.";
    public static final String PARTNERS_FETCHED = "Partners fetched successfully.";

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
    public static final String BAD_REQUEST = "Invalid request. Please check the input parameters.";
    public static final String NO_VALID_BANK_STATEMENT = "Bad Request: No valid bank statement data provided.";
    public static final String PARTNER_CODE_TAKEN = "Bad Request: Partner code is already taken.";
    public static final String USERNAME_TAKEN = "Bad Request: Username is already taken.";
    public static final String ERROR_USERNAME_MISSING_OR_EMPTY = "Bad Request: 'Username' cannot be missing or empty.";
    public static final String EMAIL_TAKEN = "Bad Request: Email is already taken.";
    public static final String BAD_REQUEST_MISSING_PARTNER_TOKEN = "Bad Request: '" + HeaderConstants.X_PARTNER_TOKEN + "' header cannot be missing or empty.";

    /**
     * 401 Unauthorized Errors - Authentication issues
     * HTTP Status Code [401] - Unauthorized: The user is not authorized to perform the action
     */
    public static final int UNAUTHORIZED_CODE = 401;
    public static final String UNAUTHORIZED_CODE_STRING = "401";
    public static final String UNAUTHORIZED = "You are not authorized to perform this action.";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access. Please provide valid credentials.";
    public static final String UNAUTHORIZED_LOGIN_REQUIRED = "Unauthorized: You must be logged in to create a partner.";
    public static final String UNAUTHORIZED_USER_NOT_FOUND = "Unauthorized: The user does not exist.";
    public static final String INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String INVALID_PARTNER_TOKEN = "Unauthorized: Invalid partner token.";

    /**
     * 403 Forbidden Errors - Insufficient permissions
     * HTTP Status Code [403] - Forbidden: The user does not have permission to access the resource
     */
    public static final int FORBIDDEN_CODE = 403;
    public static final String FORBIDDEN_CODE_STRING = "403";
    public static final String FORBIDDEN = "You do not have permission to access this resource.";
    public static final String FORBIDDEN_CREATE_PARTNER = "Forbidden: You do not have permission to create a partner.";
    public static final String FORBIDDEN_RESET_PASSWORD = "You do not have permission to reset passwords.";
    public static final String FORBIDDEN_PARTNER_TOKEN = "Forbidden: Partner token validation failed.";

    /**
     * 404 Not Found Errors - Resource not found
     * HTTP Status Code [404] - Not Found: The requested resource was not found
     */
    public static final int NOT_FOUND_CODE = 404;
    public static final String NOT_FOUND_CODE_STRING = "404";
    public static final int NO_CONTENT_CODE = 204;
    public static final String NO_CONTENT_CODE_STRING = "204";
    public static final String NOT_FOUND = "Requested resource not found.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String ROLE_NOT_FOUND = "Role not found.";
    public static final String BASE_URL_NOT_FOUND = "Base URL not found.";
    public static final String NO_PARTNERS_FOUND = "No partners found.";
    public static final String NO_BANK_STATEMENTS_FOUND = "No bank statements found.";
    public static final String NO_FMIS_CONFIG_FOUND = "FMIS Configuration Not Found.";

    /**
     * 500 Internal Server Error - Server issues
     * HTTP Status Code [500] - Internal Server Error: An unexpected error occurred
     */
    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final String INTERNAL_SERVER_ERROR_CODE_STRING = "500";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String ERROR_OCCURRED = "An error occurred: ";
    public static final String ERROR_FETCHING_BANK_STATEMENTS = "An error occurred while fetching bank statements: ";
    public static final String ERROR_FETCHING_PARTNERS = "An error occurred while fetching partners: ";
    public static final String ERROR_READING_FILE = "Error occurred while reading the documentation file.";
    public static final String ERROR_PARTNER_TOKEN_NOT_FOUND = "Partner with the provided token not found.";

    /**
     * 502 Bad Gateway Error - Issues communicating with external services
     * HTTP Status Code [502] - Bad Gateway: Issues communicating with external services
     */
    public static final int BAD_GATEWAY_CODE = 502;
    public static final String BAD_GATEWAY_CODE_STRING = "502";
    public static final String BAD_GATEWAY_NOT_CONNECT = "Could not connect to any target host";

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
