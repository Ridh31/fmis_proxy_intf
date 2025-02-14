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

    /**
     * Constructor for SecurityConfig.
     *
     * @param userDetailsService UserDetailsService object for authentication
     */
    @Autowired
    public SecurityConfig(@Lazy UserDetailsService userDetailsService) {
        // UserDetailsService can be injected and used if needed in future configurations.
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
                .csrf(csrf -> csrf.disable()) // Disable CSRF (enable in production if needed)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login", "/api/test").permitAll() // Public endpoints
                        .anyRequest().authenticated() // Protect all other endpoints
                )
                .formLogin(login -> login.disable()) // Disable default login form
                .httpBasic(Customizer.withDefaults()) // Enable Basic Authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Enforce stateless sessions
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {

                            // Set response status and JSON response when authentication fails (no Basic Auth)
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");

                            // Add status code to the response body
                            String jsonResponse = "{\"code\":" + "\"" +  HttpStatus.UNAUTHORIZED.value() + "\"" +
                                    ", \"message\":\"Unauthorized access. Please provide valid credentials.\"}";
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
