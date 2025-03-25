package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
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
            return buildBadRequestResponse(ApiResponseConstants.ERROR_USERNAME_MISSING_OR_EMPTY);
        }

        // Validate if username exists
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return buildUnauthorizedResponse(ApiResponseConstants.UNAUTHORIZED_USER_NOT_FOUND);
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
                return buildUnauthorizedResponse(ApiResponseConstants.INVALID_PARTNER_TOKEN);
            }

            // Decrypt the partner code and validate it
            User foundUser = partnerUserOptional.get();
            String decryptedData = RSAUtil.decrypt(partnerCode, foundUser.getPartner().getPrivateKey())
                    .get("decrypt").toString();

            if (!decryptedData.equals(foundUser.getPartner().getCode())) {
                return buildForbiddenResponse(ApiResponseConstants.FORBIDDEN_PARTNER_TOKEN);
            }
        } catch (Exception e) {
            // Catching all exceptions related to decryption or validation errors
            return buildInternalServerErrorResponse(ApiResponseConstants.ERROR_OCCURRED + e.getMessage());
        }

        // Return null if all validations pass
        return null;
    }

    // Helper methods for response construction
    private static ResponseEntity<ApiResponse<?>> buildBadRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ApiResponseConstants.BAD_REQUEST_CODE, message));
    }

    private static ResponseEntity<ApiResponse<?>> buildUnauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ApiResponseConstants.UNAUTHORIZED_CODE, message));
    }

    private static ResponseEntity<ApiResponse<?>> buildForbiddenResponse(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(ApiResponseConstants.FORBIDDEN_CODE, message));
    }

    private static ResponseEntity<ApiResponse<?>> buildInternalServerErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE, message));
    }
}
