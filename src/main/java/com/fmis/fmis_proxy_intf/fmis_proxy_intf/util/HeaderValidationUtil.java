package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class HeaderValidationUtil {

    // Method to validate the 'X-Partner-Token' header and check the partner code
    public static ResponseEntity<ApiResponse<?>> validatePartnerCode(String partnerCode, String username, PartnerService partnerService, UserService userService) {

        // Validate that the username is valid first
        if (username == null || username.trim().isEmpty()) {
            return buildBadRequestResponse("Bad Request: 'Username' cannot be missing or empty.");
        }

        // Validate if username exists
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return buildUnauthorizedResponse("Unauthorized: The username provided was not found.");
        }

        // Now proceed to validate the partnerCode
        if (partnerCode == null || partnerCode.trim().isEmpty()) {
            return buildBadRequestResponse("Bad Request: '" + HeaderConstants.X_PARTNER_TOKEN + "' header cannot be missing or empty.");
        }

        try {
            // Retrieve partner ID and validate user authorization
            Long partnerId = partnerService.findIdByPublicKey(partnerCode);
            Optional<User> partnerUserOptional = userService.findByPartnerIdAndUsername(partnerId, username);

            if (partnerUserOptional.isEmpty()) {
                return buildUnauthorizedResponse("Unauthorized: Invalid partner code.");
            }

            // Decrypt the partner code and validate it
            User foundUser = partnerUserOptional.get();
            String decryptedData = RSAUtil.decrypt(partnerCode, foundUser.getPartner().getPrivateKey())
                    .get("decrypt").toString();

            if (!decryptedData.equals(foundUser.getPartner().getCode())) {
                return buildForbiddenResponse("Forbidden: Partner code validation failed.");
            }
        } catch (Exception e) {
            // Catching all exceptions related to decryption or validation errors
            return buildInternalServerErrorResponse("Internal Server Error: " + e.getMessage());
        }

        // Return null if all validations pass
        return null;
    }

    // Helper methods for response construction
    private static ResponseEntity<ApiResponse<?>> buildBadRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("400", message));
    }

    private static ResponseEntity<ApiResponse<?>> buildUnauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("401", message));
    }

    private static ResponseEntity<ApiResponse<?>> buildForbiddenResponse(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>("403", message));
    }

    private static ResponseEntity<ApiResponse<?>> buildInternalServerErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("500", message));
    }
}
