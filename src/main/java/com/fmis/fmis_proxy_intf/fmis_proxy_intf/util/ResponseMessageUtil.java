package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;

/**
 * Utility for generating standardized API response messages.
 * Covers entity operations, validation/errors, authorization, and external/server issues.
 */
public class ResponseMessageUtil {

    /**
     * Returns a formatted message indicating that an entity has been processed successfully.
     *
     * @param entity The name of the entity that was processed.
     * @return Formatted success message for creation.
     */
    public static String processed(String entity) {
        return String.format(ApiResponseConstants.ENTITY_PROCESSED_SUCCESS, entity);
    }

    /**
     * Returns a formatted message indicating that an entity has been created successfully.
     *
     * @param entity The name of the entity that was created.
     * @return Formatted success message for creation.
     */
    public static String created(String entity) {
        return String.format(ApiResponseConstants.ENTITY_CREATED_SUCCESS, entity);
    }

    /**
     * Returns a formatted message indicating that an entity has been saved successfully.
     *
     * @param entity The name of the entity that was saved.
     * @return Formatted success message for saving.
     */
    public static String saved(String entity) {
        return String.format(ApiResponseConstants.ENTITY_SAVED_SUCCESS, entity);
    }

    /**
     * Returns a formatted message indicating that an entity has been imported successfully.
     *
     * @param entity The name of the entity that was imported.
     * @return Formatted success message for importing.
     */
    public static String imported(String entity) {
        return String.format(ApiResponseConstants.ENTITY_IMPORTED_SUCCESS, entity);
    }

    /**
     * Returns a formatted message indicating that an entity has been fetched successfully.
     *
     * @param entity The name of the entity that was fetched.
     * @return Formatted success message for fetching.
     */
    public static String fetched(String entity) {
        return String.format(ApiResponseConstants.ENTITY_FETCHED_SUCCESS, entity);
    }

    /**
     * Returns a formatted message indicating that an entity has been updated successfully.
     *
     * @param entity The name of the entity that was updated.
     * @return Formatted success message for updating.
     */
    public static String updated(String entity) {
        return String.format(ApiResponseConstants.ENTITY_UPDATED_SUCCESS, entity);
    }

    /**
     * Returns a formatted message indicating that an entity has been deleted successfully.
     *
     * @param entity The name of the entity that was deleted.
     * @return Formatted success message for deletion.
     */
    public static String deleted(String entity) {
        return String.format(ApiResponseConstants.ENTITY_DELETED_SUCCESS, entity);
    }

    /**
     * Returns a formatted message indicating that an entity has been reset successfully.
     *
     * @param entity The name of the entity that was reset.
     * @return Formatted success message for reset.
     */
    public static String reset(String entity) {
        return String.format(ApiResponseConstants.ENTITY_RESET_SUCCESS, entity);
    }

    /**
     * Returns a formatted message indicating that an entity was not found.
     *
     * @param entity The name of the entity that was not found.
     * @return Formatted error message for entity not found.
     */
    public static String notFound(String entity) {
        return String.format(ApiResponseConstants.NOT_FOUND_ENTITY, entity);
    }

    /**
     * Returns a formatted message indicating that a resource was not found on an external server.
     *
     * @param resource The name of the external resource.
     * @return Formatted error message for external 404.
     */
    public static String externalResourceNotFound(String resource) {
        return String.format(ApiResponseConstants.NOT_FOUND_EXTERNAL_RESOURCE, resource);
    }

    /**
     * Returns a formatted message indicating that no content was received from a specific response.
     *
     * @param source The source (e.g., FMIS, external API).
     * @return Formatted no-content message.
     */
    public static String noContent(String source) {
        return String.format(ApiResponseConstants.NO_CONTENT_ERROR, source);
    }

    /**
     * Returns a formatted message indicating that an entity is invalid or missing.
     *
     * @param entity The name of the entity that is invalid or missing.
     * @return Formatted error message for invalid entity.
     */
    public static String invalid(String entity) {
        return String.format(ApiResponseConstants.BAD_REQUEST_INVALID_ENTITY, entity);
    }

    /**
     * Returns a formatted message indicating that a field is invalid with a specific condition.
     *
     * @param field The name of the field.
     * @param condition The condition or requirement for the field.
     * @return Formatted error message for invalid field.
     */
    public static String invalidField(String field, String condition) {
        return String.format(ApiResponseConstants.BAD_REQUEST_INVALID_FIELD_CONDITION, field, condition);
    }

    /**
     * Returns a message indicating that a required HTTP header is missing or empty.
     *
     * @param header The name of the required header.
     * @return Formatted message for missing required header.
     */
    public static String requiredHeader(String header) {
        return String.format(ApiResponseConstants.BAD_REQUEST_ERROR_HEADER_REQUIRED, header);
    }

    /**
     * Returns a message indicating that a required field is missing or empty.
     *
     * @param field The name of the required field.
     * @return Formatted message for missing required field.
     */
    public static String requiredField(String field) {
        return String.format(ApiResponseConstants.BAD_REQUEST_ERROR_FIELD_REQUIRED, field);
    }

    /**
     * Returns a formatted message indicating a validation failure.
     *
     * @param field The field that failed validation.
     * @return Formatted validation failure message.
     */
    public static String validationFailed(String field) {
        return String.format(ApiResponseConstants.BAD_REQUEST_VALIDATION_FAILED, field);
    }

