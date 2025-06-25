package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.InternalCamDigiKeyService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.AuthorizationHelper;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ExceptionUtils;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ValidationErrorUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v1/internal/camdigikey")
public class InternalCamDigiKeyController {

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

        // Authenticate user
        Object userValidation = authorizationHelper.validateUser();
        if (userValidation instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(userValidation);
        }
        User currentUser = (User) userValidation;

        // Authorize admin roles
        ResponseEntity<ApiResponse<Object>> adminValidation = authorizationHelper.validateAdmin(currentUser);
        if (adminValidation != null) {
            return AuthorizationHelper.castToApiResponse(adminValidation);
        }

        // Handle validation errors
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ApiResponseConstants.BAD_REQUEST_CODE, validationErrors));
        }

        try {
            // Check for duplicate values in unique fields
            if (internalCamDigiKeyService.existsByName(internalCamDigiKey.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.NAME_TAKEN
                        ));
            }
            if (internalCamDigiKeyService.existsByAppKey(internalCamDigiKey.getAppKey())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.APP_KEY_TAKEN
                        ));
            }
            if (internalCamDigiKeyService.existsByIpAddress(internalCamDigiKey.getIpAddress())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.IP_ADDRESS_TAKEN
                        ));
            }
            if (internalCamDigiKeyService.existsByAccessURL(internalCamDigiKey.getAccessURL())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ACCESS_URL_TAKEN
                        ));
            }

            // Set creator info and save the entry
            internalCamDigiKey.setCreatedBy(currentUser.getId().intValue());
            InternalCamDigiKey saved = internalCamDigiKeyService.createInternalCamDigiKey(internalCamDigiKey);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.CREATED_CODE,
                            ApiResponseConstants.CREATED,
                            saved
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
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

        try {
            // Validate authenticated user
            Object userValidation = authorizationHelper.validateUser();
            if (userValidation instanceof ResponseEntity) {
                return AuthorizationHelper.castToApiResponse(userValidation);
            }
            User currentUser = (User) userValidation;

            // Validate admin role
            ResponseEntity<ApiResponse<Object>> adminValidation = authorizationHelper.validateAdmin(currentUser);
            if (adminValidation != null) {
                return AuthorizationHelper.castToApiResponse(adminValidation);
            }

            // Fetch filtered and paginated internal CamDigiKey list
            Page<InternalCamDigiKey> hosts = internalCamDigiKeyService.getFilteredInternalCamDigiKeys(
                    page, size, name, appKey, ipAddress, accessURL, createdDate);

            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.SUCCESS,
                    hosts
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
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

        // Authenticate user
        Object userValidation = authorizationHelper.validateUser();
        if (userValidation instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(userValidation);
        }
        User currentUser = (User) userValidation;

        // Authorize admin or privileged user
        ResponseEntity<ApiResponse<Object>> adminValidation = authorizationHelper.validateAdmin(currentUser);
        if (adminValidation != null) {
            return AuthorizationHelper.castToApiResponse(adminValidation);
        }

        // Validate request payload
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
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
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.BAD_REQUEST_ID_NOT_NUMERIC
                    ));
        }

        try {
            // Check if the host exists
            Optional<InternalCamDigiKey> optionalExisting = internalCamDigiKeyService.findById(hostId);
            if (optionalExisting.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NOT_FOUND_CODE,
                                ApiResponseConstants.HOST_NOT_FOUND
                        ));
            }

            InternalCamDigiKey existing = optionalExisting.get();

            // Manual uniqueness check for appKey
            Optional<InternalCamDigiKey> appKeyOwner = internalCamDigiKeyService.findByAppKey(updatedData.getAppKey());
            if (appKeyOwner.isPresent() && !appKeyOwner.get().getId().equals(existing.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.APP_KEY_TAKEN
                        ));
            }

            // Manual uniqueness check for name
            if (!existing.getName().equals(updatedData.getName())
                    && internalCamDigiKeyService.existsByName(updatedData.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.NAME_TAKEN
                        ));
            }

            // Access URL uniqueness
            if (!existing.getAccessURL().equals(updatedData.getAccessURL())
                    && internalCamDigiKeyService.existsByAccessURL(updatedData.getAccessURL())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ACCESS_URL_TAKEN
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
                    ApiResponseConstants.UPDATED_CODE,
                    ApiResponseConstants.UPDATED,
                    saved
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
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

        // Validate input
        if (appKey == null || appKey.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.ERROR_MISSING_REQUIRED_PARAM + "appKey"
                    ));
        }

        try {
            // Lookup host configuration by appKey
            Optional<InternalCamDigiKey> host = internalCamDigiKeyService.findByAppKey(appKey);

            if (host.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ERROR_NO_CONFIGURATION_FOUND + "(" + appKey + ")"
                        ));
            }

            // Prepare URL for external service
            String endpoint = "/api/v1/portal/camdigikey/login-token";
            String url = host.get().getAccessURL() + endpoint;

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode data = root.path("data");

                return ResponseEntity.ok(new ApiResponse<>(
                        ApiResponseConstants.SUCCESS_CODE,
                        ApiResponseConstants.SUCCESS,
                        data
                ));

            } catch (HttpClientErrorException e) {
                // Handle 4xx errors
                int rawStatusCode = e.getStatusCode().value();
                HttpStatus status = HttpStatus.resolve(rawStatusCode);
                if (status == null) status = HttpStatus.BAD_REQUEST;

                String message = switch (status) {
                    case NOT_FOUND -> ApiResponseConstants.EXTERNAL_RESOURCE_NOT_FOUND;
                    case BAD_REQUEST -> ApiResponseConstants.EXTERNAL_BAD_REQUEST;
                    default -> ApiResponseConstants.EXTERNAL_CLIENT_ERROR;
                };

                return ResponseEntity.status(status)
                        .body(new ApiResponse<>(
                                status.value(),
                                message,
                                rawStatusCode + " - " + status.getReasonPhrase()
                        ));

            } catch (HttpServerErrorException e) {
                // Handle 5xx errors from external server
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_GATEWAY_CODE,
                                ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT,
                                e.getStatusCode() + " - " + e.getStatusText()
                        ));

            } catch (ResourceAccessException e) {
                // Handle unreachable external host
                String targetHost = ExceptionUtils.extractTargetHost(e.getMessage());

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                ApiResponseConstants.SERVICE_UNAVAILABLE,
                                ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT + " (" + targetHost + ")"
                        ));

            } catch (Exception e) {
                // Handle unexpected external error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                        ));
            }

        } catch (Exception e) {
            // Handle unexpected internal error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
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

        // Input validation
        if (appKey == null || appKey.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    ApiResponseConstants.BAD_REQUEST_CODE,
                    ApiResponseConstants.ERROR_MISSING_REQUIRED_PARAM + "appKey"
            ));
        }

        if (authCode == null || authCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    ApiResponseConstants.BAD_REQUEST_CODE,
                    ApiResponseConstants.ERROR_MISSING_REQUIRED_PARAM + "authCode"
            ));
        }

        try {
            // Retrieve service configuration
            Optional<InternalCamDigiKey> host = internalCamDigiKeyService.findByAppKey(appKey);
            if (host.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(
                        ApiResponseConstants.BAD_REQUEST_CODE,
                        ApiResponseConstants.ERROR_NO_CONFIGURATION_FOUND + " (" + appKey + ")"
                ));
            }

            // Build external request URL
            String endpoint = "/api/v1/portal/camdigikey/get-user-access-token";
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
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.ERROR_JWT_VALIDATION_FAILED,
                            root.path("message")
                    ));
                }

                JsonNode jwtRoot = objectMapper.convertValue(jwtBody.getData(), JsonNode.class);
                JsonNode payload = jwtRoot.path("payload");

                // Check payload status
                if (!payload.path("status").asBoolean()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                            ApiResponseConstants.UNAUTHORIZED_CODE,
                            ApiResponseConstants.ERROR_ACCESS_TOKEN_INVALID_OR_EXPIRED
                    ));
                }

                // Return successful response
                return ResponseEntity.ok(new ApiResponse<>(
                        ApiResponseConstants.SUCCESS_CODE,
                        ApiResponseConstants.SUCCESS,
                        payload
                ));

            } catch (HttpClientErrorException e) {
                // Handle client-side HTTP errors (4xx)
                HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
                if (status == null) status = HttpStatus.BAD_REQUEST;

                String message = switch (status) {
                    case NOT_FOUND -> ApiResponseConstants.EXTERNAL_RESOURCE_NOT_FOUND;
                    case BAD_REQUEST -> ApiResponseConstants.EXTERNAL_BAD_REQUEST;
                    default -> ApiResponseConstants.EXTERNAL_CLIENT_ERROR;
                };

                return ResponseEntity.status(status).body(new ApiResponse<>(
                        status.value(),
                        message,
                        e.getResponseBodyAsString()
                ));

            } catch (HttpServerErrorException e) {
                // Handle server-side HTTP errors (5xx)
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse<>(
                        ApiResponseConstants.BAD_GATEWAY_CODE,
                        ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT,
                        e.getStatusCode() + " - " + e.getStatusText()
                ));

            } catch (ResourceAccessException e) {
                // Handle unreachable host or timeout
                String targetHost = ExceptionUtils.extractTargetHost(e.getMessage());

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<>(
                        ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                        ApiResponseConstants.SERVICE_UNAVAILABLE,
                        ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT + " (" + targetHost + ")"
                ));

            } catch (Exception e) {
                // Handle unexpected errors during external request
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                        ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                        ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                ));
            }

        } catch (Exception e) {
            // Catch-all for any other server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                    ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
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

        // Validate input
        if (appKey == null || appKey.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.ERROR_MISSING_REQUIRED_PARAM + "appKey"
                    ));
        }

        if (jwt == null || jwt.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.ERROR_MISSING_REQUIRED_PARAM + "jwt"
                    ));
        }

        try {
            // Lookup host configuration by appKey
            Optional<InternalCamDigiKey> host = internalCamDigiKeyService.findByAppKey(appKey);

            if (host.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ERROR_NO_CONFIGURATION_FOUND + "(" + appKey + ")"
                        ));
            }

            // Prepare URL for external service
            String endpoint = "/api/v1/portal/camdigikey/validate-jwt";
            String params = "?jwt=" + jwt;
            String url = host.get().getAccessURL() + endpoint + params;

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode data = objectMapper.readTree(response.getBody());

                return ResponseEntity.ok(new ApiResponse<>(
                        ApiResponseConstants.SUCCESS_CODE,
                        ApiResponseConstants.SUCCESS,
                        data
                ));

            } catch (HttpClientErrorException e) {
                // Handle 4xx errors
                int rawStatusCode = e.getStatusCode().value();
                HttpStatus status = HttpStatus.resolve(rawStatusCode);
                if (status == null) status = HttpStatus.BAD_REQUEST;

                String message = switch (status) {
                    case NOT_FOUND -> ApiResponseConstants.EXTERNAL_RESOURCE_NOT_FOUND;
                    case BAD_REQUEST -> ApiResponseConstants.EXTERNAL_BAD_REQUEST;
                    default -> ApiResponseConstants.EXTERNAL_CLIENT_ERROR;
                };

                return ResponseEntity.status(status)
                        .body(new ApiResponse<>(
                                status.value(),
                                message,
                                rawStatusCode + " - " + status.getReasonPhrase()
                        ));

            } catch (HttpServerErrorException e) {
                // Handle 5xx errors from external server
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_GATEWAY_CODE,
                                ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT,
                                e.getStatusCode() + " - " + e.getStatusText()
                        ));

            } catch (ResourceAccessException e) {
                // Handle unreachable external host
                String targetHost = ExceptionUtils.extractTargetHost(e.getMessage());

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                ApiResponseConstants.SERVICE_UNAVAILABLE,
                                ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT + " (" + targetHost + ")"
                        ));

            } catch (Exception e) {
                // Handle unexpected external error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                        ));
            }

        } catch (Exception e) {
            // Handle unexpected internal error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }
}