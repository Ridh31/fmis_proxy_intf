package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SystemStatusController provides endpoints to monitor the application's health and test connectivity
 * to external HTTP or TCP services. It is designed for diagnostics and monitoring purposes.
 */
@RestController
@RequestMapping("/system")
public class SystemStatusController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${application.version}")
    private String appVersion;

    @Value("${application.base-url}")
    private String proxyUrl;

    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;

    /**
     * Constructor for SystemStatusController.
     *
     * @param jdbcTemplate Spring JDBC template for DB connection testing.
     */
    public SystemStatusController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Returns the current health status of the application including DB and proxy connectivity.
     *
     * @return ResponseEntity containing application health details.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("app", appName);
        response.put("version", appVersion);
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("proxyUrl", proxyUrl);

        // Database connection check
        response.put("dbConnection", isDatabaseUp() ? "UP" : "DOWN");

        return ResponseEntity.ok(response);
    }

    /**
     * Tests HTTP connectivity to a specified URL.
     *
     * @param url The HTTP URL to test.
     * @return ResponseEntity with connection status.
     */
    @GetMapping("/test-http")
    public ResponseEntity<Map<String, Object>> testHttpConnection(@RequestParam String url) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("url", url);

        try {
            restTemplate.getForObject(url, String.class);
            response.put("status", "UP");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * Tests TCP connectivity to a specified host and port (like telnet).
     *
     * @param host The target host.
     * @param port The target port.
     * @return ResponseEntity with connection status.
     */
    @GetMapping("/test-tcp")
    public ResponseEntity<Map<String, Object>> testTcpConnection(@RequestParam String host, @RequestParam int port) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("host", host);
        response.put("port", port);

        if (isTcpAvailable(host, port)) {
            response.put("status", "UP");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "DOWN");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * Checks if the database is reachable by executing a simple query.
     *
     * @return true if database is reachable; false otherwise.
     */
    private boolean isDatabaseUp() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks TCP connectivity to a specified host and port.
     *
     * @param host The target host.
     * @param port The target port.
     * @return true if TCP connection succeeds; false otherwise.
     */
    private boolean isTcpAvailable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 3000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}