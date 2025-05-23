package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

    /**
     * Sets a secure, HTTP-only cookie with the specified name, value, and max age.
     *
     * @param response the HttpServletResponse to which the cookie will be added
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     * @param maxAge   the maximum age of the cookie in seconds
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}