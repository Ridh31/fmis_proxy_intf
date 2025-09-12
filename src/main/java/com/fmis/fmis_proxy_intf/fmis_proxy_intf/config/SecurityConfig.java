package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.ResponseCodeDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for authentication and authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${application.api.prefix}")
    private String apiPrefix;

    @Value("${application.cors.allowed-origins}")
    private String[] allowedOrigins;

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
                                apiPrefix + "/auth/reset-password",
                                apiPrefix + "/create-partner",
                                apiPrefix + "/list-partner",
                                apiPrefix + "/import-bank-statement",
                                apiPrefix + "/list-bank-statement",
                                apiPrefix + "/internal/camdigikey/import-host",
                                apiPrefix + "/sarmis/fmis-purchase-orders-callback",
                                apiPrefix + "/security-server/**"
                        ).authenticated()
                        .requestMatchers(
                                apiPrefix + "/open-api/**",
                                apiPrefix + "/swagger-ui/**",
                                apiPrefix + "/redoc/**"
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
                            ResponseCodeDTO responseCode;
                            String message;

                            // Check if the exception is a BadCredentialsException
                            if (authException instanceof BadCredentialsException) {
                                responseCode = ResponseCodeUtil.unauthorized();
                                message = ApiResponseConstants.UNAUTHORIZED_INVALID_CREDENTIALS;
                            } else {
                                responseCode = ResponseCodeUtil.unauthorizedAccess();
                                message = ApiResponseConstants.UNAUTHORIZED_ACCESS;
                            }

                            // Build ApiResponse with response_code
                            ApiResponse<?> apiResponse = new ApiResponse<>(responseCode, message);

                            response.setStatus(responseCode.getHttpCode());
                            response.setContentType(HeaderConstants.CONTENT_TYPE_JSON);

                            // Convert ApiResponse to JSON
                            String jsonResponse = new com.fasterxml.jackson.databind.ObjectMapper()
                                    .writeValueAsString(apiResponse);

                            response.getWriter().write(jsonResponse);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            ResponseCodeDTO responseCode = ResponseCodeUtil.forbidden();
                            ApiResponse<?> apiResponse = new ApiResponse<>(responseCode, ApiResponseConstants.FORBIDDEN);

                            response.setStatus(responseCode.getHttpCode());
                            response.setContentType(HeaderConstants.CONTENT_TYPE_JSON);

                            String jsonResponse = new com.fasterxml.jackson.databind.ObjectMapper()
                                    .writeValueAsString(apiResponse);

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
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}