package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for a dedicated RestTemplate for CamDigiKey requests.
 * Separate from other RestTemplates to allow custom timeouts and avoid conflicts.
 */
@Configuration
public class CamDigiKeyRestTemplateConfig {

    /**
     * Creates a RestTemplate with specific timeouts for CamDigiKey.
     * @return configured RestTemplate
     */
    @Bean(name = "camDigiKeyRestTemplate")
    public RestTemplate camDigiKeyRestTemplate() {

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        // Set connection and read timeouts (ms)
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(10_000);
        requestFactory.setConnectionRequestTimeout(5_000);

        return new RestTemplate(requestFactory);
    }
}