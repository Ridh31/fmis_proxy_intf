package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SecurityServerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.AuthorizationHelper;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ValidationErrorUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing security server operations.
 * Provides endpoints for creating and managing SecurityServer entities.
 */
@Tag(name = "Security Server", description = "Endpoints related to managing security server.")
@Hidden
@RestController
@RequestMapping("/security-server")
public class SecurityServerController {

    private final SecurityServerService securityServerService;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Constructs the SecurityServerController with the required services.
     *
     * @param securityServerService Service for handling SecurityServer operations.
     * @param authorizationHelper   Helper for authentication and role-based access control.
     */
    public SecurityServerController(
            SecurityServerService securityServerService,
            AuthorizationHelper authorizationHelper) {
        this.securityServerService = securityServerService;
        this.authorizationHelper = authorizationHelper;
    }

    /**
     * Endpoint to create a new Security Server.
     * Only Super Admins and Admins (level 1 and 2) are allowed to perform this action.
     *
     * @param securityServer SecurityServer data to be created.
     * @param bindingResult  Validation result for the request body.
     * @return ApiResponse with success or failure details.
     */
    @PostMapping("/create-server")
    public ResponseEntity<ApiResponse<?>> createSecurityServer(
            @Validated @RequestBody SecurityServer securityServer,
            BindingResult bindingResult) {

        // Extract validation errors from the request body if any exist
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return a bad request with the errors
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            validationErrors
                    ));
        }

        // Check if a SecurityServer with the same name already exists
        if (securityServerService.existsByName(securityServer.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.NAME_TAKEN + " (" + securityServer.getName() + ")"
                    ));
        }

        // Check if a SecurityServer with the same configKey already exists
        if (securityServerService.existsByConfigKey(securityServer.getConfigKey())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.CONFIG_KEY_TAKEN + " (" + securityServer.getConfigKey() + ")"
                    ));
        }

        // Authenticate the user and verify role-based access permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        // Retrieve the current logged-in user for setting the creator of the SecurityServer
        User currentUser = (User) authorization;

        try {
            // Set the creator of the security server as the current logged-in user
            securityServer.setCreatedBy(currentUser.getId());

            // Save the SecurityServer to the database
            SecurityServer savedServer = securityServerService.create(securityServer);

            // Return a successful response with the saved SecurityServer details
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.CREATED_CODE,
                            ApiResponseConstants.CREATED,
                            savedServer
                    ));
        } catch (Exception e) {
            // Handle any unexpected errors that occur while saving the SecurityServer
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint to fetch a paginated and filtered list of Security Servers.
     * Filters include name, configKey, description, and createdDate.
     *
     * @param page        The page number (default is 0).
     * @param size        The number of records per page (default is 10).
     * @param name        Optional filter by name.
     * @param configKey   Optional filter by config key.
     * @param description Optional filter by description.
     * @param createdDate Optional filter by creation date (dd-MM-yyyy).
     * @return A paginated list of filtered Security Servers.
     */
    @GetMapping("list-server")
    public ResponseEntity<ApiResponse<?>> listSecurityServer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createdDate) {

        // Authenticate user and verify role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        try {
            // Fetch filtered and paginated Security Server list
            Page<SecurityServer> server = securityServerService.getFilteredSecurityServers(
                    page, size, name, configKey, description, createdDate);

            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.SUCCESS,
                    server
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
     * Updates an existing SecurityServer by ID.
     * Validates input, checks authorization, ensures name and configKey uniqueness,
     * then saves and returns the updated entity.
     *
     * @param id the SecurityServer ID from the URL path
     * @param securityServer the updated SecurityServer data from the request body
     * @param bindingResult validation results for the input data
     * @return ResponseEntity with success or error details
     */
    @PutMapping("/update-server/{id}")
    public ResponseEntity<ApiResponse<?>> updateSecurityServer(
            @PathVariable String id,
            @RequestBody SecurityServer securityServer,
            BindingResult bindingResult) {

        // Extract validation errors from the request body if any exist
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return a bad request with the errors
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            validationErrors
                    ));
        }

        // Validate server ID format
        long serverId;
        try {
            serverId = Long.parseLong(id);
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.BAD_REQUEST_ID_NOT_NUMERIC
                    ));
        }

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        try {
            // Check if the server exists
            Optional<SecurityServer> optionalExisting = securityServerService.findById(serverId);
            if (optionalExisting.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NOT_FOUND_CODE,
                                String.format(ApiResponseConstants.ENTITY_NOT_FOUND, "Security Server")
                        ));
            }

            // Check if the name is already taken
            Optional<SecurityServer> nameOwner = securityServerService.findByName(securityServer.getName());
            if (nameOwner.isPresent() && !nameOwner.get().getId().equals(serverId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.NAME_TAKEN
                        ));
            }

            // Check if the config key is already taken
            Optional<SecurityServer> configKeyOwner = securityServerService.getByConfigKey(securityServer.getConfigKey());
            if (configKeyOwner.isPresent() && !configKeyOwner.get().getId().equals(serverId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.CONFIG_KEY_TAKEN
                        ));
            }

            // Proceed with the update
            SecurityServer existing = optionalExisting.get();

            // Update the server details
            existing.setName(securityServer.getName());
            existing.setConfigKey(securityServer.getConfigKey());
            existing.setBaseURL(securityServer.getBaseURL());
            existing.setEndpoint(securityServer.getEndpoint());
            existing.setSubsystem(securityServer.getSubsystem());
            existing.setUsername(securityServer.getUsername());
            existing.setPassword(securityServer.getPassword());
            existing.setContentType(securityServer.getContentType());
            existing.setDescription(securityServer.getDescription());

            // Save the updated security server
            securityServerService.update(existing);

            // Return success response with updated data
            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.SUCCESS,
                    existing
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }
}