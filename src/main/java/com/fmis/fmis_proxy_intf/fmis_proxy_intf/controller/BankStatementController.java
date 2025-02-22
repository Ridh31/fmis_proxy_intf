package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStatementService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.JsonToXmlUtil;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.RSAUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

/**
 * REST Controller for managing bank statements.
 */
@RestController
@RequestMapping("/api")
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
     * Creates a bank statement for a specific partner.
     *
     * @param bankStatementDTO The bank statement details.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/import-bank-statement")
    public ResponseEntity<ApiResponse<?>> createBankStatement(@Valid @RequestBody BankStatementDTO bankStatementDTO) {
        try {
            // Get the currently authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String publicKey = bankStatementDTO.getPartnerCode();

            // Find partner ID by public key and validate user's association
            Long partnerId = partnerService.findIdByPublicKey(publicKey);
            Optional<User> userOptional = userService.findByPartnerIdAndUsername(partnerId, username);

            // Unauthorized if user does not exist or has an invalid partner code
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                "401",
                                "Unauthorized: Invalid partner code."
                        ));
            }

            User foundUser = userOptional.get();
            String decryptedData = RSAUtil.decrypt(publicKey, foundUser.getPartner().getPrivateKey())
                    .get("decrypt").toString();

            // Forbidden if partner code validation fails
            if (!decryptedData.equals(foundUser.getPartner().getCode())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(
                                "403",
                                "Forbidden: Partner code validation failed."
                        ));
            }

            // Retrieve user ID and set details in the DTO
            Long userId = userService.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            bankStatementDTO.setCreatedBy(userId);
            bankStatementDTO.setPartnerId(partnerId);

            // Convert bank statement data to JSON format
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(bankStatementDTO.getData());

            // Save bank statement if valid data is provided
            if (!data.isEmpty()) {
                bankStatementDTO.setEndpoint("api/import-bank-statement");
                bankStatementDTO.setPayload(data);

                // Convert the payload (JSON string) into XML using the utility method
                String xmlPayload = JsonToXmlUtil.convertJsonToXml(data);

                // Send XML to FMIS

                BankStatement importedBankStatement = bankStatementService.createBankStatement(partnerId, bankStatementDTO);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(
                                "201",
                                "Bank statement saved successfully."
                        ));
            }

            // Return bad request if no valid data is provided
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            "400",
                            "Bad Request: No valid bank statement data provided."
                    ));

        } catch (Exception e) {
            // Handle any exceptions and return internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "Internal Server Error: " + e.getMessage()
                    ));
        }
    }

    /**
     * Fetches a paginated list of active bank statements.
     *
     * @param page The page number (default: 0).
     * @param size The page size (default: 10).
     * @return A Page of BankStatement entities.
     */
    @GetMapping("/list-bank-statement")
    public Page<BankStatement> getBankStatements(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return bankStatementService.getAll(page, size);
    }
}