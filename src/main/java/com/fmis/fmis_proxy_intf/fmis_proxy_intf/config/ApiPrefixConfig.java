package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class that applies a global API path prefix to all REST controllers.
 *
 * The prefix is loaded from application properties (e.g., application.api.prefix=/api/v1)
 * and automatically prepended to all @RequestMapping paths.
 *
 * This avoids hardcoding the API version in each controller and ensures consistent endpoints.
 */
@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    /**
     * Base API path prefix injected from application.properties.
     * Example: application.api.prefix=/api/...
     */
    @Value("${application.api.prefix}")
    private String apiPrefix;

    /**
     * Automatically prepends the configured API prefix
     * to all controller request mappings for consistent routing.
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(apiPrefix, c -> true);
    }
}