package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.PartnerDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.AuthorizationHelper.castToApiResponse;

/**
 * Controller for managing partner-related operations.
 */
@Tag(
        name = "Partner Operations",
        description = "Endpoints related to managing partners."
)
@Hidden
@RestController
@RequestMapping
public class PartnerController {

    private final PartnerService partnerService;
    private final UserService userService;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Constructs a new {@code PartnerController} with the required service dependencies.
     * Uses constructor-based dependency injection to ensure all components are initialized.
     *
     * @param partnerService      the service responsible for partner operations
     * @param userService         the service responsible for user authentication and retrieval
     * @param authorizationHelper utility class to handle user authorization and role validation
     */
    public PartnerController(
            PartnerService partnerService,
            UserService userService,
            AuthorizationHelper authorizationHelper
    ) {
        this.partnerService = partnerService;
        this.userService = userService;
        this.authorizationHelper = authorizationHelper;
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
                            description = ApiResponseConstants.PARTNER_NAME_TAKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_BAD_REQUEST,
                                            value = ApiResponseExamples.CREATE_PARTNER_BAD_REQUEST_NAME_TAKEN
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.BAD_REQUEST_CODE_STRING,
                            description = ApiResponseConstants.PARTNER_IDENTIFIER_TAKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_BAD_REQUEST,
                                            value = ApiResponseExamples.CREATE_PARTNER_BAD_REQUEST_IDENTIFIER_TAKEN
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
                                            value = ApiResponseExamples.CREATE_PARTNER_BAD_REQUEST_CODE_TAKEN
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
    public ResponseEntity<ApiResponse<?>> createPartner(
            @Validated @RequestBody Partner partner,
            BindingResult bindingResult) {

        // Extract validation errors
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return bad request with the errors
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            validationErrors
                    ));
        }

        // Perform authentication and authorization checks
        Object validationResult = authorizationHelper.validate(partner);
        if (validationResult instanceof ResponseEntity) {
            return castToApiResponse(validationResult);
        }

        // Retrieve the authenticated user
        User currentUser = (User) validationResult;

        try {
            // Generate RSA keys based on the partner code
            Map<String, Object> generatedKeys = RSAUtil.generatePartnerKey(partner.getCode());

            // Populate partner entity with generated keys and creator info
            partner.setPublicKey(generatedKeys.get("public_key").toString());
            partner.setPrivateKey(generatedKeys.get("private_key").toString());
            partner.setCreatedBy(currentUser.getId());

            // Persist the new partner
            Partner savedPartner = partnerService.createPartner(partner);

            // Return success response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.CREATED_CODE,
                            ApiResponseConstants.CREATED,
                            savedPartner
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
    @Hidden
    @GetMapping("/list-partner")
    public ResponseEntity<ApiResponse<?>> getAllPartners(
            @RequestHeader(value = HeaderConstants.X_PARTNER_TOKEN, required = false)
            @Parameter(required = true, description = HeaderConstants.X_PARTNER_TOKEN_DESC) String partnerCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Get authenticated username
        String username = userService.getAuthenticatedUsername();

        // Validate header: X-Partner-Token
        ResponseEntity<ApiResponse<?>> partnerValidationResponse =
                HeaderValidationUtil.validatePartnerCode(partnerCode, username, partnerService, userService);

        if (partnerValidationResponse != null) {
            return partnerValidationResponse;
        }

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        try {
            // Fetch partners
            Page<Partner> partners = partnerService.getAllPartners(page, size);

            if (partners.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NO_CONTENT_CODE,
                                ApiResponseConstants.NO_PARTNERS_FOUND
                        ));
            }

            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.PARTNERS_FETCHED,
                    partners
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_FETCHING_PARTNERS + e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint to retrieve a paginated list of all bank partners (No token).
     *
     * @param page The page number (default: 0).
     * @param size The number of items per page (default: 10).
     * @return A paginated list of bank partners.
     */
    @Operation(
            summary = "Get all bank partners (No token)",
            description = "Retrieves a paginated list of all bank partners."
    )
    @Hidden
    @GetMapping("/list-bank-partner")
    public ResponseEntity<ApiResponse<?>> getAllBankPartners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        try {
            // Fetch and map filtered bank partners
            Page<Partner> partners = partnerService.getFilteredBankPartners(page, size);
            Page<PartnerDTO> partnerDTO = partners.map(
                    partner -> new PartnerDTO(
                            partner.getId(),
                            partner.getName(),
                            partner.getDescription(),
                            partner.getIdentifier(),
                            partner.getSystemCode()
                    )
            );

            // Handle empty result
            if (partners.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NO_CONTENT_CODE,
                                ApiResponseConstants.NO_PARTNERS_FOUND
                        ));
            }

            // Return success response
            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.PARTNERS_FETCHED,
                    partnerDTO
            ));

        } catch (Exception e) {
            // Handle unexpected error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_FETCHING_PARTNERS + e.getMessage()
                    ));
        }
    }

    /**
     * Updates and saves an existing {@link Partner} entity.
     * Accessible only to Super Admin users.
     *
     * This endpoint validates the input, checks user authentication and role,
     * ensures there are no conflicts with other existing partners (by name, identifier, or code),
     * and updates the partner details including re-generating RSA keys if the code is changed.
     *
     * @param id               the ID of the partner to update
     * @param updatedPartner   the updated {@link Partner} object with new values
     * @param bindingResult    the result of validation checks
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} object with status and data
     */
    @Operation(
            summary = "Update an existing partner",
            description = "Allows a Super Admin to update the details of an existing partner. Only users with role level 1 are authorized to perform this operation."
    )
    @Hidden
    @PutMapping("/update-partner/{id}")
    public ResponseEntity<ApiResponse<?>> updatePartner(
            @PathVariable String id,
            @Validated @RequestBody Partner updatedPartner,
            BindingResult bindingResult) {

        // Extract validation errors
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return bad request with the errors
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            validationErrors
                    ));
        }

        // Validate partner ID format
        long partnerId;
        try {
            partnerId = Long.parseLong(id);
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.BAD_REQUEST_PARTNER_ID_NOT_NUMERIC
                    ));
        }

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        try {
            // Fetch existing partner
            Optional<Partner> existingPartnerOpt = partnerService.findById(partnerId);
            if (existingPartnerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NOT_FOUND_CODE,
                                ApiResponseConstants.NO_PARTNERS_FOUND
                        ));
            }

            Partner existingPartner = existingPartnerOpt.get();

            // Uniqueness validations
            if (!existingPartner.getName().equalsIgnoreCase(updatedPartner.getName())
                    && partnerService.findByName(updatedPartner.getName()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.PARTNER_NAME_TAKEN
                        ));
            }

            if (!existingPartner.getIdentifier().equalsIgnoreCase(updatedPartner.getIdentifier())
                    && partnerService.findByIdentifier(updatedPartner.getIdentifier()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.PARTNER_IDENTIFIER_TAKEN
                        ));
            }

            if (!existingPartner.getSystemCode().equalsIgnoreCase(updatedPartner.getSystemCode())
                    && partnerService.findBySystemCode(updatedPartner.getSystemCode()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.PARTNER_SYSTEM_CODE_TAKEN
                        ));
            }

            if (!existingPartner.getCode().equalsIgnoreCase(updatedPartner.getCode())
                    && partnerService.findByCode(updatedPartner.getCode()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.PARTNER_CODE_TAKEN
                        ));
            }

            // Update partner fields
            boolean codeChanged = !existingPartner.getCode().equalsIgnoreCase(updatedPartner.getCode());
            existingPartner.setName(updatedPartner.getName());
            existingPartner.setIdentifier(updatedPartner.getIdentifier());
            existingPartner.setSystemCode(updatedPartner.getSystemCode());
            existingPartner.setCode(updatedPartner.getCode());
            existingPartner.setDescription(updatedPartner.getDescription());

            // Regenerate keys if code changed
            if (codeChanged) {
                Map<String, Object> key = RSAUtil.generatePartnerKey(updatedPartner.getCode());
                existingPartner.setPublicKey(key.get("public_key").toString());
                existingPartner.setPrivateKey(key.get("private_key").toString());
            }

            // Save updates
            Partner savedPartner = partnerService.updatePartner(existingPartner);

            // Respond success
            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.UPDATED,
                    savedPartner
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
