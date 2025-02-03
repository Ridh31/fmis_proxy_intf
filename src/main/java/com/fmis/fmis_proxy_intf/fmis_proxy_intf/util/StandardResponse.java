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
}