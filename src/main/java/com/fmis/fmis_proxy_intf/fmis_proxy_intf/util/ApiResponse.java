package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.ResponseCodeDTO;

import java.util.Map;

/**
 * Standardized API response wrapper.
 * Supports both success and error responses, including
 *
 * @param <T> Type of the data payload
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "response_code", "message", "data", "error" })
public class ApiResponse<T> {

    private int code;
    @JsonProperty("response_code")
    private String responseCode;
    private String message;
    private T data;
    private T error;

    /**
     * Default constructor for successful responses.
     * Initializes code to 200 and message to "Success".
     */
    public ApiResponse() {
        this.code = 200;
        this.message = "Success";
    }

    /**
     * Constructor for responses with custom HTTP code and message.
     *
     * @param code    HTTP status code
     * @param message Human-readable response message
     */
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor for successful responses with a data payload.
     *
     * @param data Data payload (e.g., entity, list, or DTO)
     */
    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    /**
     * Constructor for responses with custom HTTP code, message, and data payload.
     *
     * @param code    HTTP status code
     * @param message Human-readable response message
     * @param data    Data payload
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * NEW: Constructor for responses using ResponseCode DTO.
     * Automatically maps HTTP code and FMIS response code.
     *
     * @param responseCode ResponseCode DTO containing HTTP and FMIS codes
     * @param message      Human-readable response message
     * @param data         Data payload (optional)
     */
    public ApiResponse(ResponseCodeDTO responseCode, String message, T data) {
        this.code = responseCode.getHttpCode();
        this.responseCode = responseCode.getFmisCode();
        this.message = message;
        this.data = data;
    }

    /**
     * Constructor for responses using ResponseCode DTO with no data.
     *
     * @param responseCode ResponseCode DTO containing HTTP and FMIS codes
     * @param message      Human-readable response message
     */
    public ApiResponse(ResponseCodeDTO responseCode, String message) {
        this.code = responseCode.getHttpCode();
        this.responseCode = responseCode.getFmisCode();
        this.message = message;
    }

    /**
     * Constructor for responses using ResponseCode DTO with validation errors.
     *
     * @param responseCode      ResponseCode DTO containing HTTP and FMIS codes
     * @param validationErrors  Map of field-specific error messages
     */
    public ApiResponse(ResponseCodeDTO responseCode, Map<String, String> validationErrors) {
        this.code = responseCode.getHttpCode();
        this.responseCode = responseCode.getFmisCode();
        this.message = "Validation failed";
        this.error = (T) validationErrors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
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