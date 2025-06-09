package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.RoleService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility component for handling authentication and authorization checks.
 */
@Component
public class AuthorizationHelper {

    private final UserService userService;
    private final RoleService roleService;
    private final PartnerService partnerService;

    @Autowired
    public AuthorizationHelper(UserService userService,
                               RoleService roleService,
                               PartnerService partnerService) {
        this.userService = userService;
        this.roleService = roleService;
        this.partnerService = partnerService;
    }

    /**
     * Validates the currently authenticated user.
     *
     * @return ResponseEntity if invalid, or the authenticated User if valid
     */
    public Object validateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse(ApiResponseConstants.UNAUTHORIZED_LOGIN_REQUIRED);
        }

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return unauthorizedResponse(ApiResponseConstants.UNAUTHORIZED_USER_NOT_FOUND);
        }

        return userOptional.get();
    }

    /**
     * Validates that the user has Super Admin access (level 1 only).
     *
     * @param user the authenticated user
     * @return ResponseEntity if unauthorized, or null if valid
     */
    public ResponseEntity<ApiResponse<Object>> validateSuperAdmin(User user) {
        if (!roleService.existsById(user.getRole().getId())) {
            return notFoundResponse(ApiResponseConstants.ROLE_NOT_FOUND);
        }

        if (user.getRole().getLevel() != 1) {
            return forbiddenResponse(ApiResponseConstants.FORBIDDEN);
        }

        return null;
    }

    /**
     * Validates if the given user is a super admin.
     *
     * @param user the authenticated user
     * @return ResponseEntity if invalid, or null if valid
     */
    public ResponseEntity<ApiResponse<Object>> validateAdmin(User user) {
        if (!roleService.existsById(user.getRole().getId())) {
            return notFoundResponse(ApiResponseConstants.ROLE_NOT_FOUND);
        }

        int level = user.getRole().getLevel();
        if (level != 1 && level != 2) {
            return forbiddenResponse(ApiResponseConstants.FORBIDDEN);
        }

        return null;
    }

    /**
     * Validates that the given partner’s name, identifier, and code are unique.
     *
     * @param partner the partner to validate
     * @return ResponseEntity if duplicate found, or null if valid
     */
    public ResponseEntity<ApiResponse<Object>> validatePartner(Partner partner) {
        if (partnerService.findByName(partner.getName()).isPresent()) {
            return badRequestResponse(ApiResponseConstants.PARTNER_NAME_TAKEN);
        }

        if (partnerService.findByIdentifier(partner.getIdentifier()).isPresent()) {
            return badRequestResponse(ApiResponseConstants.PARTNER_IDENTIFIER_TAKEN);
        }

        if (partnerService.findByCode(partner.getCode()).isPresent()) {
            return badRequestResponse(ApiResponseConstants.PARTNER_CODE_TAKEN);
        }

        return null;
    }

    /**
     * Combined validation: checks user authentication, admin role, and partner uniqueness.
     *
     * @param partner the partner to validate
     * @return User object if valid, or ResponseEntity if any validation fails
     */
    public Object validate(Partner partner) {
        Object userValidation = validateUser();

        if (userValidation instanceof ResponseEntity) {
            return userValidation;
        }

        User user = (User) userValidation;

        ResponseEntity<ApiResponse<Object>> adminCheck = validateAdmin(user);
        if (adminCheck != null) return adminCheck;

        ResponseEntity<ApiResponse<Object>> partnerCheck = validatePartner(partner);
        if (partnerCheck != null) return partnerCheck;

        return user;
    }

    private ResponseEntity<ApiResponse<Object>> unauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ApiResponseConstants.UNAUTHORIZED_CODE, message));
    }

    private ResponseEntity<ApiResponse<Object>> notFoundResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ApiResponseConstants.NOT_FOUND_CODE, message));
    }

    private ResponseEntity<ApiResponse<Object>> forbiddenResponse(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(ApiResponseConstants.FORBIDDEN_CODE, message));
    }

    private ResponseEntity<ApiResponse<Object>> badRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ApiResponseConstants.BAD_REQUEST_CODE, message));
    }

    /**
     * Casts an object to ResponseEntity<ApiResponse<?>> with suppressed warnings.
     * Use only when you're sure the object is the right type.
     */
    @SuppressWarnings("unchecked")
    public static ResponseEntity<ApiResponse<?>> castToApiResponse(Object obj) {
        return (ResponseEntity<ApiResponse<?>>) obj;
    }
}