package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.RSAUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing partner-related operations.
 */
@RestController
@RequestMapping("/api/partner")
public class PartnerController {

    private final PartnerService partnerService;
    private final UserService userService;

    public PartnerController(PartnerService partnerService, UserService userService) {
        this.partnerService = partnerService;
        this.userService = userService;
    }

    /**
     * Endpoint to create a partner.
     *
     * @param partner The partner details.
     * @return Response entity with the creation status.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createPartner(@RequestBody Partner partner) {
        try {
            // Retrieve authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                "401",
                                "Unauthorized: You must be logged in to create a partner."
                        ));
            }

            String username = authentication.getName();

            // Get the user ID from username
            Optional<User> user = userService.findByUsername(username);
            Long userId = user.map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if partner code already exists
            if (partnerService.findByCode(partner.getCode()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                "400",
                                "Bad Request: Partner code '" + partner.getCode() + "' is already taken. Please use another code."
                        ));
            }

            // Generate key from partner code
            Map<String, Object> key = RSAUtil.generatePartnerKey(partner.getCode());

            // Set generated key values in the partner entity
            partner.setBase64(key.get("base64").toString());
            partner.setSha256(key.get("sha256").toString());
            partner.setRsaPublicKey(key.get("public_key").toString());
            partner.setRsaPrivateKey(key.get("private_key").toString());
            partner.setCreatedBy(userId);

            // Save the partner
            Partner savedPartner = partnerService.createPartner(partner);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            "201",
                            "Partner created successfully!",
                            savedPartner
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            "404",
                            "Not Found: " + e.getMessage()
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "Internal Server Error: " + e.getMessage()
                    ));
        }
    }

    /**
     * Retrieves a paginated list of partners.
     *
     * @param page The page number (default: 0).
     * @param size The number of items per page (default: 10).
     * @return A paginated list of partners.
     */
    @GetMapping("/list")
    public Page<Partner> getPartners(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return partnerService.getAll(page, size);
    }
}
