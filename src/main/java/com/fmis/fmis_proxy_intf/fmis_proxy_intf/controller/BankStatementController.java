package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStatementService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.RSAUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.List;

/**
 * REST Controller to manage bank statements.
 * Provides endpoints for creating and retrieving bank statements.
 */
@RestController
@RequestMapping("/api/bank-statement")
public class BankStatementController {

    private final PartnerService partnerService;
    private final UserService userService;
    private final BankStatementService bankStatementService;

    @Autowired
    public BankStatementController(PartnerService partnerService,
                                   UserService userService,
                                   BankStatementService bankStatementService) {
        this.partnerService = partnerService;
        this.userService = userService;
        this.bankStatementService = bankStatementService;
    }

    /**
     * Endpoint to create a bank statement for a specific partner.
     * This method processes the provided bank statement details, verifies user authentication,
     * validates the partner code using RSA decryption, and saves the bank statement if all conditions are met.
     *
     * @param bankStatementDTO The bank statement details provided by the user.
     * @return ResponseEntity containing success or error message.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createBankStatement(@Valid @RequestBody BankStatementDTO bankStatementDTO) {
        try {
            // Get the currently authenticated user from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve the partner's public key and bank data from the request
            String publicKey = bankStatementDTO.getPartnerCode();
            BankStatementDTO.BankData bankData = bankStatementDTO.getData();

            // Find the partner ID using the provided RSA public key
            Long partnerId = partnerService.findIdByRsaPublicKey(publicKey);

            // Find the user associated with the given partner and username
            Optional<User> userOptional = userService.findByPartnerIdAndUsername(partnerId, username);

            // If the user is not found, return an unauthorized response
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                "401",
                                "Unauthorized: Invalid partner code."
                        ));
            }

            // Retrieve the user details
            User foundUser = userOptional.get();

            // Decrypt the partner data using RSA decryption to verify the partner code
            String decryptedData = RSAUtil.decrypt(publicKey, foundUser.getPartner().getRsaPrivateKey())
                    .get("decrypt").toString();

            // If the decrypted partner code doesn't match, return a forbidden response
            if (!decryptedData.equals(foundUser.getPartner().getCode())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(
                                "403",
                                "Forbidden: Partner code validation failed."
                        ));
            }

            // Retrieve the user ID from the username
            Long userId = userService.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Set the user ID and partner ID in the bank statement DTO
            bankStatementDTO.setCreatedBy(userId);
            bankStatementDTO.setPartnerId(partnerId);

            // Extract the list of bank statements
            List<BankStatementDTO.BankStatement> bankStatements = bankStatementDTO.getData().getCmbBankStmStg();

            // Wrap in the same structure as received
            BankStatementDTO.BankData responseData = new BankStatementDTO.BankData();
            responseData.setCmbBankStmStg(bankStatements);

            // Create an ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Support LocalDateTime serialization
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Use ISO format for dates

            // Convert responseData to JSON
            String jsonResponse = objectMapper.writeValueAsString(responseData);

            // If the bank data contains valid statements, save them
            if (bankData != null && bankData.getCmbBankStmStg() != null) {
                for (BankStatementDTO.BankStatement statement : bankData.getCmbBankStmStg()) {
                    bankStatementService.createBankStatement(userId, partnerId, statement);
                }

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(
                                "201",
                                "Bank statement saved successfully."
                                // responseData
                        ));
            }

            // If no valid bank statement data is provided, return a bad request response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            "400",
                            "Bad Request: No valid bank statement data provided."
                    ));
        } catch (Exception e) {
            // Handle unexpected errors and return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "Internal Server Error: " + e.getMessage()
                    ));
        }
    }

    /**
     * API endpoint to fetch a paginated list of active bank statements.
     *
     * @param page The page number (default: 0).
     * @param size The page size (default: 10).
     * @return A Page of BankStatement entities.
     */
    @GetMapping("/list")
    public Page<BankStatement> getBankStatements(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return bankStatementService.getAll(page, size);
    }
}