package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class to create and manage RestTemplate beans for the application.
 * RestTemplate is used to make HTTP requests to external APIs, such as SARMIS or CamDigiKey services.
 */
@Configuration
public class ProxyRestTemplateConfig {

    /**
     * Creates a RestTemplate bean named "proxyRestTemplate".
     * This bean can be injected wherever HTTP calls to external services are needed.
     *
     * @return a new instance of RestTemplate
     */
    @Bean(name = "proxyRestTemplate")
    public RestTemplate proxyRestTemplate() {
        return new RestTemplate();
    }
}