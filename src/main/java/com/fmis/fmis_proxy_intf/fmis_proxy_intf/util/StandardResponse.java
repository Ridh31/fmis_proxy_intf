package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StandardResponse {
        private String code;
        private String message;
        private Object data;

        // Static factory method for success responses
        public static StandardResponse success(String message, Object data) {
                return new StandardResponse("200", message, data);
        }

        // Static factory method for error responses
        public static StandardResponse error(String message, Object data) {
                return new StandardResponse("500", message, data); // You can use "500" or any other error code here.
        }

        // Static factory method for not found responses
        public static StandardResponse notFound(String message) {
                return new StandardResponse("404", message, null);
        }

        // Static factory method for created responses (e.g., for POST requests)
        public static StandardResponse created(String message, Object data) {
                return new StandardResponse("201", message, data);
        }

        // Static factory method for no content responses (e.g., for DELETE requests)
        public static StandardResponse noContent() {
                return new StandardResponse("204", "No content", null);
        }
}
