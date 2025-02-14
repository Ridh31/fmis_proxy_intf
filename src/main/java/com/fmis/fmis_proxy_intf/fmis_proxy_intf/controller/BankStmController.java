package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStm;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling Bank Statement creation and management.
 */
@RestController
@RequestMapping("/api/bank-statement")
public class BankStmController {

    private final PartnerService partnerService;
    private final UserService userService;

    /**
     * Constructor for injecting the required services.
     *
     * @param partnerService Service to handle partner-related logic.
     * @param userService    Service to handle user-related logic.
     */
    @Autowired
    public BankStmController(PartnerService partnerService, UserService userService) {
        this.partnerService = partnerService;
        this.userService = userService;
    }

    /**
     * Endpoint to create a bank statement for a specific partner.
     *
     * @param partnerCode The encoded hint code used to identify the partner.
     * @param bankStm  The bank statement details to be created.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/create/{partnerCode}")
    public ResponseEntity<ApiResponse<?>> createBankStm(
            @PathVariable String partnerCode,
            @RequestBody BankStm bankStm) {

        String statusCode;
        String message;

        try {
            // Retrieve the Authentication object from the SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Decode the partner ID using the partnerCode
            Long partnerId = partnerService.findByBase64(partnerCode);

            // Check if the user exists for the given partner ID and username
            Optional<User> userOpt = userService.findByPartnerIdAndUsername(partnerId, username);

            if (userOpt.isPresent()) {
                User foundUser = userOpt.get();
                statusCode = "200";
                message = "User Authorized!";
            } else {
                statusCode = "400";
                message = "Your partner code is invalid. Please use a provided code.";
            }

            // Return the appropriate response based on the user lookup result
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse<>(statusCode, message));

        } catch (Exception e) {
            // Return an internal server error response if an exception occurs
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("500", e.getMessage()));
        }
    }
}
