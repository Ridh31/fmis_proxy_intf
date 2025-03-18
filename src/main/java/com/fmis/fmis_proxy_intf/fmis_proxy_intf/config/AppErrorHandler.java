package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles global application errors and provides standardized JSON responses.
 */
@RestController
public class AppErrorHandler implements ErrorController {

    private static final String STATUS_CODE_ATTRIBUTE = "jakarta.servlet.error.status_code";

    /**
     * Handles application errors and provides a structured error response.
     *
     * @param request the HTTP request containing error details
     * @return a ResponseEntity with the appropriate HTTP status and error message
     */
    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(STATUS_CODE_ATTRIBUTE);

        if (status instanceof Integer statusCode) {
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(
                                statusCode,
                                "Resource not found. The requested URL does not exist."
                        ));
            }
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred."
                ));
    }

    /**
     * A simple DTO for structured error responses.
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
