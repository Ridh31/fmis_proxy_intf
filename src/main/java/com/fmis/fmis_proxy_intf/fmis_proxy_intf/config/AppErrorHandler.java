package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseCodeUtil;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseMessageUtil;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.ResponseCodeDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Global error handler that provides standardized JSON responses for application errors.
 * Supports FMIS response codes and custom messages where needed.
 */
@Hidden
@RestController
public class AppErrorHandler implements ErrorController {

    private static final String STATUS_CODE_ATTRIBUTE = "jakarta.servlet.error.status_code";

    /**
     * Handles all application errors and returns a structured JSON response.
     *
     * @param request the HTTP request containing error details
     * @return ResponseEntity with HTTP status, FMIS response code, and message
     */
    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(STATUS_CODE_ATTRIBUTE);
        int statusCode = (status instanceof Integer) ? (Integer) status : HttpStatus.INTERNAL_SERVER_ERROR.value();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);

        ResponseCodeDTO responseCode;
        String message;

        switch (statusCode) {
            case 401: // Unauthorized
                Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
                if (throwable instanceof BadCredentialsException) {
                    responseCode = ResponseCodeUtil.unauthorized();
                    message = ApiResponseConstants.UNAUTHORIZED_INVALID_CREDENTIALS;
                } else {
                    responseCode = ResponseCodeUtil.unauthorizedAccess();
                    message = ResponseMessageUtil.unauthorizedAccess();
                }
                break;
            case 404: // Not Found
                responseCode = ResponseCodeUtil.notFound();
                message = ApiResponseConstants.NOT_FOUND_RESOURCE;
                break;
            case 415: // Unsupported Media Type
                responseCode = ResponseCodeUtil.unsupportedMediaType();
                message = ResponseMessageUtil.unsupportedMediaType("Content-Type");
                break;
            default: // Internal Server Error and other unhandled errors
                responseCode = ResponseCodeUtil.internalError();
                message = ApiResponseConstants.INTERNAL_SERVER_ERROR;
                break;
        }

        return ResponseEntity.status(httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(responseCode, message));
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