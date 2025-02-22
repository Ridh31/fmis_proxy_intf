package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.RoleService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user authentication and registration operations.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final PartnerService partnerService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          RoleService roleService,
                          PartnerService partnerService,
                          AuthenticationManager authenticationManager) {
        this.roleService = roleService;
        this.userService = userService;
        this.partnerService = partnerService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user if the username is available and both the role and partner exist.
     *
     * @param user User details for registration.
     * @return ResponseEntity containing the registration status.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody User user) {
        try {
            // Check if the username is already taken
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                "400",
                                "The username '" + user.getUsername() + "' is already taken. Please choose another username."
                        ));
            }

            // Check if the role exists
            Long roleId = user.getRole().getId();
            if (!roleService.existsById(roleId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                "404",
                                "Role with ID '" + roleId + "' not found."
                        ));
            }

            // Check if the partner exists
            Long partnerId = user.getPartner().getId();
            if (!partnerService.existsById(partnerId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(
                                "404",
                                "Partner with ID '" + partnerId + "' not found."
                        ));
            }

            // Set the partner and register the user
            user.setPartner(user.getPartner());
            User savedUser = userService.registerUser(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            "201",
                            "User '" + savedUser.getUsername() + "' has been successfully registered."
                    ));

        } catch (Exception e) {
            // Handle unexpected errors
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
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody User user) {
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
            // Handle invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            "401",
                            "Authentication failed. Invalid username or password."
                    ));

        } catch (RuntimeException e) {
            // Handle user not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            "404",
                            "User '" + user.getUsername() + "' not found. Please register if you don't have an account."
                    ));

        } catch (Exception e) {
            // Handle other errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "An error occurred during login: " + e.getMessage()
                    ));
        }
    }
}