    /**
     * Returns a message indicating that JWT validation has failed.
     *
     * @return Formatted error message for JWT validation failure.
     */
    public static String jwtValidationFailed() {
        return ApiResponseConstants.BAD_REQUEST_ERROR_JWT_VALIDATION_FAILED;
    }

    /**
     * Returns a message indicating that the access token is invalid or expired.
     *
     * @return Formatted error message for invalid/expired access token.
     */
    public static String accessTokenInvalidOrExpired() {
        return ApiResponseConstants.UNAUTHORIZED_ACCESS_TOKEN_INVALID_OR_EXPIRED;
    }

    /**
     * Returns a formatted message indicating that an entity is already taken.
     *
     * @param entity The name of the entity that is taken.
     * @return Formatted error message for entity taken.
     */
    public static String taken(String entity) {
        return String.format(ApiResponseConstants.BAD_REQUEST_ENTITY_TAKEN, entity);
    }

    /**
     * Returns a formatted message indicating that a request failed to process.
     *
     * @param entity The name of the entity or resource that failed to process.
     * @return Formatted error message for failed processing.
     */
    public static String failedProcess(String entity) {
        return String.format(ApiResponseConstants.BAD_REQUEST_FAILED_PROCESS, entity);
    }

    /**
     * Returns a formatted message indicating that no configuration was found for the given input.
     *
     * @param config The key or identifier used to search for the configuration.
     * @return Formatted error message specifying the missing configuration.
     */
    public static String configurationNotFound(String config) {
        return String.format(ApiResponseConstants.BAD_REQUEST_CONFIGURATION_NOT_FOUND, config);
    }

    /**
     * Returns a formatted message indicating that the user is unauthorized to perform an action.
     *
     * @param entity The name of the entity or action.
     * @return Formatted unauthorized message.
     */
    public static String unauthorized(String entity) {
        return String.format(ApiResponseConstants.UNAUTHORIZED, entity);
    }

    /**
     * Returns a message indicating that the user is unauthorized to perform an action.
     *
     * @return Formatted unauthorized access message.
     */
    public static String unauthorizedAccess() {
        return ApiResponseConstants.UNAUTHORIZED_ACCESS;
    }

    /**
     * Returns a formatted message indicating that the user is forbidden from performing an action.
     *
     * @param action The name of the action the user is forbidden to perform.
     * @return Formatted forbidden message.
     */
    public static String forbidden(String action) {
        return String.format(ApiResponseConstants.FORBIDDEN, action);
    }

    /**
     * Returns a formatted message indicating a conflict with existing data.
     *
     * @param entity The name of the entity causing conflict.
     * @return Formatted conflict message.
     */
    public static String conflict(String entity) {
        return String.format(ApiResponseConstants.CONFLICT_ENTITY, entity);
    }

    /**
     * Returns a formatted message indicating that the provided content type is not supported.
     *
     * @param contentType The unsupported content type.
     * @return Formatted unsupported content type message.
     */
    public static String unsupportedMediaType(String contentType) {
        return String.format(ApiResponseConstants.UNSUPPORTED_MEDIA_TYPE, contentType);
    }

    /**
     * Returns a formatted message indicating that an error occurred while fetching an entity.
     *
     * @param entity The name of the entity that failed to fetch.
     * @return Formatted error message for fetch failure.
     */
    public static String fetchError(String entity) {
        return String.format(ApiResponseConstants.INTERNAL_SERVER_ERROR_FETCHING_ENTITY, entity);
    }

    /**
     * Returns a formatted message indicating that an internal error occurred while processing an entity.
     *
     * @param entity The name of the entity being processed.
     * @return Formatted error message for internal server error.
     */
    public static String internalError(String entity) {
        return String.format(ApiResponseConstants.INTERNAL_SERVER_ERROR_OCCURRED, entity);
    }

    /**
     * Returns a formatted message indicating that the request sent to an external server was invalid.
     *
     * @param entity The name of the external entity or resource.
     * @return Formatted external error message.
     */
    public static String externalError(String entity) {
        return String.format(ApiResponseConstants.BAD_REQUEST_EXTERNAL, entity);
    }

    /**
     * Returns a formatted message indicating that an error occurred while communicating with an external server.
     *
     * @param entity The name of the external entity or resource involved in the request.
     * @return Formatted error message for external client communication failure.
     */
    public static String externalClientError(String entity) {
        return String.format(ApiResponseConstants.EXTERNAL_CLIENT_ERROR, entity);
    }

    /**
     * Returns a formatted message indicating that the server could not connect to the target host.
     *
     * @param targetHost The host that could not be reached.
     * @return Formatted error message for bad gateway connectivity.
     */
    public static String badGatewayNotConnect(String targetHost) {
        return String.format(ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT, targetHost);
    }

    /**
     * Returns a message indicating that the upstream service returned an invalid response.
     *
     * @return Formatted message for bad gateway/upstream service error.
     */
    public static String upstreamServiceError() {
        return ApiResponseConstants.BAD_GATEWAY_UPSTREAM_SERVICE_ERROR_MESSAGE;
    }

    /**
     * Returns a message indicating that the service is temporarily unavailable.
     *
     * @return Formatted message for 503 Service Unavailable.
     */
    public static String serviceUnavailable() {
        return ApiResponseConstants.SERVICE_UNAVAILABLE;
    }
}