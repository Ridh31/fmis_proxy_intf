package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.ResponseCodeDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.InternalCamDigiKeyService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing internal CamDigiKey operations.
 */
@Tag(name = "Internal CamDigiKey", description = "Endpoints related to managing internal CamDigiKey.")
@Hidden
@RestController
@RequestMapping("/internal/camdigikey")
public class InternalCamDigiKeyController {

    @Value("${application.api.prefix}")
    private String apiPrefix;

    @Autowired
    private RestTemplate restTemplate;
    private final InternalCamDigiKeyService internalCamDigiKeyService;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Constructor for dependency injection.
     *
     * @param internalCamDigiKeyService Service for CamDigiKey operations.
     * @param authorizationHelper       Helper for user authorization and validation.
     */
    public InternalCamDigiKeyController(
            InternalCamDigiKeyService internalCamDigiKeyService,
            AuthorizationHelper authorizationHelper
    ) {
        this.internalCamDigiKeyService = internalCamDigiKeyService;
        this.authorizationHelper = authorizationHelper;
    }

    /**
     * Endpoint to import a new internal CamDigiKey host.
     * Only Super Admins and Admins (level 1 and 2) are allowed.
     *
     * @param internalCamDigiKey The CamDigiKey data to be imported.
     * @param bindingResult      Holds validation result from the request body.
     * @return ApiResponse with success or failure information.
     */
    @PostMapping("/import-host")
    public ResponseEntity<ApiResponse<?>> importHost(
            @Validated @RequestBody InternalCamDigiKey internalCamDigiKey,
            BindingResult bindingResult) {

        // Extract validation errors
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return bad request with the errors
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.validationFailed(),
                            validationErrors
                    ));
        }

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        User currentUser = (User) authorization;

        try {
            // Check for duplicate values in unique fields
            if (internalCamDigiKeyService.existsByName(internalCamDigiKey.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.taken(),
                                ResponseMessageUtil.taken("Name")
                        ));
            }
            if (internalCamDigiKeyService.existsByAppKey(internalCamDigiKey.getAppKey())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.taken(),
                                ResponseMessageUtil.taken("App key")
                        ));
            }
            if (internalCamDigiKeyService.existsByIpAddress(internalCamDigiKey.getIpAddress())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.taken(),
                                ResponseMessageUtil.taken("IP Address")
                        ));
            }
            if (internalCamDigiKeyService.existsByAccessURL(internalCamDigiKey.getAccessURL())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.taken(),
                                ResponseMessageUtil.taken("Access URL")
                        ));
            }

            // Set creator info and save the entry
            internalCamDigiKey.setCreatedBy(currentUser.getId().intValue());
            InternalCamDigiKey saved = internalCamDigiKeyService.createInternalCamDigiKey(internalCamDigiKey);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.created(),
                            ResponseMessageUtil.created("Host"),
                            saved
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Host")
                    ));
        }
    }

    /**
     * Endpoint to retrieve a paginated and filtered list of internal CamDigiKey hosts.
     *
     * @param page        the page number (default is 0)
     * @param size        the number of items per page (default is 10)
     * @param name        optional filter by host name
     * @param appKey      optional filter by application key
     * @param ipAddress   optional filter by IP address
     * @param accessURL   optional filter by access URL
     * @param createdDate optional filter by creation date in "dd-MM-yyyy" format
     * @return ResponseEntity containing a paginated list of hosts or appropriate status message
     */
    @GetMapping("list-host")
    public ResponseEntity<ApiResponse<?>> getFilteredHosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String accessURL,
            @RequestParam(required = false) String createdDate) {

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        try {
            // Fetch filtered and paginated internal CamDigiKey list
            Page<InternalCamDigiKey> hosts = internalCamDigiKeyService.getFilteredInternalCamDigiKeys(
                    page, size, name, appKey, ipAddress, accessURL, createdDate);

            return ResponseEntity.ok(new ApiResponse<>(
                    ResponseCodeUtil.fetched(),
                    ResponseMessageUtil.fetched("Host"),
                    hosts
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Host")
                    ));
        }
    }

    /**
     * Endpoint to update an existing InternalCamDigiKey host.
     *
     * @param id           the ID of the host to update
     * @param updatedData  the updated host data
     * @param bindingResult holds the results of the validation
     * @return ResponseEntity containing the API response
     */
    @PutMapping("/update-host/{id}")
    public ResponseEntity<ApiResponse<?>> updateHost(
            @PathVariable String id,
            @Validated @RequestBody InternalCamDigiKey updatedData,
            BindingResult bindingResult) {

        // Extract validation errors
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return bad request with the errors
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.validationFailed(),
                            validationErrors
                    ));
        }

        // Validate host ID format
        long hostId;
        try {
            hostId = Long.parseLong(id);
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.invalidField(),
                            ResponseMessageUtil.invalidField("ID", "numeric")
                    ));
        }

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        try {
            // Check if the host exists
            Optional<InternalCamDigiKey> optionalExisting = internalCamDigiKeyService.findById(hostId);
            if (optionalExisting.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.notFound(),
                                ResponseMessageUtil.notFound("Host")
                        ));
            }

            InternalCamDigiKey existing = optionalExisting.get();

            // Manual uniqueness check for appKey
            Optional<InternalCamDigiKey> appKeyOwner = internalCamDigiKeyService.findByAppKey(updatedData.getAppKey());
            if (appKeyOwner.isPresent() && !appKeyOwner.get().getId().equals(existing.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.taken(),
                                ResponseMessageUtil.taken("App key")
                        ));
            }

            // Manual uniqueness check for name
            if (!existing.getName().equals(updatedData.getName())
                    && internalCamDigiKeyService.existsByName(updatedData.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.taken(),
                                ResponseMessageUtil.taken("Name")
                        ));
            }

            // Access URL uniqueness
            if (!existing.getAccessURL().equals(updatedData.getAccessURL())
                    && internalCamDigiKeyService.existsByAccessURL(updatedData.getAccessURL())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.taken(),
                                ResponseMessageUtil.taken("Access URL")
                        ));
            }

            // Update the host entity with new values
            existing.setName(updatedData.getName());
            existing.setAppKey(updatedData.getAppKey());
            existing.setIpAddress(updatedData.getIpAddress());
            existing.setAccessURL(updatedData.getAccessURL());
            existing.setStatus(updatedData.getStatus());
            existing.setIsDeleted(updatedData.getIsDeleted());

            // Save updated host
            InternalCamDigiKey saved = internalCamDigiKeyService.createInternalCamDigiKey(existing);

            // Return success response
            return ResponseEntity.ok(new ApiResponse<>(
                    ResponseCodeUtil.updated(),
                    ResponseMessageUtil.updated("Host"),
                    saved
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Host")
                    ));
        }
    }

    /**
     * Retrieves an organization access token from an external service using the appKey.
     * Validates input, looks up configuration, calls external endpoint,
     * and handles errors including missing parameters, config not found,
     * HTTP client/server errors, and connectivity issues.
     *
     * @param appKey the application key identifying the external system
     * @return ResponseEntity with token data or error details
     */
    @GetMapping("/organization-token")
    public ResponseEntity<ApiResponse<?>> getOrganizationAccessToken(@RequestParam(required = false) String appKey) {

        String resource = "Organization token";

        // Validate input
        if (appKey == null || appKey.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.invalid(),
                            ResponseMessageUtil.invalid("appKey")
                    ));
        }

        try {
            // Lookup host configuration by appKey
            Optional<InternalCamDigiKey> host = internalCamDigiKeyService.findByAppKey(appKey);

            if (host.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound(appKey)
                        ));
            }

            // Prepare URL for external service
            String endpoint = apiPrefix + "/portal/camdigikey/organization-token";
            String url = host.get().getAccessURL() + endpoint;

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode data = root.path("data");

                return ResponseEntity.ok(new ApiResponse<>(
                        ResponseCodeUtil.processed(),
                        ResponseMessageUtil.processed(resource),
                        data
                ));

            } catch (HttpClientErrorException e) {
                // Handle 4xx errors
                int rawStatusCode = e.getStatusCode().value();
                HttpStatus status = HttpStatus.resolve(rawStatusCode);
                if (status == null) status = HttpStatus.BAD_REQUEST;

                // Map HTTP status to FMIS ResponseCodeDTO
                ResponseCodeDTO fmisResponseCode = switch (status) {
                    case NOT_FOUND -> ResponseCodeUtil.externalResourceNotFound();
                    case BAD_REQUEST -> ResponseCodeUtil.externalError();
                    default -> ResponseCodeUtil.externalClientError();
                };

                String message = switch (status) {
                    case NOT_FOUND -> ResponseMessageUtil.externalResourceNotFound(resource);
                    case BAD_REQUEST -> ResponseMessageUtil.externalError(resource);
                    default -> ResponseMessageUtil.externalClientError(resource);
                };

                // Return ApiResponse with FMIS response_code
                return ResponseEntity.status(status)
                        .body(new ApiResponse<>(
                                fmisResponseCode,
                                message,
                                rawStatusCode + " - " + status.getReasonPhrase()
                        ));

            } catch (HttpServerErrorException e) {
                // Handle 5xx errors from external server
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.badGatewayNotConnect(),
                                ResponseMessageUtil.badGatewayNotConnect(url),
                                e.getStatusCode() + " - " + e.getStatusText()
                        ));

            } catch (ResourceAccessException e) {
                // Handle unreachable external host
                String targetHost = ExceptionUtils.extractTargetHost(e.getMessage());

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.serviceUnavailable(),
                                ResponseMessageUtil.serviceUnavailable(),
                                ResponseMessageUtil.badGatewayNotConnect(targetHost)
                        ));

            } catch (Exception e) {
                // Handle unexpected external error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.internalError(),
                                ResponseMessageUtil.internalError(resource)
                        ));
            }

        } catch (Exception e) {
            // Handle unexpected internal error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError(resource)
                    ));
        }
    }

    /**
     * Retrieves a login token from an external service using the provided appKey.
     *
     * @param appKey the application key identifying the target system
     * @return ResponseEntity with the login token or appropriate error message
     */
    @GetMapping("/login-token")
    public ResponseEntity<ApiResponse<?>> getLoginToken(@RequestParam(required = false) String appKey) {

        String resource = "Login token";

        // Validate input
        if (appKey == null || appKey.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.invalid(),
                            ResponseMessageUtil.invalid("appKey")
                    ));
        }

        try {
            // Lookup host configuration by appKey
            Optional<InternalCamDigiKey> host = internalCamDigiKeyService.findByAppKey(appKey);

            if (host.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound("appKey")
                        ));
            }

            // Prepare URL for external service
            String endpoint = apiPrefix + "/portal/camdigikey/login-token";
            String url = host.get().getAccessURL() + endpoint;

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode data = root.path("data");

                return ResponseEntity.ok(new ApiResponse<>(
                        ResponseCodeUtil.processed(),
                        ResponseMessageUtil.processed(resource),
                        data
                ));

            } catch (HttpClientErrorException e) {
                // Handle 4xx errors
                int rawStatusCode = e.getStatusCode().value();
                HttpStatus status = HttpStatus.resolve(rawStatusCode);
                if (status == null) status = HttpStatus.BAD_REQUEST;

                // Map HTTP status to FMIS ResponseCodeDTO
                ResponseCodeDTO fmisResponseCode = switch (status) {
                    case NOT_FOUND -> ResponseCodeUtil.externalResourceNotFound();
                    case BAD_REQUEST -> ResponseCodeUtil.externalError();
                    default -> ResponseCodeUtil.externalClientError();
                };

                String message = switch (status) {
                    case NOT_FOUND -> ResponseMessageUtil.externalResourceNotFound(resource);
                    case BAD_REQUEST -> ResponseMessageUtil.externalError(resource);
                    default -> ResponseMessageUtil.externalClientError(resource);
                };

                // Return ApiResponse with FMIS response_code
                return ResponseEntity.status(status)
                        .body(new ApiResponse<>(
                                fmisResponseCode,
                                message,
                                rawStatusCode + " - " + status.getReasonPhrase()
                        ));

            } catch (HttpServerErrorException e) {
                // Handle 5xx errors from external server
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.badGatewayNotConnect(),
                                ResponseMessageUtil.badGatewayNotConnect(url),
                                e.getStatusCode() + " - " + e.getStatusText()
                        ));

            } catch (ResourceAccessException e) {
                // Handle unreachable external host
                String targetHost = ExceptionUtils.extractTargetHost(e.getMessage());

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.serviceUnavailable(),
                                ResponseMessageUtil.serviceUnavailable(),
                                ResponseMessageUtil.badGatewayNotConnect(targetHost)
                        ));

            } catch (Exception e) {
                // Handle unexpected external error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.internalError(),
                                ResponseMessageUtil.internalError(resource)
                        ));
            }

        } catch (Exception e) {
            // Handle unexpected internal error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError(resource)
                    ));
        }
    }

    /**
     * Retrieves a user access token from an external authentication service using the provided appKey and authCode.
     * It then validates the received token and returns the user's information payload if valid.
     *
     * @param appKey   the unique application key used to find the external service configuration
     * @param authCode the authorization code received from the authentication step
     * @return ResponseEntity containing the user payload if successful or an appropriate error message
     */
    @GetMapping("/get-user-access-token")
    public ResponseEntity<ApiResponse<?>> getUserAccessToken(
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String authCode) {

        String resource = "Access token";

        // Input validation
        if (appKey == null || appKey.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    ResponseCodeUtil.invalid(),
                    ResponseMessageUtil.invalid("appKey")
            ));
        }

        if (authCode == null || authCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    ResponseCodeUtil.invalid(),
                    ResponseMessageUtil.invalid("authCode")
            ));
        }

        try {
            // Retrieve service configuration
            Optional<InternalCamDigiKey> host = internalCamDigiKeyService.findByAppKey(appKey);
            if (host.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(
                        ResponseCodeUtil.configurationNotFound(),
                        ResponseMessageUtil.configurationNotFound("appKey")
                ));
            }

            // Build external request URL
            String endpoint = apiPrefix + "/portal/camdigikey/get-user-access-token";
            String params = "?authCode=" + authCode;
            String url = host.get().getAccessURL() + endpoint + params;

            try {
                // Call external service to get access token
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode data = root.path("data");
                String accessToken = data.path("accessToken").asText();

                // Validate the token
                ResponseEntity<ApiResponse<?>> jwtResponse = validateJwt(appKey, accessToken);
                ApiResponse<?> jwtBody = jwtResponse.getBody();

                if (jwtBody == null || jwtBody.getData() == null) {
                    return ResponseEntity.badRequest().body(new ApiResponse<>(
                            ResponseCodeUtil.jwtValidationFailed(),
                            ResponseMessageUtil.jwtValidationFailed(),
                            root.path("message")
                    ));
                }

                JsonNode jwtRoot = objectMapper.convertValue(jwtBody.getData(), JsonNode.class);
                JsonNode payload = jwtRoot.path("payload");

                // Check payload status
                if (!payload.path("status").asBoolean()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                            ResponseCodeUtil.accessTokenInvalidOrExpired(),
                            ResponseMessageUtil.accessTokenInvalidOrExpired()
                    ));
                }

                // Return successful response
                return ResponseEntity.ok(new ApiResponse<>(
                        ResponseCodeUtil.processed(),
                        ResponseMessageUtil.processed(resource),
                        payload
                ));

            } catch (HttpClientErrorException e) {
                // Handle 4xx errors
                int rawStatusCode = e.getStatusCode().value();
                HttpStatus status = HttpStatus.resolve(rawStatusCode);
                if (status == null) status = HttpStatus.BAD_REQUEST;

                // Map HTTP status to FMIS ResponseCodeDTO
                ResponseCodeDTO fmisResponseCode = switch (status) {
                    case NOT_FOUND -> ResponseCodeUtil.externalResourceNotFound();
                    case BAD_REQUEST -> ResponseCodeUtil.externalError();
                    default -> ResponseCodeUtil.externalClientError();
                };

                String message = switch (status) {
                    case NOT_FOUND -> ResponseMessageUtil.externalResourceNotFound(resource);
                    case BAD_REQUEST -> ResponseMessageUtil.externalError(resource);
                    default -> ResponseMessageUtil.externalClientError(resource);
                };

                // Return ApiResponse with FMIS response_code
                return ResponseEntity.status(status)
                        .body(new ApiResponse<>(
                                fmisResponseCode,
                                message,
                                rawStatusCode + " - " + status.getReasonPhrase()
                        ));

            } catch (HttpServerErrorException e) {
                // Handle server-side HTTP errors (5xx)
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse<>(
                        ResponseCodeUtil.invalid(),
                        ResponseMessageUtil.invalid("authCode"),
                        e.getMessage()
                ));

            } catch (ResourceAccessException e) {
                // Handle unreachable host or timeout
                String targetHost = ExceptionUtils.extractTargetHost(e.getMessage());

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<>(
                        ResponseCodeUtil.serviceUnavailable(),
                        ResponseMessageUtil.serviceUnavailable(),
                        ResponseMessageUtil.badGatewayNotConnect(targetHost)
                ));

            } catch (Exception e) {
                // Handle unexpected errors during external request
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                        ResponseCodeUtil.internalError(),
                        ResponseMessageUtil.internalError(resource)
                ));
            }

        } catch (Exception e) {
            // Catch-all for any other server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    ResponseCodeUtil.internalError(),
                    ResponseMessageUtil.internalError(resource)
            ));
        }
    }

    /**
     * Validates a JSON Web Token (JWT) against an external authentication service using the provided appKey.
     * The method checks the validity and integrity of the JWT and returns the validation result or an error message.
     *
     * @param appKey the unique application key used to find the external service configuration
     * @param jwt    the JSON Web Token to be validated
     * @return ResponseEntity containing the validation result if successful or an appropriate error message
     */
    @GetMapping("/validate-jwt")
    public ResponseEntity<ApiResponse<?>> validateJwt(
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String jwt) {

        String resource = "JWT";

        // Validate input
        if (appKey == null || appKey.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.invalid(),
                            ResponseMessageUtil.invalid("appKey")
                    ));
        }

        if (jwt == null || jwt.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.invalid(),
                            ResponseMessageUtil.invalid("jwt")
                    ));
        }

        try {
            // Lookup host configuration by appKey
            Optional<InternalCamDigiKey> host = internalCamDigiKeyService.findByAppKey(appKey);

            if (host.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound("appKey")
                        ));
            }

            // Prepare URL for external service
            String endpoint = apiPrefix + "/portal/camdigikey/validate-jwt";
            String params = "?jwt=" + jwt;
            String url = host.get().getAccessURL() + endpoint + params;

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode data = objectMapper.readTree(response.getBody());

                return ResponseEntity.ok(new ApiResponse<>(
                        ResponseCodeUtil.processed(),
                        ResponseMessageUtil.processed(resource),
                        data
                ));

            } catch (HttpClientErrorException e) {
                // Handle 4xx errors
                int rawStatusCode = e.getStatusCode().value();
                HttpStatus status = HttpStatus.resolve(rawStatusCode);
                if (status == null) status = HttpStatus.BAD_REQUEST;

                // Map HTTP status to FMIS ResponseCodeDTO
                ResponseCodeDTO fmisResponseCode = switch (status) {
                    case NOT_FOUND -> ResponseCodeUtil.externalResourceNotFound();
                    case BAD_REQUEST -> ResponseCodeUtil.externalError();
                    default -> ResponseCodeUtil.externalClientError();
                };

                String message = switch (status) {
                    case NOT_FOUND -> ResponseMessageUtil.externalResourceNotFound(resource);
                    case BAD_REQUEST -> ResponseMessageUtil.externalError(resource);
                    default -> ResponseMessageUtil.externalClientError(resource);
                };

                // Return ApiResponse with FMIS response_code
                return ResponseEntity.status(status)
                        .body(new ApiResponse<>(
                                fmisResponseCode,
                                message,
                                rawStatusCode + " - " + status.getReasonPhrase()
                        ));

            } catch (HttpServerErrorException e) {
                // Handle 5xx errors from external server
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.invalid(),
                                ResponseMessageUtil.invalid("JWT"),
                                e.getMessage()
                        ));

            } catch (ResourceAccessException e) {
                // Handle unreachable external host
                String targetHost = ExceptionUtils.extractTargetHost(e.getMessage());

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.serviceUnavailable(),
                                ResponseMessageUtil.serviceUnavailable(),
                                ResponseMessageUtil.badGatewayNotConnect(targetHost)
                        ));

            } catch (Exception e) {
                // Handle unexpected external error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.internalError(),
                                ResponseMessageUtil.internalError(resource)
                        ));
            }

        } catch (Exception e) {
            // Handle unexpected internal error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError(resource)
                    ));
        }
    }
}