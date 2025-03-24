package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStatementService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.FmisService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(
        name = "Bank Statement",
        description = "Endpoints for importing and retrieving bank statements."
)
@RestController
@RequestMapping("/api/v1")
public class BankStatementController {

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
    @Operation(
            summary = "Import Bank Statement",
            description = "Imports bank statement data after validation and sends it to FMIS.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "cURL",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_CURL
                                            ),
                                            @ExampleObject(
                                                    name = "JavaScript (Fetch API)",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_JS_FETCH
                                            ),
                                            @ExampleObject(
                                                    name = "Python (requests)",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_PYTHON
                                            ),
                                            @ExampleObject(
                                                    name = "Java (HttpURLConnection)",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_JAVA_HTTPURLCONNECTION
                                            ),
                                            @ExampleObject(
                                                    name = "C# (HttpClient)",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_CSHARP_HTTPCLIENT
                                            ),
                                            @ExampleObject(
                                                    name = "PHP (cURL)",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_PHP_CURL
                                            ),
                                            @ExampleObject(
                                                    name = "Node.js (Axios)",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_NODEJS
                                            ),
                                            @ExampleObject(
                                                    name = "Ruby (Net::HTTP)",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_RUBY
                                            ),
                                            @ExampleObject(
                                                    name = "Go (net/http)",
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_GO
                                            )
                                    }
                            )
                    }
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Bank statement saved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Success Response",
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_SUCCESS
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Missing 'X-Partner-Token' or validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing X-Partner-Token",
                                                    value = ApiResponseExamples.IMPORT_BANK_STATEMENT_MISSING_PARTNER_TOKEN
                                            ),
                                            @ExampleObject(
                                                    name = "Validation Error",
                                                    value = ApiResponseExamples.IMPORT_BANK_STATEMENT_VALIDATION_ERROR
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Invalid partner code",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Unauthorized Response",
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_UNAUTHORIZED
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Partner code validation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Forbidden Response",
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_FORBIDDEN
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Not Found - FMIS base URL not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Not Found Response",
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_NOT_FOUND
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "502",
                            description = "Bad Gateway - Failed to send data to FMIS",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "FMIS Failure Response",
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_FMIS_FAILURE
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - Unexpected failure",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Server Error Response",
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_SERVER_ERROR
                                    )
                            )
                    )
            }
    )
    @PostMapping("/import-bank-statement")
    public ResponseEntity<ApiResponse<?>> createBankStatement(
            @Validated
            @RequestHeader(value = "X-Partner-Token", required = false)
            @Parameter(required = true, description = "X-Partner-Token") String partnerCode,
            @RequestBody BankStatementDTO bankStatementDTO,
            BindingResult bindingResult) {

        // Validate that the X-Partner-Token is not missing or empty
        if (partnerCode == null || partnerCode.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            "400",
                            "Bad Request: 'X-Partner-Token' header cannot be missing or empty."
                    ));
        }

        // Extract validation errors using the utility method
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return them in the response
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("400", validationErrors));
        }

        try {
            // Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve partner id based on the public key
            Long partnerId = partnerService.findIdByPublicKey(partnerCode);
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
            String decryptedData = RSAUtil.decrypt(partnerCode, foundUser.getPartner().getPrivateKey())
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

            Optional<Partner> partner = partnerService.findById(partnerId);

            // Add CMB_BANK_CODE to all arrays in the data
            if (bankStatementDTO.getData() != null) {
                for (Map.Entry<String, Object> entry : bankStatementDTO.getData().entrySet()) {

                    // Check if the value is a list
                    if (entry.getValue() instanceof List<?>) {
                        List<Map<String, Object>> list = (List<Map<String, Object>>) entry.getValue();

                        // Add CMB_BANK_CODE to each object in the list
                        for (Map<String, Object> item : list) {
                            item.put("CMB_BANK_CODE", partnerCode);
                        }
                    } else {
                        // Handle non-list entries by adding CMB_BANK_CODE to the object as null
                        if (entry.getValue() instanceof Map) {
                            Map<String, Object> mapValue = (Map<String, Object>) entry.getValue();
                            mapValue.put("CMB_BANK_CODE", null);
                        }
                    }
                }
            }

            // Convert the bank statement data to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(bankStatementDTO.getData());

            // Check if the data is valid
            if (!data.isEmpty()) {
                bankStatementDTO.setMethod("POST");
                bankStatementDTO.setEndpoint("api/import-bank-statement");
                bankStatementDTO.setPayload(data);

                // Convert JSON data to XML for FMIS
                String xmlPayload = JsonToXmlUtil.convertJsonToXml(data);
                bankStatementDTO.setXml(xmlPayload);

                // Get FMIS configuration
                Optional<FMIS> fmis = fmisService.getFmisUrlById(1L);
                if (fmis.isPresent()) {
                    FMIS fmisConfig = fmis.get();
                    String fmisURL = fmisConfig.getBaseURL() + "/INTF_CMB_BANKSTM_STG.v1/";
                    String fmisUsername = fmisConfig.getUsername();
                    String fmisPassword = fmisConfig.getPassword();
                    String fmisContentType = fmisConfig.getContentType();

                    // Send XML payload to FMIS and handle response
                    ResponseEntity<String> fmisResponse = fmisService.sendXmlToFmis(fmisURL, fmisUsername, fmisPassword, xmlPayload);
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
                                        "Failed to send data to FMIS: " + fmisResponseBody
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
     * Endpoint to fetch paginated bank statements.
     *
     * This method retrieves a list of bank statements from the database,
     * maps them to BankStatementDTOs, and returns them in a paginated format.
     * The data includes all relevant fields from the bank statement entity,
     * and it handles parsing and conversion of payload data (JSON) to a Map for the API response.
     *
     * @param page The page number to retrieve (default is 0).
     * @param size The number of items per page (default is 10).
     * @return A paginated list of BankStatementDTOs containing the bank statement data.
     */
    @Operation(
            summary = "Get Bank Statements",
            description = "Retrieves a paginated list of bank statements from the database."
    )
    @GetMapping("/list-bank-statement")
    public Page<BankStatementDTO> getBankStatements(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        // Fetch the paginated list of bank statements from the service
        Page<BankStatement> bankStatements = bankStatementService.getAllBankStatements(page, size);

        // ObjectMapper for JSON conversion
        ObjectMapper objectMapper = new ObjectMapper();

        // Mapping each BankStatement entity to a BankStatementDTO
        return bankStatements.map(bankStatement -> {

            // Initialize a new BankStatementDTO to hold the mapped data
            BankStatementDTO dto = new BankStatementDTO();

            // Copy fields from the BankStatement entity to the DTO
            dto.setId(bankStatement.getId());
            dto.setPartnerCode(bankStatement.getPartner().getCode());
            dto.setPartnerId(bankStatement.getPartner().getId());
            dto.setMethod(bankStatement.getMethod());
            dto.setEndpoint(bankStatement.getEndpoint());
            dto.setXml(bankStatement.getXml());
            dto.setCreatedBy(bankStatement.getCreatedBy());
            dto.setCreatedDate(bankStatement.getCreatedDate());
            dto.setStatus(bankStatement.getStatus());
            dto.setIsDeleted(bankStatement.getIsDeleted());

            // Convert payload string to JsonNode for API response
            JsonNode payloadJson = null;

            try {
                payloadJson = objectMapper.readTree(bankStatement.getPayload());

            } catch (Exception e) {
                payloadJson = objectMapper.createObjectNode();
            }

            // Check if the payloadJson is not null and is a valid JSON object
            if (payloadJson != null && payloadJson.isObject()) {
                Map<String, Object> dataMap = objectMapper.convertValue(payloadJson, Map.class);
                dto.setData(dataMap);

            } else {
                // If the payload is invalid or not an object, set an empty map as the data
                Map<String, Object> emptyMap = objectMapper.convertValue(objectMapper.createObjectNode(), Map.class);
                dto.setData(emptyMap);
            }

            // Return the populated DTO
            return dto;
        });
    }
}