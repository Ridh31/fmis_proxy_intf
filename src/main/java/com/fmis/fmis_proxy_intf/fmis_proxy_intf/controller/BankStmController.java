package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStm;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling Bank Statement creation and management.
 */
@RestController
@RequestMapping("/api/bankstm")
public class BankStmController {

    private final PartnerService partnerService;
    private final UserService userService;

    // Constructor for injecting services
    @Autowired
    public BankStmController(PartnerService partnerService, UserService userService) {
        this.partnerService = partnerService;
        this.userService = userService;
    }

    /**
     * Endpoint to create a bank statement.
     *
     * @param hintCode the encoded hint code to identify the partner
     * @param bankStm the bank statement details to be created
     * @return a response message indicating success or failure
     */
    @PostMapping("/create/{hintCode}")
    public String createBankStm(@PathVariable String hintCode, @RequestBody BankStm bankStm) {

        String response;

        try {
            // Retrieve the Authentication object from the SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Get the username of the authenticated user
            String username = authentication.getName();

            // Decode the partner ID using the hintCode
            Long partnerId = partnerService.findByBase64(hintCode);

            // Check if the user exists for the given partner ID and username
            Optional<User> userOpt = userService.findByPartnerIdAndUsername(partnerId, username);

            if (userOpt.isPresent()) {
                User foundUser = userOpt.get();
                response = "User found!";

            } else {
                response = "User not found!";
            }

        } catch (Exception e) {
            response = "Error: " + e.getMessage();
        }

        // Return the response message
        return response;
    }
}
