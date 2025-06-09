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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
}