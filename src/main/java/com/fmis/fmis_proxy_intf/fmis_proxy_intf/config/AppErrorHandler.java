package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
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
 */
@Hidden
@RestController
public class AppErrorHandler implements ErrorController {

    private static final String STATUS_CODE_ATTRIBUTE = "jakarta.servlet.error.status_code";

    /**
     * Handles application errors and returns a structured JSON response.
     *
     * @param request the HTTP request containing error details
     * @return a ResponseEntity with the appropriate HTTP status and error message
     */
    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(STATUS_CODE_ATTRIBUTE);

        int statusCode = (status instanceof Integer) ? (Integer) status : HttpStatus.INTERNAL_SERVER_ERROR.value();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);

        String message = ApiResponseConstants.INTERNAL_SERVER_ERROR;

        // Handle 401 Unauthorized
        if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
            Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
            if (throwable instanceof BadCredentialsException) {
                message = ApiResponseConstants.INVALID_CREDENTIALS;
            } else {
                message = ApiResponseConstants.UNAUTHORIZED_ACCESS;
            }
        }

        // Handle 404 Not Found
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            message = ApiResponseConstants.RESOURCE_NOT_FOUND;
        }

        return ResponseEntity.status(httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(statusCode, message));
    }

    /**
     * DTO for structured error responses.
     */
    public static class ErrorResponse {
        private final int code;
        private final String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}