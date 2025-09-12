package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.ConfigDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.FmisRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.FmisService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Hidden
@RestController
@RequestMapping
public class ConfigController {

    private final FmisRepository fmisRepository;
    private final FmisService fmisService;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Constructor for {@code ConfigController} that injects required dependencies.
     *
     * @param fmisRepository      repository for FMIS data access operations
     * @param fmisService         service layer handling FMIS business logic
     * @param authorizationHelper helper for authorization and authentication checks
     */
    public ConfigController(FmisRepository fmisRepository, FmisService fmisService, AuthorizationHelper authorizationHelper) {
        this.fmisRepository = fmisRepository;
        this.fmisService = fmisService;
        this.authorizationHelper = authorizationHelper;
    }

    /**
     * Endpoint to retrieve a paginated list of FMIS configurations.
     * Accessible only to Admin users.
     *
     * @param page The page number (default: 0).
     * @param size The number of items per page (default: 10).
     * @return A paginated list of FMIS configurations or an appropriate HTTP response in case of error or access denial.
     */
    @Operation(
            summary = "Get FMIS Configuration",
            description = "Retrieves a paginated list of FMIS configurations. Accessible only to Admins."
    )
    @Hidden
    @GetMapping("/list-config")
    public ResponseEntity<ApiResponse<?>> listConfig(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        try {
            // Fetch paginated FMIS config list
            Page<FMIS> config = fmisService.getConfig(page, size);

            return ResponseEntity.ok(new ApiResponse<>(
                    ResponseCodeUtil.fetched(),
                    ResponseMessageUtil.fetched("Config"),
                    config
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.fetchError(),
                            ResponseMessageUtil.fetchError("Config")
                    ));
        }
    }

    /**
     * Endpoint to update the FMIS configuration.
     * Accessible only to Admin users.
     *
     * @param request       The configuration update request containing the new FMIS configuration data.
     * @param bindingResult The validation result for the incoming request data.
     * @return A response indicating the success or failure of the update operation.
     */
    @Operation(
            summary = "Update FMIS Configuration",
            description = "Allows a Admin to update FMIS configuration."
    )
    @Hidden
    @PutMapping("/update-fmis-config")
    public ResponseEntity<ApiResponse<?>> updateConfig(
            @Validated @RequestBody ConfigDTO request,
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

        try {
            // Fetch existing configuration
            Optional<FMIS> optionalConfig = fmisRepository.findFirstBy();
            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.notFound(),
                                ResponseMessageUtil.notFound("Config")
                        ));
            }

            // Update configuration fields
            FMIS config = optionalConfig.get();
            config.setBaseURL(request.getBaseURL());
            config.setUsername(request.getUsername());
            config.setPassword(request.getPassword());
            config.setContentType(request.getContentType());
            config.setDescription(request.getDescription());
            config.setCreatedDate(LocalDateTime.now());

            // Persist updates
            fmisRepository.save(config);

            // Return success response
            return ResponseEntity.ok(new ApiResponse<>(
                    ResponseCodeUtil.updated(),
                    ResponseMessageUtil.updated("Config")
            ));

        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Config")
                    ));
        }
    }
}