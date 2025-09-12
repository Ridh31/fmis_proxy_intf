package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * Utility class for validating headers and verifying partner-related information.
 * This includes validating the 'X-Partner-Token' header and verifying the associated partner code.
 */
public class HeaderValidationUtil {

    /**
     * Validates the 'X-Partner-Token' header and the associated partner code.
     * This method ensures that the provided username is valid, exists in the system,
     * and that the provided partner code is correctly decrypted and authorized.
     *
     * @param partnerCode The partner code provided in the 'X-Partner-Token' header.
     * @param username The username associated with the request.
     * @param partnerService Service to handle partner-related operations.
     * @param userService Service to handle user-related operations.
     * @return A ResponseEntity containing an appropriate error message if validation fails, or null if successful.
     */
    public static ResponseEntity<ApiResponse<?>> validatePartnerCode(String partnerCode, String username, PartnerService partnerService, UserService userService) {

        // Validate that the username is not null or empty
        if (username == null || username.trim().isEmpty()) {
            return buildBadRequestResponse(ResponseMessageUtil.invalid("Username"));
        }

        // Check if the user exists by their username
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return buildUnauthorizedResponse(ResponseMessageUtil.unauthorizedAccess());
        }

        // Validate the partnerCode (must not be null or empty)
        if (partnerCode == null || partnerCode.trim().isEmpty()) {
            return buildBadRequestResponse(ResponseMessageUtil.requiredHeader(HeaderConstants.X_PARTNER_TOKEN));
        }

        try {
            // Retrieve the partner ID associated with the provided partner code
            Long partnerId = partnerService.findIdByPublicKey(partnerCode);
            Optional<User> partnerUserOptional = userService.findByPartnerIdAndUsername(partnerId, username);

            // Ensure that the user is authorized to access this partner
            if (partnerUserOptional.isEmpty()) {
                return buildUnauthorizedResponse(ResponseMessageUtil.invalid("Partner token"));
            }

            // Decrypt the partner code and validate it against the expected code
            User foundUser = partnerUserOptional.get();
            String decryptedData = RSAUtil.decrypt(partnerCode, foundUser.getPartner().getPrivateKey())
                    .get("decrypt").toString();

            // Check if the decrypted partner code matches the expected value
            if (!decryptedData.equals(foundUser.getPartner().getCode())) {
                return buildForbiddenResponse(ApiResponseConstants.FORBIDDEN_PARTNER_TOKEN);
            }
        } catch (ResourceNotFoundException ex) {
            return buildNotFoundResponse(ex.getMessage());
        } catch (Exception e) {
            // Catch any exceptions related to decryption or validation errors
            return buildInternalServerErrorResponse(ResponseMessageUtil.internalError("Partner token"));
        }

        // Return null if all validations pass successfully
        return null;
    }

    /*
     * Helper methods for constructing standardized response messages.
     */

    /**
     * Builds a BAD_REQUEST response with a custom message.
     *
     * @param message The error message to be included in the response.
     * @return A ResponseEntity with a 400 Bad Request status and the provided error message.
     */
    private static ResponseEntity<ApiResponse<?>> buildBadRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.requiredHeader(),
                        message
                ));
    }

    /**
     * Builds a NOT_FOUND response with a custom message.
     *
     * @param message The error message to be included in the response.
     * @return A ResponseEntity with a 404 Not Found status and the provided error message.
     */
    private static ResponseEntity<ApiResponse<?>> buildNotFoundResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.notFound(),
                        message
                ));
    }

    /**
     * Builds an UNAUTHORIZED response with a custom message.
     *
     * @param message The error message to be included in the response.
     * @return A ResponseEntity with a 401 Unauthorized status and the provided error message.
     */
    private static ResponseEntity<ApiResponse<?>> buildUnauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.unauthorized(),
                        message
                ));
    }

    /**
     * Builds a FORBIDDEN response with a custom message.
     *
     * @param message The error message to be included in the response.
     * @return A ResponseEntity with a 403 Forbidden status and the provided error message.
     */
    private static ResponseEntity<ApiResponse<?>> buildForbiddenResponse(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.forbidden(),
                        message
                ));
    }

    /**
     * Builds an INTERNAL_SERVER_ERROR response with a custom message.
     *
     * @param message The error message to be included in the response.
     * @return A ResponseEntity with a 500 Internal Server Error status and the provided error message.
     */
    private static ResponseEntity<ApiResponse<?>> buildInternalServerErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.internalError(),
                        message
                ));
    }
}