package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration to scan components, JPA repositories, and entities
 * in both local and CamDigiKey client packages.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.fmis.fmis_proxy_intf",
        "kh.gov.camdx.camdigikey"
})
@EnableJpaRepositories(basePackages = {
        "com.fmis.fmis_proxy_intf",
        "kh.gov.camdx.camdigikey"
})
@EntityScan(basePackages = {
        "com.fmis.fmis_proxy_intf",
        "kh.gov.camdx.camdigikey"
})
@EnableConfigurationProperties
public class CamDigiKeyConfig {
}