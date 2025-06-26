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
    /** HTTP Status Code [200] - OK: The request has been successfully processed */
    public static final int SUCCESS_CODE = 200;
    public static final String SUCCESS_CODE_STRING = "200";
    public static final String SUCCESS = "The request has been processed successfully.";
    public static final String ACCESS_GRANTED = "Access granted: Admin privileges verified.";
    public static final String ACCESS_DENIED = "Access denied: Insufficient privileges.";

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
    public static final String USERS_FETCHED = "Users fetched successfully.";

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
    public static final String NO_CONFIG_TO_UPDATE = "There is no FMIS configuration in the database to update.";
    public static final String PARTNER_NAME_TAKEN = "Bad Request: Partner name is already taken.";
    public static final String PARTNER_IDENTIFIER_TAKEN = "Bad Request: Partner identifier is already taken.";
    public static final String PARTNER_SYSTEM_CODE_TAKEN = "Bad Request: Partner system or bank code is already taken.";
    public static final String PARTNER_CODE_TAKEN = "Bad Request: Partner code is already taken.";
    public static final String USERNAME_TAKEN = "Bad Request: Username is already taken.";
    public static final String NAME_TAKEN = "Bad Request: Name is already taken.";
    public static final String APP_KEY_TAKEN = "Bad Request: App key is already taken.";
    public static final String IP_ADDRESS_TAKEN = "Bad Request: IP Address is already taken.";
    public static final String ACCESS_URL_TAKEN = "Bad Request: Access URL is already taken.";
    public static final String ERROR_USERNAME_MISSING_OR_EMPTY = "Bad Request: 'Username' cannot be missing or empty.";
    public static final String EMAIL_TAKEN = "Bad Request: Email is already taken.";
    public static final String BAD_REQUEST_MISSING_PARTNER_TOKEN = "Bad Request: '" + HeaderConstants.X_PARTNER_TOKEN + "' header cannot be missing or empty.";
    public static final String BAD_REQUEST_FILE_MISSING_OR_EMPTY = "Bad Request: File is missing or empty.";
    public static final String BAD_REQUEST_INVALID_FILE_TYPE = "Bad Request: Invalid file type. Only JSON is allowed.";
    public static final String BAD_REQUEST_FAILED_TO_PARSE_JSON = "Bad Request: Failed to read or parse JSON file.";
    public static final String BAD_REQUEST_INVALID_DATA = "An error occurred due to a missing or invalid 'Data' field. Please ensure the data is correct and try again.";
    public static final String BAD_REQUEST_ID_NOT_NUMERIC = "Invalid ID. Must be a numeric value.";
    public static final String BAD_REQUEST_PARTNER_ID_NOT_NUMERIC = "Invalid partner ID. Must be a numeric value.";
    public static final String BAD_REQUEST_INVALID_STATUS_VALUE = "Invalid status value. Allowed values are: true or false.";
    public static final String ERROR_MISSING_REQUIRED_PARAM = "Missing or empty required parameter: ";
    public static final String ERROR_NO_CONFIGURATION_FOUND = "No configuration found for the provided input.";
    public static final String EXTERNAL_BAD_REQUEST = "The request sent to the external server was invalid.";
    public static final String ERROR_JWT_VALIDATION_FAILED = "JWT validation failed: no response, token expired or invalid.";
    public static final String BAD_REQUEST_ACCOUNT_NUMBER_MISMATCH = "The bank account number field (CMB_BANK_ACCOUNT_N) mismatch: provided '%s', but found '%s'. (Entry: %d)";

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
    public static final String ERROR_ACCESS_TOKEN_INVALID_OR_EXPIRED = "Access token is invalid or expired.";

    /**
     * 403 Forbidden Errors - Insufficient permissions
     * HTTP Status Code [403] - Forbidden: The user does not have permission to access the resource
     */
    public static final int FORBIDDEN_CODE = 403;
    public static final String FORBIDDEN_CODE_STRING = "403";
    public static final String FORBIDDEN = "You do not have permission to access this resource.";
    public static final String FORBIDDEN_CREATE_PARTNER = "Forbidden: You do not have permission to create a partner.";
    public static final String FORBIDDEN_UPDATE_PARTNER = "Forbidden: You do not have permission to update a partner.";
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
    public static final String HOST_NOT_FOUND = "Host not found.";
    public static final String BASE_URL_NOT_FOUND = "Base URL not found.";
    public static final String NO_PARTNERS_FOUND = "No partners found.";
    public static final String NO_BANK_STATEMENTS_FOUND = "No bank statements found.";
    public static final String NO_FMIS_CONFIG_FOUND = "FMIS Configuration Not Found.";
    public static final String EXTERNAL_RESOURCE_NOT_FOUND = "The requested resource was not found on the external server.";

    /**
     * 500 Internal Server Error - Server issues
     * HTTP Status Code [500] - Internal Server Error: An unexpected error occurred
     */
    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final String INTERNAL_SERVER_ERROR_CODE_STRING = "500";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String ERROR_OCCURRED = "An error occurred: ";
    public static final String ERROR_FETCHING_BANK_STATEMENTS = "An error occurred while fetching bank statements: ";
    public static final String ERROR_FETCHING_USERS = "An error occurred while fetching users: ";
    public static final String ERROR_FETCHING_PARTNERS = "An error occurred while fetching partners: ";
    public static final String ERROR_READING_FILE = "Error occurred while reading the documentation file.";
    public static final String ERROR_PARTNER_TOKEN_NOT_FOUND = "Partner with the provided token not found.";
    public static final String ERROR_FMIS_RESPONSE_PARSE = "Failed to parse FMIS XML response.";
    public static final String ERROR_FMIS_RESPONSE_EMPTY = "No content received from FMIS response.";
    public static final String EXTERNAL_SERVER_ERROR = "An internal error occurred on the external server.";
    public static final String EXTERNAL_CLIENT_ERROR = "An error occurred while communicating with the external server.";

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
