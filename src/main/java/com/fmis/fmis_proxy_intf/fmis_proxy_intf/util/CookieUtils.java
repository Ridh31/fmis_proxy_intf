package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieUtils {

    /**
     * Sets a secure, HTTP-only cookie with the specified name, value, and max age.
     * The value is URL-encoded to ensure safe storage of special characters.
     *
     * @param response the HttpServletResponse to which the cookie will be added
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param maxAge the maximum age of the cookie in seconds
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        try {
            value = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}