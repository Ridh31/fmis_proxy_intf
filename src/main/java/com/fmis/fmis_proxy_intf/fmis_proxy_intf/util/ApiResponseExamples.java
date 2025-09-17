package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.FmisResponseCodes;

public class ApiResponseExamples {

    /*
    |--------------------------------------------------------------------------
    | Test Responses
    |--------------------------------------------------------------------------
    */
    /**
     * Example response for a basic test (200 OK).
     * This response indicates that the FMIS Interface Web Service is running successfully.
     */
    public static final String BASIC_TEST = "{\n" +
            "  \"code\": " + ApiResponseConstants.SUCCESS_CODE + ",\n" +
            "  \"response_code\": " + FmisResponseCodes.FMIS_SUCCESS_PROCESSED + ",\n" +
            "  \"message\": \"FMIS Interface Web Service.\"\n" +
            "}";

    /**
     * Example response for testing SARMIS connectivity (200 OK).
     * This response indicates that SARMIS connectivity was successful with no errors.
     */
    public static final String SARMIS_TEST = "{\n" +
            "  \"message\": \"Successful\",\n" +
            "  \"error\": \"0\",\n" +
            "  \"data\": \"Welcome!\"\n" +
            "}";

    /**
     * Example response for a successful FMIS test (200 OK).
     * This response indicates that the FMIS test was successful, returning XML data.
     */
    public static final String FMIS_TEST_SUCCESS = "{\n" +
            "  \"code\": " + ApiResponseConstants.SUCCESS_CODE + ",\n" +
            "  \"response_code\": " + FmisResponseCodes.FMIS_SUCCESS_PROCESSED + ",\n" +
            "  \"message\": \"" + ApiResponseConstants.SUCCESS + "\",\n" +
            "  \"data\": \"<?xml version=\\\"1.0\\\"?>\\r\\n<response><data><item1>Hello FMIS Internface API Webservice</item1><item2>Your payload is: test</item2></data></response>\"\n" +
            "}";

    /**
     * Example response when FMIS configuration is not found (404 Not Found).
     * This response indicates that the FMIS configuration was not found in the system.
     */
    public static final String FMIS_TEST_NO_CONFIG_FOUND = "{\n" +
            "  \"code\": " + ApiResponseConstants.NOT_FOUND_CODE + ",\n" +
            "  \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_CONFIGURATION_NOT_FOUND + ",\n" +
            "  \"message\": \"" + ApiResponseConstants.NOT_FOUND_FMIS_CONFIG + "\"\n" +
            "}";

    /**
     * Example response for a bad gateway error (502 Bad Gateway).
     * This response indicates that the system was unable to connect to the FMIS server.
     */
    public static final String FMIS_TEST_BAD_GATEWAY = "{\n" +
            "  \"code\": " + ApiResponseConstants.BAD_GATEWAY_CODE + ",\n" +
            "  \"response_code\": " + FmisResponseCodes.FMIS_BAD_GATEWAY_NOT_CONNECT + ",\n" +
            "  \"message\": \"" + ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT + "\"\n" +
            "}";

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
            "    \"response_code\": " + FmisResponseCodes.FMIS_SUCCESS_CREATED + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.CREATED + "\",\n" +
            "    \"data\": " + ApiResponseExamples.CREATE_PARTNER_DATA + "\n" +
            "}";

    /**
     * Example response for a bad request (400 Bad Request).
     * This response occurs when the partner creation fails due to invalid data, e.g., duplicate partner name.
     */
    public static final String CREATE_PARTNER_BAD_REQUEST_NAME_TAKEN = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_TAKEN + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_PARTNER_NAME_TAKEN + "\"\n" +
            "}";

    /**
     * Example response for a bad request (400 Bad Request).
     * This response occurs when the partner creation fails due to invalid data, e.g., duplicate partner identifer.
     */
    public static final String CREATE_PARTNER_BAD_REQUEST_IDENTIFIER_TAKEN = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_TAKEN + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_PARTNER_IDENTIFIER_TAKEN + "\"\n" +
            "}";

    /**
     * Example response for a bad request (400 Bad Request).
     * This response occurs when the partner creation fails due to invalid data, e.g., duplicate partner code.
     */
    public static final String CREATE_PARTNER_BAD_REQUEST_CODE_TAKEN = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_TAKEN + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_PARTNER_CODE_TAKEN + "\"\n" +
            "}";

    /**
     * Example response for an unauthorized request (401 Unauthorized).
     * This response indicates that the request was made by an unauthenticated user.
     */
    public static final String CREATE_PARTNER_UNAUTHORIZED = "{\n" +
            "    \"code\": " + ApiResponseConstants.UNAUTHORIZED_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_UNAUTHORIZED + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.UNAUTHORIZED_LOGIN_REQUIRED + "\"\n" +
            "}";

