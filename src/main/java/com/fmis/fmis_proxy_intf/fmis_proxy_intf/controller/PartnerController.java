package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.RSAUtil;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ValidationErrorUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
            description = "Creates a new partner and returns the partner details along with status."
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
