package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 *
 * This configuration defines the metadata for the FMIS Proxy Interface API documentation,
 * including the title, version, description, and contact information.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "FMIS - Proxy Interface API",
                version = "1.0",
                description = "FMIS Proxy Interface API enables seamless communication between external systems and the FMIS platform. " +
                        "It provides various endpoints for testing connectivity, managing test entities, and interacting with FMIS data " +
                        "in a secure and standardized way. This API acts as an intermediary layer to simplify interactions with FMIS, " +
                        "ensuring efficient integration, data exchange, and system scalability.",
                contact = @Contact(
                        name = "FMIS Cambodia",
                        email = "fmis.info@mef.gov.kh",
                        url = "https://fmis.gov.kh"
                )
        )
)
public class OpenApiConfig {
}