    /**
     * Example response for a not found error (404 Not Found).
     * This response occurs when the user referenced in the request cannot be found in the system.
     */
    public static final String CREATE_PARTNER_NOT_FOUND = "{\n" +
            "    \"code\": " + ApiResponseConstants.NOT_FOUND_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_NOT_FOUND + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.NOT_FOUND_USER + "\"\n" +
            "}";

    /**
     * Example response for a server error (500 Internal Server Error).
     * This response occurs when an unexpected error happens on the server while processing the request.
     */
    public static final String CREATE_PARTNER_SERVER_ERROR = "{\n" +
            "    \"code\": " + ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_INTERNAL_ERROR + ",\n" +
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
            "    \"response_code\": " + FmisResponseCodes.FMIS_SUCCESS_CREATED + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BANK_STATEMENT_IMPORTED + "\"\n" +
            "}";

    /**
     * Example response for a missing X-Partner-Token (400 Bad Request).
     * This response occurs when the 'X-Partner-Token' header is missing or empty in the request.
     */
    public static final String IMPORT_BANK_STATEMENT_MISSING_PARTNER_TOKEN = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_INVALID_ENTITY + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_MISSING_PARTNER_TOKEN + "\"\n" +
            "}";

    /**
     * Example response for a validation error (400 Bad Request).
     * This response occurs when the request fails due to invalid data, such as incorrect formats or missing fields.
     */
    public static final String IMPORT_BANK_STATEMENT_VALIDATION_ERROR = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_INVALID_ENTITY + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_NO_VALID_BANK_STATEMENT + "\"\n" +
            "}";

    /**
     * Example response for an unauthorized request (401 Unauthorized).
     * This response indicates that the request was made with invalid or missing credentials.
     */
    public static final String IMPORT_BANK_STATEMENT_UNAUTHORIZED = "{\n" +
            "    \"code\": " + ApiResponseConstants.UNAUTHORIZED_CODE_STRING + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_UNAUTHORIZED + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.UNAUTHORIZED_INVALID_PARTNER_TOKEN + "\"\n" +
            "}";

    /**
     * Example response for a forbidden request (403 Forbidden).
     * This response indicates that the partner code is not authorized to perform the requested action.
     */
    public static final String IMPORT_BANK_STATEMENT_FORBIDDEN = "{\n" +
            "    \"code\": " + ApiResponseConstants.FORBIDDEN_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_FORBIDDEN + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.FORBIDDEN_PARTNER_TOKEN + "\"\n" +
            "}";

    /**
     * Example response for a missing FMIS base URL (404 Not Found).
     * This response occurs when the FMIS base URL cannot be found, meaning the target endpoint is unavailable.
     */
    public static final String IMPORT_BANK_STATEMENT_NOT_FOUND = "{\n" +
            "    \"code\": " + ApiResponseConstants.NOT_FOUND_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_NOT_FOUND + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.NOT_FOUND_FMIS_CONFIG + "\"\n" +
            "}";

    /**
     * Example response for a failed FMIS request (502 Bad Gateway).
     * This response indicates that an error occurred while attempting to communicate with the FMIS system.
     */
    public static final String IMPORT_BANK_STATEMENT_FMIS_FAILURE = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_GATEWAY_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_GATEWAY_NOT_CONNECT + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT + "\"\n" +
            "}";

    /**
     * Example response for a general server error (500 Internal Server Error).
     * This response indicates that a general server error occurred while processing the bank statement import.
     */
    public static final String IMPORT_BANK_STATEMENT_SERVER_ERROR = "{\n" +
            "    \"code\": " + ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_INTERNAL_ERROR + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.INTERNAL_SERVER_ERROR + "\"\n" +
            "}";

    /**
     * Example response for a missing or empty file error (400 Bad Request).
     * This response indicates that the uploaded file is either missing or empty.
     */
    public static final String UPLOAD_BANK_STATEMENT_FILE_MISSING_OR_EMPTY_ERROR = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_INVALID_ENTITY + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_FILE_MISSING_OR_EMPTY + "\"\n" +
            "}";

    /**
     * Example response for an invalid file type error (400 Bad Request).
     * This response indicates that the uploaded file type is invalid and only JSON files are allowed.
     */
    public static final String UPLOAD_BANK_STATEMENT_INVALID_FILE_TYPE_ERROR = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_INVALID_FIELD + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_INVALID_FILE_TYPE + "\"\n" +
            "}";

    /**
     * Example response for a failed JSON parsing error (400 Bad Request).
     * This response indicates that there was an issue parsing the uploaded JSON file, likely due to malformed JSON.
     */
    public static final String UPLOAD_BANK_STATEMENT_FAILED_TO_PARSE_JSON_ERROR = "{\n" +
            "    \"code\": " + ApiResponseConstants.BAD_REQUEST_CODE + ",\n" +
            "    \"response_code\": " + FmisResponseCodes.FMIS_BAD_REQUEST_FAILED_PROCESS + ",\n" +
            "    \"message\": \"" + ApiResponseConstants.BAD_REQUEST_FAILED_PROCESS_FILE + "\"\n" +
            "}";
}
