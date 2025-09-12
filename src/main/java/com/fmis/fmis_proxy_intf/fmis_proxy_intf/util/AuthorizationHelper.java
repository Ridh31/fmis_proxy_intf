package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
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

    /**
     * Constructs an instance of AuthorizationHelper with required services.
     * Uses constructor injection to initialize dependencies for user, role, and partner management.
     *
     * @param userService    service for user-related operations
     * @param roleService    service for role-related operations
     * @param partnerService service for partner-related operations
     */
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
            return unauthorizedResponse(ResponseMessageUtil.unauthorized("perform action"));
        }

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return unauthorizedResponse(ResponseMessageUtil.unauthorizedAccess());
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
            return notFoundResponse(ResponseMessageUtil.notFound("Role"));
        }

        if (user.getRole().getLevel() != 1) {
            return forbiddenResponse(ResponseMessageUtil.forbidden("access the resource"));
        }

        return null;
    }

    /**
     * Validates that the user has Admin or Super Admin access (level 1 or 2).
     *
     * @param user the authenticated user
     * @return ResponseEntity if unauthorized, or null if valid
     */
    public ResponseEntity<ApiResponse<Object>> validateAdmin(User user) {
        if (!roleService.existsById(user.getRole().getId())) {
            return notFoundResponse(ResponseMessageUtil.notFound("Role"));
        }

        int level = user.getRole().getLevel();
        if (level != 1 && level != 2) {
            return forbiddenResponse(ResponseMessageUtil.forbidden("access the resource"));
        }

        return null;
    }

    /**
     * Authenticate user and authorize as Super Admin only.
     *
     * @return the authenticated User if successful,
     *         or ResponseEntity<ApiResponse<?>> if failed.
     */
    public Object authenticateAndAuthorizeSuperAdmin() {
        Object result = getAuthenticatedUserOrResponse();
        if (result instanceof ResponseEntity) {
            return result;
        }
        User user = (User) result;

        ResponseEntity<ApiResponse<Object>> superAdminValidation = validateSuperAdmin(user);
        if (superAdminValidation != null) {
            return superAdminValidation;
        }

        return user;
    }

    /**
     * Authenticate user and authorize as Admin or Super Admin.
     *
     * @return the authenticated User if successful,
     *         or ResponseEntity<ApiResponse<?>> if failed.
     */
    public Object authenticateAndAuthorizeAdmin() {
        Object result = getAuthenticatedUserOrResponse();
        if (result instanceof ResponseEntity) {
            return result;
        }
        User user = (User) result;

        ResponseEntity<ApiResponse<Object>> adminValidation = validateAdmin(user);
        if (adminValidation != null) {
            return adminValidation;
        }

        return user;
    }

    /**
     * Validates that the given partner’s name, identifier, and code are unique.
     *
     * @param partner the partner to validate
     * @return ResponseEntity if duplicate found, or null if valid
     */
    public ResponseEntity<ApiResponse<Object>> validatePartner(Partner partner) {
        if (partnerService.findByName(partner.getName()).isPresent()) {
            return badRequestResponse(ResponseMessageUtil.taken("Partner name"));
        }

        if (partnerService.findByIdentifier(partner.getIdentifier()).isPresent()) {
            return badRequestResponse(ResponseMessageUtil.taken("Partner identifier"));
        }

        if (partnerService.findByCode(partner.getCode()).isPresent()) {
            return badRequestResponse(ResponseMessageUtil.taken("Partner code"));
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

    /**
     * Internal method to authenticate and retrieve the current user, or return an error response.
     *
     * @return the authenticated User object or ResponseEntity if authentication fails
     */
    private Object getAuthenticatedUserOrResponse() {
        Object userValidation = validateUser();
        if (userValidation instanceof ResponseEntity) {
            return userValidation;
        }
        return (User) userValidation;
    }

    /**
     * Constructs a response for an unauthorized request (HTTP 401).
     * This is used when the user is not authenticated or does not have valid credentials.
     *
     * @param message The message to include in the response body.
     * @return A ResponseEntity with status 401 and an ApiResponse containing the provided message.
     */
    private ResponseEntity<ApiResponse<Object>> unauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.unauthorized(),
                        message
                ));
    }

    /**
     * Constructs a response for a not found error (HTTP 404).
     * This is used when a requested resource is not found in the system.
     *
     * @param message The message to include in the response body.
     * @return A ResponseEntity with status 404 and an ApiResponse containing the provided message.
     */
    private ResponseEntity<ApiResponse<Object>> notFoundResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.notFound(),
                        message
                ));
    }

    /**
     * Constructs a response for a forbidden request (HTTP 403).
     * This is used when the user is authenticated but does not have permission to perform the action.
     *
     * @param message The message to include in the response body.
     * @return A ResponseEntity with status 403 and an ApiResponse containing the provided message.
     */
    private ResponseEntity<ApiResponse<Object>> forbiddenResponse(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.forbidden(),
                        message
                ));
    }

    /**
     * Constructs a response for a bad request (HTTP 400).
     * This is used when the client provides invalid input, such as missing or incorrect data.
     *
     * @param message The message to include in the response body.
     * @return A ResponseEntity with status 400 and an ApiResponse containing the provided message.
     */
    private ResponseEntity<ApiResponse<Object>> badRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.taken(),
                        message
                ));
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