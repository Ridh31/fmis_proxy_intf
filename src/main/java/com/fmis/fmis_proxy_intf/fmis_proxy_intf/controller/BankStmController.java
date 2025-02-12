package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public ResponseEntity<ApiResponse<String>> createBankStm(@PathVariable String hintCode, @RequestBody BankStm bankStm) {
        String response;

        try {
            // Retrieve the Authentication object from the SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(new ApiResponse<>("200", "User found!"));
            System.out.println("TESTSETESTES============ " + jsonString);  // Log the serialized object

            // Return response with HttpStatus.OK (200)
            ApiResponse<String> apiResponse = new ApiResponse<>("200", response);
            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            // Log the error and return it in the response
            e.printStackTrace();
            ApiResponse<String> errorResponse = new ApiResponse<>("500", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
