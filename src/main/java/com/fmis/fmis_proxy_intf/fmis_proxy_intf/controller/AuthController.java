package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Role;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.UserDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.RoleService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.HeaderValidationUtil;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ValidationErrorUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

/**
 * Controller for handling user authentication and registration operations.
 */
@Tag(
        name = "User Management",
        description = "Endpoints for user registration, authentication, and password management."
)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final PartnerService partnerService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          RoleService roleService,
                          PartnerService partnerService,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.partnerService = partnerService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user if the username is available and both the role and partner exist.
     *
     * @param userDTO User details for registration.
     * @param bindingResult Validation result.
     * @return ResponseEntity containing the registration status.
     */
    @Operation(
            summary = "Register a New User",
            description = "Registers a new user if the username is available and the provided role and partner exist."
    )
    @Hidden
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @Validated @RequestBody UserDTO userDTO,
            BindingResult bindingResult
    ) {
        // Extract validation errors
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return bad request with the errors
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ApiResponseConstants.BAD_REQUEST_CODE, validationErrors));
        }

        try {
            // Check if the username is already taken
            if (userService.findByUsername(userDTO.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.USERNAME_TAKEN
                        ));
            }

            // Check if the email is already taken
            if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.EMAIL_TAKEN
                        ));
            }

            // Validate roleId from DTO and set default if null
            Long roleId = (userDTO.getRoleId() != null) ? userDTO.getRoleId() : 5L;
            Role role = roleService.findById(roleId)
                    .orElseThrow(() -> new RuntimeException(ApiResponseConstants.ROLE_NOT_FOUND));

            // Validate partnerId from DTO
            Long partnerId = userDTO.getPartnerId();
            if (partnerId == null || !partnerService.existsById(partnerId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NOT_FOUND_CODE,
                                ApiResponseConstants.NO_PARTNERS_FOUND
                        ));
            }

            // Fetch the partner entity
            Partner partner = partnerService.findById(partnerId)
                    .orElseThrow(() -> new RuntimeException(ApiResponseConstants.NO_PARTNERS_FOUND));

            // Create User entity and set its properties
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setRole(role);
            user.setPartner(partner);
            user.setEmail(userDTO.getEmail()); // Set email

            // Register the user
            User savedUser = userService.registerUser(user);

            // Return a successful response with the saved user details
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.CREATED_CODE,
                            ApiResponseConstants.CREATED
                    ));

        } catch (RuntimeException e) {
            // Handle unexpected errors and return a concise internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            e.getMessage()
                    ));
        }
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param user User credentials for authentication.
     * @return ResponseEntity containing the login status.
     */
    @Operation(
            summary = "User Verification",
            description = "Authenticates a user using a username and password. Returns a success message if authentication is successful."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Validated
            @RequestHeader(value = HeaderConstants.X_PARTNER_TOKEN, required = false)
            @Parameter(required = true, description = HeaderConstants.X_PARTNER_TOKEN_DESC) String partnerCode,
            @RequestBody User user,
            BindingResult bindingResult) {

        // Extract validation errors using the utility method
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return them in the response
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ApiResponseConstants.BAD_REQUEST_CODE, validationErrors));
        }

        // Get the authenticated user's username
        String username = user.getUsername();

        // Validate that the X-Partner-Token is not missing or empty
        ResponseEntity<ApiResponse<?>> partnerValidationResponse = HeaderValidationUtil.validatePartnerCode(partnerCode, username, partnerService, userService);
        if (partnerValidationResponse != null) {
            return partnerValidationResponse;
        }

        try {
            // Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // Fetch authenticated user from the database
            User loggedInUser = userService.findByUsername(user.getUsername())
                    .orElseThrow(() -> new RuntimeException(ApiResponseConstants.USER_NOT_FOUND));

            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.SUCCESS,
                    loggedInUser
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.UNAUTHORIZED_CODE,
                            ApiResponseConstants.INVALID_CREDENTIALS
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.NOT_FOUND_CODE,
                            ApiResponseConstants.USER_NOT_FOUND
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Resets the password of a user by the Super Admin using the username.
     *
     * @param username The username of the user whose password is to be reset.
     * @param password The new password to set.
     * @return Response with the status of the operation.
     */
    @Operation(
            summary = "Reset User Password",
            description = "Allows a Super Admin (Level 1) to reset a user's password by providing the username and new password."
    )
    @Hidden
    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestParam String username, @RequestParam String password) {
        try {
            // Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName(); // The logged-in user's username
            User currentUser = userService.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException(ApiResponseConstants.USER_NOT_FOUND));

            // Fetch the role of the authenticated user
            Long roleId = currentUser.getRole().getId();
            if (!roleService.existsById(roleId)) {
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
                                ApiResponseConstants.FORBIDDEN_RESET_PASSWORD
                        ));
            }

            // Fetch the target user by username
            User targetUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException(ApiResponseConstants.USER_NOT_FOUND));

            // Hash the new password before saving
            String encodedPassword = passwordEncoder.encode(password);
            targetUser.setPassword(encodedPassword);

            // Save the updated user object
            userService.save(targetUser);

            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.UPDATED_CODE,
                    ApiResponseConstants.PASSWORD_RESET_SUCCESS + username
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }
}