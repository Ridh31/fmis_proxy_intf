package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.ResponseCodeDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.FmisResponseCodes;
import org.springframework.http.HttpStatus;

/**
 * Utility class to map common API responses to HTTP codes and FMIS-specific response codes.
 * Provides a single source of truth for response codes, improving consistency across the API.
 */
public class ResponseCodeUtil {

    private ResponseCodeUtil() {
        // Prevent instantiation
    }

    /**
     * Response when an entity has been processed successfully.
     *
     * @return ResponseCodeDTO with HTTP 200 and FMIS-1000
     */
    public static ResponseCodeDTO processed() {
        return new ResponseCodeDTO(HttpStatus.OK.value(), FmisResponseCodes.FMIS_SUCCESS_PROCESSED);
    }

    /**
     * Response when an entity has been created successfully.
     *
     * @return ResponseCodeDTO with HTTP 201 and FMIS-1001
     */
    public static ResponseCodeDTO created() {
        return new ResponseCodeDTO(HttpStatus.CREATED.value(), FmisResponseCodes.FMIS_SUCCESS_CREATED);
    }

    /**
     * Response when an entity has been saved successfully.
     *
     * @return ResponseCodeDTO with HTTP 201 and FMIS-1002
     */
    public static ResponseCodeDTO saved() {
        return new ResponseCodeDTO(HttpStatus.CREATED.value(), FmisResponseCodes.FMIS_SUCCESS_SAVED);
    }

    /**
     * Response when an entity has been imported successfully.
     *
     * @return ResponseCodeDTO with HTTP 201 and FMIS-1003
     */
    public static ResponseCodeDTO imported() {
        return new ResponseCodeDTO(HttpStatus.CREATED.value(), FmisResponseCodes.FMIS_SUCCESS_IMPORTED);
    }

    /**
     * Response when an entity has been fetched successfully.
     *
     * @return ResponseCodeDTO with HTTP 200 and FMIS-1004
     */
    public static ResponseCodeDTO fetched() {
        return new ResponseCodeDTO(HttpStatus.OK.value(), FmisResponseCodes.FMIS_SUCCESS_FETCHED);
    }

    /**
     * Response when an entity has been updated successfully.
     *
     * @return ResponseCodeDTO with HTTP 200 and FMIS-1005
     */
    public static ResponseCodeDTO updated() {
        return new ResponseCodeDTO(HttpStatus.OK.value(), FmisResponseCodes.FMIS_SUCCESS_UPDATED);
    }

    /**
     * Response when an entity has been deleted successfully.
     *
     * @return ResponseCodeDTO with HTTP 200 and FMIS-1006
     */
    public static ResponseCodeDTO deleted() {
        return new ResponseCodeDTO(HttpStatus.OK.value(), FmisResponseCodes.FMIS_SUCCESS_DELETED);
    }

    /**
     * Response when an entity has been reset successfully.
     *
     * @return ResponseCodeDTO with HTTP 200 and FMIS-1007
     */
    public static ResponseCodeDTO reset() {
        return new ResponseCodeDTO(HttpStatus.OK.value(), FmisResponseCodes.FMIS_SUCCESS_RESET);
    }

    /**
     * Response when a resource is not found.
     *
     * @return ResponseCodeDTO with HTTP 404 and FMIS-4040
     */
    public static ResponseCodeDTO notFound() {
        return new ResponseCodeDTO(HttpStatus.NOT_FOUND.value(), FmisResponseCodes.FMIS_NOT_FOUND);
    }

    /**
     * Response when an external resource is not found.
     *
     * @return ResponseCodeDTO with HTTP 404 and FMIS-4041
     */
    public static ResponseCodeDTO externalResourceNotFound() {
        return new ResponseCodeDTO(HttpStatus.NOT_FOUND.value(), FmisResponseCodes.FMIS_NOT_FOUND_EXTERNAL_RESOURCE);
    }

    /**
     * Response when there is no content to return.
     *
     * @return ResponseCodeDTO with HTTP 204 and FMIS-4042
     */
    public static ResponseCodeDTO noContent() {
        return new ResponseCodeDTO(HttpStatus.NO_CONTENT.value(), FmisResponseCodes.FMIS_NO_CONTENT);
    }

