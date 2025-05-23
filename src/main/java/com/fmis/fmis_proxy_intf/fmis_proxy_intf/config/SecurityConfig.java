package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for authentication and authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    public SecurityConfig(@Lazy UserDetailsService userDetailsService) {

    }

    /**
     * Configures the HTTP security filter chain.
     *
     * @param http HttpSecurity object to configure
     * @return SecurityFilterChain
     * @throws Exception if any error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF (enable in production if needed)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure HTTP request authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/reset-password",
                                "/api/v1/create-partner",
                                "/api/v1/list-partner",
                                "/api/v1/import-bank-statement",
                                "/api/v1/list-bank-statement"
                        ).authenticated()
                        .requestMatchers(
                                "/api/v1/open-api/**",
                                "/api/v1/swagger-ui/**",
                                "/api/v1/redoc/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Allow static resources
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**").permitAll()

                        // Allow all other requests
                        .anyRequest().permitAll()
                )

                // Disable default login form
                .formLogin(AbstractHttpConfigurer::disable)

                // Enable HTTP Basic Authentication
                .httpBasic(Customizer.withDefaults())

                // Enforce stateless sessions (no session persistence)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Handle authentication errors
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(HeaderConstants.CONTENT_TYPE_JSON);

                            // Check if the exception is a BadCredentialsException
                            String message = ApiResponseConstants.UNAUTHORIZED_ACCESS;
                            if (authException instanceof BadCredentialsException) {
                                message = ApiResponseConstants.INVALID_CREDENTIALS;
                            }

                            String jsonResponse = String.format(
                                    "{\"code\":\"%d\", \"message\":\"%s\"}",
                                    HttpStatus.UNAUTHORIZED.value(),
                                    message
                            );
                            response.getWriter().write(jsonResponse);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(HeaderConstants.CONTENT_TYPE_JSON);
                            String jsonResponse = String.format(
                                    "{\"code\":\"%d\", \"message\":\"%s\"}",
                                    HttpStatus.FORBIDDEN.value(),
                                    ApiResponseConstants.FORBIDDEN
                            );
                            response.getWriter().write(jsonResponse);
                        })
                );

        return http.build();
    }

    /**
     * Bean for password encoding using BCrypt.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean for retrieving the AuthenticationManager from the authentication configuration.
     *
     * @param authenticationConfiguration AuthenticationConfiguration object
     * @return AuthenticationManager
     * @throws Exception if any error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures CORS settings to allow cross-origin requests.
     *
     * @return CorsConfigurationSource instance
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "https://dev-fmis-intf.fmis.gov.kh",
                "http://10.10.3.52"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}