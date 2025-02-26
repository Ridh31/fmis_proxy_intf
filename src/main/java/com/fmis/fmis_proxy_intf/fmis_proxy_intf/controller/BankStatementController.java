package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStatementService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.FmisService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.JsonToXmlUtil;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.RSAUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BankStatementController {

    // Service dependencies injected via constructor
    private final PartnerService partnerService;
    private final UserService userService;
    private final FmisService fmisService;
    private final BankStatementService bankStatementService;

    @Autowired
    public BankStatementController(PartnerService partnerService,
                                   UserService userService,
                                   FmisService fmisService,
                                   BankStatementService bankStatementService) {
        this.partnerService = partnerService;
        this.userService = userService;
        this.fmisService = fmisService;
        this.bankStatementService = bankStatementService;
    }

    /**
     * Endpoint to create a bank statement after importing the data.
     *
     * @param bankStatementDTO The bank statement data transfer object containing the data.
     * @return ResponseEntity with API response.
     */
    @PostMapping("/import-bank-statement")
    public ResponseEntity<ApiResponse<?>> createBankStatement(@Valid @RequestBody BankStatementDTO bankStatementDTO) {
        try {
            // Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String publicKey = bankStatementDTO.getPartnerCode();

            // Retrieve partner id based on the public key
            Long partnerId = partnerService.findIdByPublicKey(publicKey);
            Optional<User> userOptional = userService.findByPartnerIdAndUsername(partnerId, username);

            // Check if the user is authorized to perform this action
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                "401",
                                "Unauthorized: Invalid partner code."
                        ));
            }

            // Decrypt the partner data and validate the partner code
            User foundUser = userOptional.get();
            String decryptedData = RSAUtil.decrypt(publicKey, foundUser.getPartner().getPrivateKey())
                    .get("decrypt").toString();

            if (!decryptedData.equals(foundUser.getPartner().getCode())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(
                                "403",
                                "Forbidden: Partner code validation failed."
                        ));
            }

            // Set the createdBy and partnerId values
            Long userId = userService.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            bankStatementDTO.setCreatedBy(userId);
            bankStatementDTO.setPartnerId(partnerId);

            // Convert the bank statement data to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(bankStatementDTO.getData());

            // Check if the data is valid
            if (!data.isEmpty()) {
                bankStatementDTO.setEndpoint("api/import-bank-statement");
                bankStatementDTO.setPayload(data);

                // Convert JSON data to XML for FMIS
                String xmlPayload = JsonToXmlUtil.convertJsonToXml(data);

                // Get FMIS configuration
                Optional<FMIS> fmis = fmisService.getFmisUrlById(1L);
                if (fmis.isPresent()) {
                    FMIS fmisConfig = fmis.get();
                    String fmisURL = fmisConfig.getBaseURL() + "/Z_INTF_SO_GET_TEST_GET.v1/get-test/test";
                    String fmisUsername = fmisConfig.getUsername();
                    String fmisPassword = fmisConfig.getPassword();
                    String fmisContentType = fmisConfig.getContentType();

                    // Send XML payload to FMIS and handle response
                    ResponseEntity<String> fmisResponse = fmisService.getXmlFromFmis(fmisURL, fmisUsername, fmisPassword);

                    // Extract and handle FMIS response
                    String fmisResponseBody = fmisResponse.getBody();

                    if (fmisResponse.getStatusCode().is2xxSuccessful()) {

                        // Save the bank statement if FMIS response is successful
                        bankStatementService.createBankStatement(partnerId, bankStatementDTO);
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(new ApiResponse<>(
                                        "201",
                                        "Bank statement saved successfully.",
                                        fmisResponseBody
                                ));
                    } else {
                        // Handle failure in sending data to FMIS
                        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                .body(new ApiResponse<>(
                                        "502",
                                        "Failed to send data to FMIS: " + fmisResponse.getBody()
                                ));
                    }
                } else {
                    // Handle case when FMIS URL is not found
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(
                                    "404",
                                    "Base URL not found"
                            ));
                }
            }

            // Return error if no valid bank statement data is provided
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            "400",
                            "Bad Request: No valid bank statement data provided."
                    ));
        } catch (Exception e) {
            // Handle any server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            "500",
                            "Internal Server Error: " + e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint to list bank statements with pagination.
     *
     * @param page The page number for pagination (default: 0).
     * @param size The size of each page (default: 10).
     * @return A page of bank statements.
     */
    @GetMapping("/list-bank-statement")
    public Page<BankStatement> getBankStatements(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return bankStatementService.getAllBankStatements(page, size);
    }
}