    /**
     * Response when an invalid entity is provided.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2001
     */
    public static ResponseCodeDTO invalid() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_INVALID_ENTITY);
    }

    /**
     * Response when a field value is invalid.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2002
     */
    public static ResponseCodeDTO invalidField() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_INVALID_FIELD);
    }

    /**
     * Response when a required header is missing.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2003
     */
    public static ResponseCodeDTO requiredHeader() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_REQUIRED_HEADER);
    }

    /**
     * Response when a required field is missing.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2004
     */
    public static ResponseCodeDTO requiredField() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_REQUIRED_FIELD);
    }

    /**
     * Response when validation fails.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2005
     */
    public static ResponseCodeDTO validationFailed() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_VALIDATION_FAILED);
    }

    /**
     * Response when JWT validation fails.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2006
     */
    public static ResponseCodeDTO jwtValidationFailed() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_JWT_VALIDATION_FAILED);
    }

    /**
     * Response when access token is invalid or expired.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2007
     */
    public static ResponseCodeDTO accessTokenInvalidOrExpired() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_ACCESS_TOKEN_INVALID_OR_EXPIRED);
    }

    /**
     * Response when a resource already exists.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2010
     */
    public static ResponseCodeDTO taken() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_TAKEN);
    }

    /**
     * Response when processing a resource fails.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2008
     */
    public static ResponseCodeDTO failedProcess() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_FAILED_PROCESS);
    }

    /**
     * Response when configuration is not found.
     *
     * @return ResponseCodeDTO with HTTP 400 and FMIS-2009
     */
    public static ResponseCodeDTO configurationNotFound() {
        return new ResponseCodeDTO(HttpStatus.BAD_REQUEST.value(), FmisResponseCodes.FMIS_BAD_REQUEST_CONFIGURATION_NOT_FOUND);
    }

    /**
     * Response when the user is unauthorized.
     *
     * @return ResponseCodeDTO with HTTP 401 and FMIS-3000
     */
    public static ResponseCodeDTO unauthorized() {
        return new ResponseCodeDTO(HttpStatus.UNAUTHORIZED.value(), FmisResponseCodes.FMIS_UNAUTHORIZED);
    }

    /**
     * Response when unauthorized access is attempted.
     *
     * @return ResponseCodeDTO with HTTP 401 and FMIS-3001
     */
    public static ResponseCodeDTO unauthorizedAccess() {
        return new ResponseCodeDTO(HttpStatus.UNAUTHORIZED.value(), FmisResponseCodes.FMIS_UNAUTHORIZED_ACCESS);
    }

    /**
     * Response when access is forbidden.
     *
     * @return ResponseCodeDTO with HTTP 403 and FMIS-3002
     */
    public static ResponseCodeDTO forbidden() {
        return new ResponseCodeDTO(HttpStatus.FORBIDDEN.value(), FmisResponseCodes.FMIS_FORBIDDEN);
    }

    /**
     * Response for conflict errors.
     *
     * @return ResponseCodeDTO with HTTP 409 and FMIS-5000
     */
    public static ResponseCodeDTO conflict() {
        return new ResponseCodeDTO(HttpStatus.CONFLICT.value(), FmisResponseCodes.FMIS_CONFLICT_ENTITY);
    }

    /**
     * Response when unsupported media type is provided.
     *
     * @return ResponseCodeDTO with HTTP 415 and FMIS-4043
     */
    public static ResponseCodeDTO unsupportedMediaType() {
        return new ResponseCodeDTO(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), FmisResponseCodes.FMIS_UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Response when fetching a resource fails.
     *
     * @return ResponseCodeDTO with HTTP 500 and FMIS-5002
     */
    public static ResponseCodeDTO fetchError() {
        return new ResponseCodeDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), FmisResponseCodes.FMIS_FETCH_ERROR);
    }

    /**
     * Response when an internal error occurs.
     *
     * @return ResponseCodeDTO with HTTP 500 and FMIS-5001
     */
    public static ResponseCodeDTO internalError() {
        return new ResponseCodeDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), FmisResponseCodes.FMIS_INTERNAL_ERROR);
    }

    /**
     * Response when an external error occurs.
     *
     * @return ResponseCodeDTO with HTTP 500 and FMIS-5003
     */
    public static ResponseCodeDTO externalError() {
        return new ResponseCodeDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), FmisResponseCodes.FMIS_EXTERNAL_ERROR);
    }

    /**
     * Response when an external client error occurs.
     *
     * @return ResponseCodeDTO with HTTP 502 and FMIS-5004
     */
    public static ResponseCodeDTO externalClientError() {
        return new ResponseCodeDTO(HttpStatus.BAD_GATEWAY.value(), FmisResponseCodes.FMIS_EXTERNAL_CLIENT_ERROR);
    }

    /**
     * Response when a bad gateway cannot connect.
     *
     * @return ResponseCodeDTO with HTTP 502 and FMIS-5005
     */
    public static ResponseCodeDTO badGatewayNotConnect() {
        return new ResponseCodeDTO(HttpStatus.BAD_GATEWAY.value(), FmisResponseCodes.FMIS_BAD_GATEWAY_NOT_CONNECT);
    }

    /**
     * Response when an upstream service error occurs.
     *
     * @return ResponseCodeDTO with HTTP 502 and FMIS-5006
     */
    public static ResponseCodeDTO upstreamServiceError() {
        return new ResponseCodeDTO(HttpStatus.BAD_GATEWAY.value(), FmisResponseCodes.FMIS_UPSTREAM_SERVICE_ERROR);
    }

    /**
     * Response when the service is unavailable.
     *
     * @return ResponseCodeDTO with HTTP 503 and FMIS-5007
     */
    public static ResponseCodeDTO serviceUnavailable() {
        return new ResponseCodeDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), FmisResponseCodes.FMIS_SERVICE_UNAVAILABLE);
    }
}