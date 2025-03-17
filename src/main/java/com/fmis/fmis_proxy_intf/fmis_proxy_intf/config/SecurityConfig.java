package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpStatus;

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
                // Disable CSRF (enable in production if needed)
                .csrf(csrf -> csrf.disable())

                // Configure HTTP request authorization
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/api/v1/test/**",
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/open-api",
                                "/api/v1/docs",
                                "/api/v1/swagger-ui",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/redoc.html",
                                "/js/**",
                                "/img/**"
                        ).permitAll() // Allow these endpoints without authentication
                        .anyRequest().authenticated() // Protect all other endpoints
                )

                // Disable default login form
                .formLogin(login -> login.disable())

                // Enable HTTP Basic Authentication
                .httpBasic(Customizer.withDefaults())

                // Enforce stateless sessions (no session persistence)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Handle authentication errors
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((
                                request,
                                response,
                                authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            String jsonResponse = "{\"code\":\"" + HttpStatus.UNAUTHORIZED.value() +
                                    "\", \"message\":\"Unauthorized access. Please provide valid credentials.\"}";
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
}
