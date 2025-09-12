package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.ResponseCodeDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResourceNotFoundException;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseCodeUtil;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * Global exception handler to manage application-wide exceptions.
 */
@Hidden
@RestController
public class GlobalExceptionHandler {

    /**
     * Handles resource not found exceptions (404).
     *
     * @param ex the ResourceNotFoundException
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ResponseCodeUtil.notFound(),
                ApiResponseConstants.NOT_FOUND_RESOURCE
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles all unexpected exceptions (500).
     *
     * @param ex the Exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ResponseCodeUtil.internalError(),
                ApiResponseConstants.INTERNAL_SERVER_ERROR
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * DTO for structured error responses.
     * Includes HTTP code, FMIS response code, and message.
     */
    @JsonPropertyOrder({ "code", "response_code", "message" })
    public static class ErrorResponse {
        private final int code;
        @JsonProperty("response_code")
        private final String responseCode;
        private final String message;

        public ErrorResponse(ResponseCodeDTO responseCodeDTO, String message) {
            this.code = responseCodeDTO.getHttpCode();
            this.responseCode = responseCodeDTO.getFmisCode();
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public String getMessage() {
            return message;
        }
    }
}