package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.RoleService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing partner-related operations.
 */
@Tag(
        name = "Partner Operations",
        description = "Endpoints related to managing partners."
)
@Hidden
@RestController
@RequestMapping("/api/v1")
public class PartnerController {

    private final PartnerService partnerService;
    private final UserService userService;
    private final RoleService roleService;

    @Value("${application-base-url}")
    private String baseUrl;

    // Constructor injection for the services
    public PartnerController(PartnerService partnerService, UserService userService, RoleService roleService) {
        this.partnerService = partnerService;
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Endpoint to create a new partner.
     *
     * @param partner The partner details.
     * @return Response entity with the creation status.
     */
    @Operation(
            summary = "Create a new partner",
            description = "Only accessible by Super Admin users. Creates a new partner and returns the partner details along with status. Super Admins are the only users with permission to create partners.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = {
                            @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    schema = @Schema(implementation = Partner.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = ApiRequestExamples.CURL,
                                                    value = ApiRequestExamples.CREATE_PARTNER_CURL
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.JAVASCRIPT,
                                                    value = ApiRequestExamples.CREATE_PARTNER_JS_FETCH
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.PYTHON,
                                                    value = ApiRequestExamples.CREATE_PARTNER_PYTHON
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.JAVA_OKHTTP,
                                                    value = ApiRequestExamples.CREATE_PARTNER_JAVA_OKHTTP
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.CSHARP,
                                                    value = ApiRequestExamples.CREATE_PARTNER_CSHARP
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.PHP_CURL,
                                                    value = ApiRequestExamples.CREATE_PARTNER_PHP_CURL
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.NODEJS,
                                                    value = ApiRequestExamples.CREATE_PARTNER_NODEJS
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.RUBY,
                                                    value = ApiRequestExamples.CREATE_PARTNER_RUBY
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.GO,
                                                    value = ApiRequestExamples.CREATE_PARTNER_GO
                                            )
                                    }
                            )
                    }
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.CREATED_CODE_STRING,
                            description = ApiResponseConstants.CREATED,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_SUCCESS,
                                            value = ApiResponseExamples.CREATE_PARTNER_SUCCESS
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.BAD_REQUEST_CODE_STRING,
                            description = ApiResponseConstants.PARTNER_CODE_TAKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_BAD_REQUEST,
                                            value = ApiResponseExamples.CREATE_PARTNER_BAD_REQUEST
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.UNAUTHORIZED_CODE_STRING,
                            description = ApiResponseConstants.UNAUTHORIZED_LOGIN_REQUIRED,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_UNAUTHORIZED,
                                            value = ApiResponseExamples.CREATE_PARTNER_UNAUTHORIZED
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.NOT_FOUND_CODE_STRING,
                            description = ApiResponseConstants.USER_NOT_FOUND,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_NOT_FOUND,
                                            value = ApiResponseExamples.CREATE_PARTNER_NOT_FOUND
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE_STRING,
                            description = ApiResponseConstants.INTERNAL_SERVER_ERROR,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_SERVER_ERROR,
                                            value = ApiResponseExamples.CREATE_PARTNER_SERVER_ERROR
                                    )
                            )
                    )
            }
    )
    @PostMapping("/create-partner")
    public ResponseEntity<ApiResponse<?>> createPartner(@Validated @RequestBody Partner partner, BindingResult bindingResult) {

        // Extract validation errors using the utility method
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return them in the response
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ApiResponseConstants.BAD_REQUEST_CODE, validationErrors));
        }

        try {
            // Retrieve the currently authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.UNAUTHORIZED_CODE,
                                ApiResponseConstants.UNAUTHORIZED_LOGIN_REQUIRED
                        ));
            }

            String username = authentication.getName();

            // Get the user from the database based on the username
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.UNAUTHORIZED_CODE,
                                ApiResponseConstants.UNAUTHORIZED_USER_NOT_FOUND
                        ));
            }

            User currentUser = userOptional.get();

            // Fetch the role of the authenticated user
            Long roleId = currentUser.getRole().getId();
            if (!roleService.existsById(roleId)) {
                // Handle case where role does not exist
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NOT_FOUND_CODE,
                                ApiResponseConstants.ROLE_NOT_FOUND
                        ));
            }

            // Check if the authenticated user is a Super Admin (level 1)
            if (currentUser.getRole().getLevel() != 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.FORBIDDEN_CODE,
                                ApiResponseConstants.FORBIDDEN_CREATE_PARTNER
                        ));
            }

            // Check if the partner code already exists in the database
            if (partnerService.findByCode(partner.getCode()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.PARTNER_CODE_TAKEN
                        ));
            }

            // Generate RSA keys based on the partner code
            Map<String, Object> key = RSAUtil.generatePartnerKey(partner.getCode());

            // Set the generated keys and the createdBy user ID in the partner entity
            partner.setPublicKey(key.get("public_key").toString());
            partner.setPrivateKey(key.get("private_key").toString());
            partner.setCreatedBy(currentUser.getId());

            // Save the new partner in the database
            Partner savedPartner = partnerService.createPartner(partner);

            // Return successful response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.CREATED_CODE,
                            ApiResponseConstants.CREATED,
                            savedPartner
                    ));

        } catch (Exception e) {
            // Handle any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint to retrieve a paginated list of all partners.
     *
     * @param page The page number (default: 0).
     * @param size The number of items per page (default: 10).
     * @return A paginated list of partners.
     */
    @Operation(
            summary = "Get all partners",
            description = "Retrieves a paginated list of all partners."
    )
    @GetMapping("/list-partner")
    public ResponseEntity<ApiResponse<?>> getAllPartners(
            @RequestHeader(value = HeaderConstants.X_PARTNER_TOKEN, required = false)
            @Parameter(required = true, description = HeaderConstants.X_PARTNER_TOKEN_DESC) String partnerCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Get the authenticated user's username
        String username = userService.getAuthenticatedUsername();

        // Validate that the X-Partner-Token is not missing or empty
        ResponseEntity<ApiResponse<?>> partnerValidationResponse = HeaderValidationUtil.validatePartnerCode(partnerCode, username, partnerService, userService);
        if (partnerValidationResponse != null) {
            return partnerValidationResponse;
        }

        try {
            // Fetch the paginated list of partners
            Page<Partner> partners = partnerService.getAllPartners(page, size);

            // If no partners are found, return a 204 No Content response
            if (partners.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NO_CONTENT_CODE,
                                ApiResponseConstants.NO_PARTNERS_FOUND
                        ));
            }

            // Return the paginated list of partners wrapped in a successful API response
            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.PARTNERS_FETCHED,
                    partners
            ));

        } catch (Exception e) {
            // Handle any exceptions and return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_FETCHING_PARTNERS + e.getMessage()
                    ));
        }
    }
}
