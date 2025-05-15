package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.ConfigDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.FmisRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.FmisService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.RoleService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ValidationErrorUtils;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
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
@RequestMapping("/api/v1")
public class ConfigController {

    private final FmisRepository fmisRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final FmisService fmisService;

    public ConfigController(FmisRepository fmisRepository, UserService userService, RoleService roleService, FmisService fmisService) {
        this.fmisRepository = fmisRepository;
        this.userService = userService;
        this.roleService = roleService;
        this.fmisService = fmisService;
    }

    /**
     * Endpoint to retrieve a paginated list of FMIS configurations.
     * Accessible only to Super Admin users.
     *
     * @param page The page number (default: 0).
     * @param size The number of items per page (default: 10).
     * @return A paginated list of FMIS configurations or an appropriate HTTP response in case of error or access denial.
     */
    @Operation(
            summary = "Get FMIS Configuration",
            description = "Retrieves a paginated list of FMIS configurations. Accessible only to Super Admins."
    )
    @Hidden
    @GetMapping("/list-config")
    public ResponseEntity<ApiResponse<?>> listConfig(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Retrieve the username of the authenticated user
        String username = userService.getAuthenticatedUsername();

        // Attempt to retrieve the current user by username
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.UNAUTHORIZED_CODE,
                            ApiResponseConstants.UNAUTHORIZED_USER_NOT_FOUND
                    ));
        }

        User currentUser = userOptional.get();

        // Verify that the user's role exists
        Long roleId = currentUser.getRole().getId();
        if (!roleService.existsById(roleId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.NOT_FOUND_CODE,
                            ApiResponseConstants.ROLE_NOT_FOUND
                    ));
        }

        // Restrict access to only Super Admins (level 1)
        if (currentUser.getRole().getLevel() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.FORBIDDEN_CODE,
                            ApiResponseConstants.FORBIDDEN
                    ));
        }

        try {
            // Fetch paginated list of FMIS configurations
            Page<FMIS> config = fmisService.getConfig(page, size);

            // Return 204 if no configurations found
            if (config.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NO_CONTENT_CODE,
                                ApiResponseConstants.NOT_FOUND
                        ));
            }

            // Return successful response with FMIS configuration list
            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.SUCCESS, config
            ));

        } catch (Exception e) {
            // Return 500 in case of an internal error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_FETCHING_PARTNERS + e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint to update the FMIS configuration.
     * Accessible only to Super Admin users (Level 1).
     *
     * @param request       The configuration update request containing the new FMIS configuration data.
     * @param bindingResult The validation result for the incoming request data.
     * @return A response indicating the success or failure of the update operation.
     */
    @Operation(
            summary = "Update FMIS Configuration",
            description = "Allows a Super Admin (Level 1) to update FMIS configuration."
    )
    @Hidden
    @PutMapping("/update-fmis-config")
    public ResponseEntity<ApiResponse<?>> updateConfig(
            @Validated @RequestBody ConfigDTO request,
            BindingResult bindingResult) {

        // Handle validation errors
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(ApiResponseConstants.BAD_REQUEST_CODE, validationErrors)
            );
        }

        // Retrieve the username of the authenticated user
        String username = userService.getAuthenticatedUsername();

        // Attempt to retrieve the current user by username
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.UNAUTHORIZED_CODE,
                            ApiResponseConstants.UNAUTHORIZED_USER_NOT_FOUND
                    ));
        }

        User currentUser = userOptional.get();

        // Verify that the user's role exists
        Long roleId = currentUser.getRole().getId();
        if (!roleService.existsById(roleId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.NOT_FOUND_CODE,
                            ApiResponseConstants.ROLE_NOT_FOUND
                    ));
        }

        // Restrict access to only Super Admins (level 1)
        if (currentUser.getRole().getLevel() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.FORBIDDEN_CODE,
                            ApiResponseConstants.FORBIDDEN
                    ));
        }

        try {
            Optional<FMIS> optionalConfig = fmisRepository.findFirstBy();

            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.NO_CONFIG_TO_UPDATE
                        ));
            }

            FMIS config = optionalConfig.get();
            config.setBaseURL(request.getBaseURL());
            config.setUsername(request.getUsername());
            config.setPassword(request.getPassword());
            config.setContentType(request.getContentType());
            config.setDescription(request.getDescription());
            config.setCreatedDate(LocalDateTime.now());

            fmisRepository.save(config);

            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.UPDATED
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            e.getMessage()
                    ));
        }
    }
}