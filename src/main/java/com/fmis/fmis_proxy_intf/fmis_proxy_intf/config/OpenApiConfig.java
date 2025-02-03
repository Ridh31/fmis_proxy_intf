package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
//    @Bean
//    public OpenAPI openAPI(@Value("${application-title}") String title,
//                           @Value("${application-description}") String description,
//                           @Value("${application-version}") String version,
//                           @Value("${application-license}") String license) {
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
//                .components(new Components()
//                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
//                .info(new Info()
//                        .title(title)
//                        .description(description)
//                        .version(version)
//                        .license(new License().name(license)));
//    }
//    private SecurityScheme createAPIKeyScheme() {
//        // Removed 'bearerFormat' to allow generic security testing
//        return new SecurityScheme()
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("bearer"); // Removed bearerFormat="JWT" to make it more generic
//    }
}
