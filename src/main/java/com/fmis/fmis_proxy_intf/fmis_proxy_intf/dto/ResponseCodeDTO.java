package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

/**
 * DTO to hold both HTTP status code and FMIS-specific response code.
 * Used with ApiResponse to provide standardized API responses including both HTTP and FMIS codes.
 */
public class ResponseCodeDTO {

    private final int httpCode;
    private final String fmisCode;

    /**
     * Constructor
     *
     * @param httpCode HTTP status code
     * @param fmisCode FMIS response code
     */
    public ResponseCodeDTO(int httpCode, String fmisCode) {
        this.httpCode = httpCode;
        this.fmisCode = fmisCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getFmisCode() {
        return fmisCode;
    }
}
