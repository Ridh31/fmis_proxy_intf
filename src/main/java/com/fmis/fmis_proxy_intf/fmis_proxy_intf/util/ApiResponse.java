package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String code;
    private String message;
    private T error;  // Renamed 'data' to 'error' to match the desired response structure.

    /**
     * Default constructor for successful responses.
     * Initializes code to "200" and message to "Success".
     */
    public ApiResponse() {
        this.code = "200";
        this.message = "Success";
    }

    /**
     * Constructor for responses with custom code and message.
     *
     * @param code    Response status code
     * @param message Human-readable response message
     */
    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor for successful responses with error payload.
     *
     * @param error Error payload (field-specific validation errors)
     */
    public ApiResponse(T error) {
        this();
        this.error = error;
    }

    /**
     * Full constructor for custom responses with error payload.
     *
     * @param code    Response status code
     * @param message Human-readable response message
     * @param error   Error payload (field-specific validation errors)
     */
    public ApiResponse(String code, String message, T error) {
        this.code = code;
        this.message = message;
        this.error = error;
    }

    /**
     * Constructor for responses with a Map of field-specific error messages.
     *
     * @param code            Response status code
     * @param validationErrors Map of field-specific validation error messages
     */
    public ApiResponse(String code, Map<String, String> validationErrors) {
        this.code = code;
        this.message = "Validation failed";  // default message for validation errors
        this.error = (T) validationErrors;  // Cast Map<String, String> to T
    }

    /* Static Factory Methods */

    /**
     * Creates a successful response with error payload.
     *
     * @param error Error payload
     * @return ApiResponse instance with success status
     * @param <T> Type of the error payload
     */
    public static <T> ApiResponse<T> success(T error) {
        return new ApiResponse<>(error);
    }

    /**
     * Creates an error response with custom code and message.
     *
     * @param code    Error status code
     * @param message Error description
     * @return ApiResponse instance with error details
     */
    public static ApiResponse<?> error(String code, String message) {
        return new ApiResponse<>(code, message);
    }

    /**
     * Creates an error response with custom code, message, and optional data.
     *
     * @param code    Error status code
     * @param message Error description
     * @param error   Additional error data (validation errors)
     * @return ApiResponse instance with error details
     * @param <T> Type of the error data
     */
    public static <T> ApiResponse<T> error(String code, String message, T error) {
        return new ApiResponse<>(code, message, error);
    }

    // Manually defined getters and setters for error field

    public T getError() {
        return error;
    }

    public void setError(T error) {
        this.error = error;
    }

    // Getters and Setters for other fields
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}