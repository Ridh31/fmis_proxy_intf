package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.*;
import io.swagger.v3.oas.annotations.Operation;
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
@RestController
@RequestMapping("/api/v1")
public class PartnerController {

    private final PartnerService partnerService;
    private final UserService userService;

    @Value("${application-base-url}")
    private String baseUrl;

    // Constructor injection for the services
    public PartnerController(PartnerService partnerService, UserService userService) {
        this.partnerService = partnerService;
        this.userService = userService;
    }

    /**
     * Endpoint to create a new partner.
     *
     * @param partner The partner details.
     * @return Response entity with the creation status.
     */
    @Operation(
            summary = "Create a new partner",
            description = "Creates a new partner and returns the partner details along with status.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Partner.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "cURL",
                                                    value = ApiRequestExamples.CREATE_PARTNER_CURL
                                            ),
                                            @ExampleObject(
                                                    name = "JavaScript (Fetch API)",
                                                    value = ApiRequestExamples.CREATE_PARTNER_JS_FETCH
                                            ),
                                            @ExampleObject(
                                                    name = "Python (requests)",
                                                    value = ApiRequestExamples.CREATE_PARTNER_PYTHON
                                            ),
                                            @ExampleObject(
                                                    name = "Java (OkHttp)",
                                                    value = ApiRequestExamples.CREATE_PARTNER_JAVA_OKHTTP
                                            ),
                                            @ExampleObject(
                                                    name = "C# (HttpClient)",
                                                    value = ApiRequestExamples.CREATE_PARTNER_CSHARP
                                            ),
                                            @ExampleObject(
                                                    name = "PHP (cURL)",
                                                    value = ApiRequestExamples.CREATE_PARTNER_PHP_CURL
                                            ),
                                            @ExampleObject(
                                                    name = "Node.js (Axios)",
                                                    value = ApiRequestExamples.CREATE_PARTNER_NODEJS
                                            ),
                                            @ExampleObject(
                                                    name = "Ruby (Net::HTTP)",
                                                    value = ApiRequestExamples.CREATE_PARTNER_RUBY
                                            ),
                                            @ExampleObject(
                                                    name = "Go (net/http)",
                                                    value = ApiRequestExamples.CREATE_PARTNER_GO
                                            )
                                    }
                            )
                    }
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Partner created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = ApiResponseExamples.CREATE_PARTNER_SUCCESS
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Bad Request: Validation errors or duplicate partner code",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = ApiResponseExamples.CREATE_PARTNER_BAD_REQUEST
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized: User not logged in",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = ApiResponseExamples.CREATE_PARTNER_UNAUTHORIZED
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Not Found: User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = ApiResponseExamples.CREATE_PARTNER_NOT_FOUND
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error: Unexpected failure",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
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
                    .body(new ApiResponse<>("400", validationErrors));
        }

        try {
            // Retrieve the currently authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                "401",
                                "Unauthorized: You must be logged in to create a partner."
                        ));
            }

            String username = authentication.getName();

            // Get the user ID from the username
            Optional<User> userOptional = userService.findByUsername(username);
            Long userId = userOptional.map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if the partner code already exists in the database
            if (partnerService.findByCode(partner.getCode()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                "400",
                                "Bad Request: Partner code '" + partner.getCode() + "' is already taken."
                        ));
            }

            // Generate RSA keys based on the partner code
            Map<String, Object> key = RSAUtil.generatePartnerKey(partner.getCode());

            // Set the generated keys and the createdBy user ID in the partner entity
            partner.setPublicKey(key.get("public_key").toString());
            partner.setPrivateKey(key.get("private_key").toString());
            partner.setCreatedBy(userId);

            // Save the new partner in the database
            Partner savedPartner = partnerService.createPartner(partner);

            // Return successful response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            "201",
                            "Partner created successfully!",
                            savedPartner
                    ));

        } catch (RuntimeException e) {
            // Handle case where a runtime exception occurs (e.g., user not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            "404",
                            "Not Found: " + e.getMessage()
                    ));

        } catch (Exception e) {
            // Handle any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "Internal Server Error: " + e.getMessage()
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
    public Page<Partner> getAllPartners(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return partnerService.getAllPartners(page, size);
    }
}
