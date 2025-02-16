package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStatementService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.RSAUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling Bank Statement creation and management.
 * Provides endpoints for managing bank statements related to a partner.
 */
@RestController
@RequestMapping("/api/bank-statement")
public class BankStatementController {

    private final PartnerService partnerService;
    private final UserService userService;
    private final BankStatementService bankStatementService;

    /**
     * Constructor injection for services used in the controller.
     *
     * @param partnerService        Service to interact with partner data.
     * @param userService           Service to interact with user data.
     * @param bankStatementService  Service to handle bank statement creation.
     */
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
     * This method validates the partner code, processes the bank statement data,
     * and saves the statement if all conditions are met.
     *
     * @param bankStatementDTO The bank statement details to be created.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createBankStatement(@Valid @RequestBody BankStatementDTO bankStatementDTO) {
        try {
            // Retrieve currently authenticated user's details
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String publicKey = bankStatementDTO.getPartnerCode(); // Partner code to be validated
            BankStatementDTO.BankData bankData = bankStatementDTO.getData(); // Bank data to be saved

            // Validate partner code and retrieve partner ID using the RSA public key
            Long partnerId = partnerService.findIdByRsaPublicKey(publicKey);
            Optional<User> userOptional = userService.findByPartnerIdAndUsername(partnerId, username);

            // If user or partner information is invalid, return an error response
            if (userOptional.isEmpty()) {
                return buildErrorResponse(
                        "400",
                        "Your partner code is invalid. Please use the provided code."
                );
            }

            // Retrieve the found user and partner's default code and private key for further validation
            User foundUser = userOptional.get();
            String defaultCode = foundUser.getPartner().getCode();
            String privateKey = foundUser.getPartner().getRsaPrivateKey();

            // Decrypt and validate the partner code using RSA decryption
            String decryptedData = decryptPartnerCode(publicKey, privateKey);
            if (decryptedData == null || !decryptedData.equals(defaultCode)) {
                return buildErrorResponse(
                        "400",
                        "Your partner code is invalid. Please use the provided code."
                );
            }

            // Set 'createdBy' and 'partnerId' fields for the bank statement
            Long userId = getUserId(username); // Retrieve user ID based on username
            bankStatementDTO.setCreatedBy(userId); // Set the user ID who is creating the bank statement
            bankStatementDTO.setPartnerId(partnerId); // Set the partner ID associated with the bank statement

            // If valid bank data is provided, save the bank statement
            if (isValidBankData(bankData)) {
                BankStatement savedBankStatement = saveBankStatements(userId, partnerId, bankData);
                ApiResponse<BankStatement> response = new ApiResponse<>(
                        "200",
                        "Saved successfully!"
                );
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                // Return error response if the bank data is invalid
                return buildErrorResponse(
                        "400",
                        "Error while saving bank statement data."
                );
            }

        } catch (Exception e) {
            // Handle any unforeseen errors and return a generic server error response
            return buildErrorResponse("500", e.getMessage());
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

    /**
     * Helper method to build error responses.
     * This method creates an error response with the given code and message,
     * which is returned as an HTTP BAD_REQUEST response.
     *
     * @param code Error code to be included in the response.
     * @param message The error message to be included in the response.
     * @return A ResponseEntity containing the error message and code.
     */
    private ResponseEntity<ApiResponse<?>> buildErrorResponse(String code, String message) {
        ApiResponse<String> response = new ApiResponse<>(code, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Helper method to decrypt partner code using RSA.
     * This method uses the partner's public and private keys to decrypt the encrypted partner code.
     *
     * @param publicKey The partner's public key used for decryption.
     * @param privateKey The partner's private key used for decryption.
     * @return The decrypted data (partner code).
     */
    private String decryptPartnerCode(String publicKey, String privateKey) {
        Map<String, Object> decodedResult = RSAUtil.decrypt(publicKey, privateKey);
        return decodedResult.get("decrypt").toString(); // Extract decrypted data
    }

    /**
     * Helper method to retrieve the user ID based on the username.
     * This method checks the database for a matching user by username and returns the user's ID.
     *
     * @param username The username of the authenticated user.
     * @return The user ID if found.
     * @throws RuntimeException If the user is not found.
     */
    private Long getUserId(String username) {
        return userService.findByUsername(username)
                .map(User::getId) // Get the user ID from the User object
                .orElseThrow(() -> new RuntimeException("User not found")); // Throw exception if user not found
    }

    /**
     * Helper method to validate bank data.
     * This method checks if the provided bank data contains the necessary fields for saving.
     *
     * @param bankData The bank data to be validated.
     * @return True if the bank data is valid; otherwise, false.
     */
    private boolean isValidBankData(BankStatementDTO.BankData bankData) {
        return bankData != null && bankData.getCmbBankStmStg() != null; // Check if bank data and stages are present
    }

    /**
     * Helper method to save bank statements.
     * This method iterates through the bank statement stages and saves them using the service.
     *
     * @param userId The ID of the user creating the statement.
     * @param partnerId The ID of the partner related to the bank statement.
     * @param bankData The bank data containing the bank statement stages.
     * @return The saved bank statement object.
     */
    private BankStatement saveBankStatements(Long userId, Long partnerId, BankStatementDTO.BankData bankData) {
        BankStatement savedBankStatement = null;

        // Iterate through each bank statement stage and save it
        for (BankStatementDTO.BankStatement statement : bankData.getCmbBankStmStg()) {
            savedBankStatement = bankStatementService.createBankStatement(userId, partnerId, statement);
        }
        return savedBankStatement;
    }
}
