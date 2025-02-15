package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
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
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling Bank Statement creation and management.
 */
@RestController
@RequestMapping("/api/bank-statement")
public class BankStatementController {

    private final PartnerService partnerService;
    private final UserService userService;

    /**
     * Constructor for injecting the required services.
     *
     * @param partnerService Service to handle partner-related logic.
     * @param userService    Service to handle user-related logic.
     */
    @Autowired
    public BankStatementController(PartnerService partnerService, UserService userService) {
        this.partnerService = partnerService;
        this.userService = userService;
    }

    /**
     * Endpoint to create a bank statement for a specific partner.
     *
     * @param bankStatementDTO The bank statement details to be created.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<?>> createBankStatement(@Valid @RequestBody BankStatementDTO bankStatementDTO) {
        String statusCode;
        String message;

        try {
            // Retrieve the Authentication object from the SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String publicKey = bankStatementDTO.getPartnerCode();

            // Decode the partner ID using the partnerCode
            Long partnerId = partnerService.findIdByRsaPublicKey(publicKey);

            // Check if the user exists for the given partner ID and username
            Optional<User> userOptional = userService.findByPartnerIdAndUsername(partnerId, username);

            if (userOptional.isPresent()) {
                User foundUser = userOptional.get();
                String defaultCode = foundUser.getPartner().getCode();
                String privateKey = foundUser.getPartner().getRsaPrivateKey();

                // Decrypt the data using RSAUtil
                Map<String, Object> decodedResult = RSAUtil.decrypt(publicKey, privateKey);
                String decryptedData = decodedResult.get("decrypt").toString();

                // Check if decryption was successful and the decrypted data is valid
                if (decryptedData != null && !decryptedData.isEmpty()) {
                    if (decryptedData.equals(defaultCode)) {
                        statusCode = "200";
                        message = "User Authorized!";
                    } else {
                        return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse<>(
                                        "400",
                                        "Your partner code is invalid. Please use the provided code."
                                ));
                    }
                } else {
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    "500",
                                    "Error during decryption."
                            ));
                }

            } else {
                statusCode = "400";
                message = "Your partner code is invalid. Please use the provided code.";
            }

            // Return the appropriate response based on the user lookup result
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse<>(
                            statusCode,
                            message
                    ));

        } catch (Exception e) {
            // Return an internal server error response if an exception occurs
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            e.getMessage()
                    ));
        }
    }
}
