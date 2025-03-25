package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;  // Changed from String to int
    private String message;
    private T data;
    private T error;

    /**
     * Default constructor for successful responses.
     * Initializes code to 200 and message to "Success".
     */
    public ApiResponse() {
        this.code = 200;  // Changed from "200" to 200
        this.message = "Success";
    }

    /**
     * Constructor for responses with custom code and message.
     *
     * @param code    Response status code
     * @param message Human-readable response message
     */
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor for successful responses with data payload.
     *
     * @param data Data payload (could be XML or other formats)
     */
    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    /**
     * Full constructor for custom responses with data payload.
     *
     * @param code    Response status code
     * @param message Human-readable response message
     * @param data    Data payload
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Constructor for responses with a Map of field-specific error messages.
     *
     * @param code            Response status code
     * @param validationErrors Map of field-specific validation error messages
     */
    public ApiResponse(int code, Map<String, String> validationErrors) {
        this.code = code;
        this.message = "Validation failed";
        this.error = (T) validationErrors;
    }

    /* Static Factory Methods */

    /**
     * Creates a successful response with data payload.
     *
     * @param data Data payload
     * @return ApiResponse instance with success status
     * @param <T> Type of the data payload
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    /**
     * Creates an error response with custom code and message.
     *
     * @param code    Error status code
     * @param message Error description
     * @return ApiResponse instance with error details
     */
    public static ApiResponse<?> error(int code, String message) {
        return new ApiResponse<>(code, message);
    }

    /**
     * Creates an error response with custom code, message, and optional data.
     *
     * @param code    Error status code
     * @param message Error description
     * @param data    Additional error data
     * @return ApiResponse instance with error details
     * @param <T> Type of the error data
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    // Manually defined getters and setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getError() {
        return error;
    }

    public void setError(T error) {
        this.error = error;
    }
}