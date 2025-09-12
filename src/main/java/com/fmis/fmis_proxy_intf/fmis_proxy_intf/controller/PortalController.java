package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseMessageUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kh.gov.camdx.camdigikey.client.CamDigiKeyClient;
import kh.gov.camdx.camdigikey.client.exception.InvalidTokenSignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for handling portal-related endpoints,
 * including login token retrieval from the CamDigiKey service.
 */
@Tag(name = "Portal", description = "Portal related APIs")
@Hidden
@RestController
@RequestMapping("/portal")
public class PortalController {

    private final CamDigiKeyClient camDigiKeyClient;

    /**
     * Constructs a PortalController with the given CamDigiKey client.
     *
     * @param camDigiKeyClient the CamDigiKey client to interact with the authentication service
     */
    @Autowired
    public PortalController(CamDigiKeyClient camDigiKeyClient) {
        this.camDigiKeyClient = camDigiKeyClient;
    }

    /**
     * Validates a JWT token using the CamDigiKey service.
     *
     * @param jwt the JWT token to validate (must not be null or empty)
     * @return ResponseEntity containing validation result or error message
     */
    @GetMapping("/camdigikey/validate-jwt")
    public ResponseEntity<Map<String, Object>> validateJwt(@RequestParam(required = false) String jwt) {
        if (jwt == null || jwt.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", ResponseMessageUtil.requiredField("jwt"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            String token = jwt.replace(" ", "+");
            return ResponseEntity.ok(camDigiKeyClient.validateJwt(token));
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to validate JWT: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves an organization access token from CamDigiKey.
     *
     * @return ResponseEntity containing access token or error message
     */
    @Operation(
            summary = "Get organization access token",
            description = "Fetches organization-level access token from CamDigiKey"
    )
    @GetMapping("/camdigikey/organization-token")
    public ResponseEntity<Map<String, Object>> getOrganizationAccessToken() {
        try {
            return ResponseEntity.ok(camDigiKeyClient.getOrganizationAccessToken());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get organization token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves a login token and login URL from CamDigiKey.
     *
     * @return ResponseEntity containing login token or error message
     */
    @Operation(
            summary = "Get login token",
            description = "Fetches login URL and login token (base64) from CamDigiKey"
    )
    @GetMapping("/camdigikey/login-token")
    public ResponseEntity<Map<String, Object>> getLoginToken() {
        try {
            return ResponseEntity.ok(camDigiKeyClient.getLoginToken());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get login token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves a user access token using an authorization code.
     *
     * @param authCode the authorization code (must not be null or empty)
     * @return ResponseEntity containing access token or error message
     */
    @Operation(
            summary = "Get user access token",
            description = "Retrieves a user access token from CamDigiKey using an authorization code"
    )
    @GetMapping("/camdigikey/get-user-access-token")
    public ResponseEntity<Map<String, Object>> getUserAccessToken(@RequestParam(required = false) String authCode) {
        if (authCode == null || authCode.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", ResponseMessageUtil.requiredField("authCode"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            return ResponseEntity.ok(camDigiKeyClient.getUserAccessToken(authCode));
        } catch (UnsupportedEncodingException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Encoding error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (InvalidTokenSignatureException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid token signature: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get user access token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Verifies the given account token using the CamDigiKey service.
     *
     * @param accountToken the token to be verified (must not be null or empty)
     * @return ResponseEntity containing verification result or error message
     */
    @Operation(
            summary = "Verify account token",
            description = "Verifies a given account token using CamDigiKey"
    )
    @GetMapping("/camdigikey/verify-account-token")
    public ResponseEntity<Map<String, Object>> verifyAccountToken(@RequestParam(required = false) String accountToken) {
        if (accountToken == null || accountToken.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", ResponseMessageUtil.requiredField("accountToken"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            return ResponseEntity.ok(camDigiKeyClient.verifyAccountToken(accountToken));
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to verify account token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}