package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Role;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.RoleService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ValidationErrorUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;
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
     * @param user User details for registration.
     * @param bindingResult Validation result.
     * @return ResponseEntity containing the registration status.
     */
    @Operation(
            summary = "Register a New User",
            description = "Registers a new user if the username is available and the provided role and partner exist."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Validated @RequestBody User user, BindingResult bindingResult) {

        // Extract validation errors using the utility method
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return them in the response
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("400", validationErrors));
        }

        try {
            // Check if the username is already taken
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                "400",
                                "The username '" + user.getUsername() + "' is already taken. Please choose another username."
                        ));
            }

            // Set default role if not provided
            Long roleId = (user.getRole() != null && user.getRole().getId() != null) ? user.getRole().getId() : 5L;

            if (user.getRole() == null) {
                Role defaultRole = roleService.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Default role not found."));
                user.setRole(defaultRole);
            }

            // Validate if role exists
            if (!roleService.existsById(roleId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                "404",
                                "Role with ID '" + roleId + "' not found."
                        ));
            }

            // Check if the partner exists
            Long partnerId = (user.getPartner() != null && user.getPartner().getId() != null)
                    ? user.getPartner().getId()
                    : null;

            if (partnerId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                "400",
                                "Partner is not provided. Please provide a valid partner."
                        ));
            }

            if (!partnerService.existsById(partnerId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                "404",
                                "Partner with ID '" + partnerId + "' not found."
                        ));
            }

            // Register the user
            user.setPartner(user.getPartner());
            User savedUser = userService.registerUser(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            "201",
                            "User '" + savedUser.getUsername() + "' has been successfully registered."
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "An unexpected error occurred during registration: " + e.getMessage()
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
    public ResponseEntity<ApiResponse<?>> login(@Validated @RequestBody User user, BindingResult bindingResult) {

        // Extract validation errors using the utility method
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return them in the response
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("400", validationErrors));
        }

        try {
            // Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // Fetch authenticated user from the database
            User loggedInUser = userService.findByUsername(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(new ApiResponse<>(
                    "200",
                    "Login successful. Welcome, " + loggedInUser.getUsername() + "."
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            "401",
                            "Authentication failed. Invalid username or password."
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            "404",
                            "User '" + user.getUsername() + "' not found. Please register if you don't have an account."
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "An error occurred during login: " + e.getMessage()
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
    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestParam String username, @RequestParam String password) {
        try {
            // Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName(); // The logged-in user's username
            User currentUser = userService.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

            // Fetch the role of the authenticated user
            Long roleId = currentUser.getRole().getId();
            if (!roleService.existsById(roleId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                "404",
                                "Role not found"
                        ));
            }

            // Check if the authenticated user is a Super Admin (level 1)
            if (currentUser.getRole().getLevel() != 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(
                                "403",
                                "You do not have permission to reset passwords."
                        ));
            }

            // Fetch the target user by username
            User targetUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Hash the new password before saving
            String encodedPassword = passwordEncoder.encode(password);
            targetUser.setPassword(encodedPassword);

            // Save the updated user object
            userService.save(targetUser);

            return ResponseEntity.ok(new ApiResponse<>(
                    "200",
                    "Password reset successfully for user: " + username
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "An error occurred: " + e.getMessage()
                    ));
        }
    }
}