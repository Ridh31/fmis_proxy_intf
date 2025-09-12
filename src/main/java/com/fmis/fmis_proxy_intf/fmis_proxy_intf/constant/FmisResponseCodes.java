package com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant;

/**
 * Defines custom FMIS response codes for standardized API responses.
 *
 * Code ranges:
 * 1xxx - Success
 * 2xxx - Bad Request / Validation
 * 3xxx - Unauthorized / Forbidden
 * 4xxx - Not Found / No Content / Unsupported Media Type
 * 5xxx - Conflict / Server / External Errors
 */
public final class FmisResponseCodes {

    /** 1xxx - Success */
    public static final String FMIS_SUCCESS_PROCESSED = "FMIS-1000";
    public static final String FMIS_SUCCESS_CREATED = "FMIS-1001";
    public static final String FMIS_SUCCESS_SAVED = "FMIS-1002";
    public static final String FMIS_SUCCESS_IMPORTED = "FMIS-1003";
    public static final String FMIS_SUCCESS_FETCHED = "FMIS-1004";
    public static final String FMIS_SUCCESS_UPDATED = "FMIS-1005";
    public static final String FMIS_SUCCESS_DELETED = "FMIS-1006";
    public static final String FMIS_SUCCESS_RESET = "FMIS-1007";

    /** 2xxx - Bad Request / Validation */
    public static final String FMIS_BAD_REQUEST_NOT_FOUND = "FMIS-2000";
    public static final String FMIS_BAD_REQUEST_INVALID_ENTITY = "FMIS-2001";
    public static final String FMIS_BAD_REQUEST_INVALID_FIELD = "FMIS-2002";
    public static final String FMIS_BAD_REQUEST_REQUIRED_HEADER = "FMIS-2003";
    public static final String FMIS_BAD_REQUEST_REQUIRED_FIELD = "FMIS-2004";
    public static final String FMIS_BAD_REQUEST_VALIDATION_FAILED = "FMIS-2005";
    public static final String FMIS_BAD_REQUEST_JWT_VALIDATION_FAILED = "FMIS-2006";
    public static final String FMIS_BAD_REQUEST_ACCESS_TOKEN_INVALID_OR_EXPIRED = "FMIS-2007";
    public static final String FMIS_BAD_REQUEST_FAILED_PROCESS = "FMIS-2008";
    public static final String FMIS_BAD_REQUEST_CONFIGURATION_NOT_FOUND = "FMIS-2009";
    public static final String FMIS_BAD_REQUEST_TAKEN = "FMIS-2010";

    /** 3xxx - Unauthorized / Forbidden */
    public static final String FMIS_UNAUTHORIZED = "FMIS-3000";
    public static final String FMIS_UNAUTHORIZED_ACCESS = "FMIS-3001";
    public static final String FMIS_FORBIDDEN = "FMIS-3002";

    /** 4xxx - Not Found / No Content / Unsupported Media Type */
    public static final String FMIS_NOT_FOUND = "FMIS-4040";
    public static final String FMIS_NOT_FOUND_EXTERNAL_RESOURCE = "FMIS-4041";
    public static final String FMIS_NO_CONTENT = "FMIS-4042";
    public static final String FMIS_UNSUPPORTED_MEDIA_TYPE = "FMIS-4043";

    /** 5xxx - Conflict / Internal / External / Server Errors */
    public static final String FMIS_CONFLICT_ENTITY = "FMIS-5000";
    public static final String FMIS_INTERNAL_ERROR = "FMIS-5001";
    public static final String FMIS_FETCH_ERROR = "FMIS-5002";
    public static final String FMIS_EXTERNAL_ERROR = "FMIS-5003";
    public static final String FMIS_EXTERNAL_CLIENT_ERROR = "FMIS-5004";
    public static final String FMIS_BAD_GATEWAY_NOT_CONNECT = "FMIS-5005";
    public static final String FMIS_UPSTREAM_SERVICE_ERROR = "FMIS-5006";
    public static final String FMIS_SERVICE_UNAVAILABLE = "FMIS-5007";

    private FmisResponseCodes() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }
}