package com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant;

/**
 * This class contains constants for common HTTP header names as well as custom headers
 * used in the application. These constants are used to avoid hardcoding header names
 * throughout the codebase, improving maintainability and reducing the risk of errors.
 */
public final class HeaderConstants {

    /**
     * Common HTTP Header Names
     */
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String USER_AGENT_HEADER = "User-Agent";
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * Custom Headers
     */
    public static final String X_PARTNER_TOKEN = "X-Partner-Token";
    public static final String X_PARTNER_TOKEN_DESC = "The identifier of the partner, which is recognized and authorized by the provider.";
    public static final String X_ROAD_CLIENT = "X-Road-Client";

    // Private constructor to prevent instantiation of this utility class
    private HeaderConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
