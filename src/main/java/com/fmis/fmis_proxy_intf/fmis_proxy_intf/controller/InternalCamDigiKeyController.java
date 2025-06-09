package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.InternalCamDigiKeyService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.AuthorizationHelper;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ValidationErrorUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    private final InternalCamDigiKeyService internalCamDigiKeyService;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Constructor for dependency injection.
     *
     * @param internalCamDigiKeyService Service for CamDigiKey operations.
     * @param authorizationHelper       Helper for user authorization and validation.
     */
    public InternalCamDigiKeyController(InternalCamDigiKeyService internalCamDigiKeyService, AuthorizationHelper authorizationHelper) {
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
     * Endpoint to retrieve a paginated list of internal CamDigiKey hosts.
     *
     * @param page the page number (default is 0)
     * @param size the number of items per page (default is 10)
     * @return ResponseEntity containing the list of hosts or an appropriate status message
     */
    @GetMapping("list-host")
    public ResponseEntity<ApiResponse<?>> getAllHosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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

            // Fetch paginated internal CamDigiKey list
            Page<InternalCamDigiKey> hosts = internalCamDigiKeyService.getAllInternalCamDigiKey(page, size);

            if (hosts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NO_CONTENT_CODE,
                                ApiResponseConstants.NOT_FOUND
                        ));
            }

            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.SUCCESS,
                    hosts
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.INTERNAL_SERVER_ERROR + e.getMessage()
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

            // Check for uniqueness conflicts (only if values changed)
            if (!existing.getName().equals(updatedData.getName()) &&
                    internalCamDigiKeyService.existsByName(updatedData.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.NAME_TAKEN
                        ));
            }

            if (!existing.getAppKey().equals(updatedData.getAppKey()) &&
                    internalCamDigiKeyService.existsByAppKey(updatedData.getAppKey())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.APP_KEY_TAKEN
                        ));
            }

            if (!existing.getIpAddress().equals(updatedData.getIpAddress()) &&
                    internalCamDigiKeyService.existsByIpAddress(updatedData.getIpAddress())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.IP_ADDRESS_TAKEN
                        ));
            }

            if (!existing.getAccessURL().equals(updatedData.getAccessURL()) &&
                    internalCamDigiKeyService.existsByAccessURL(updatedData.getAccessURL())) {
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
            // Handle unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }
}