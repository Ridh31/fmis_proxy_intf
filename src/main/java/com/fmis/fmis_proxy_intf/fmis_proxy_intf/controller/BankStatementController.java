package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
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
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Node;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = {
                                            @ExampleObject(
                                                    name = ApiRequestExamples.CURL,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_CURL
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.JAVASCRIPT,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_JS_FETCH
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.PYTHON,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_PYTHON
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.JAVA_HTTPURLCONNECTION,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_JAVA_HTTPURLCONNECTION
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.CSHARP,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_CSHARP_HTTPCLIENT
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.PHP_CURL,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_PHP_CURL
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.NODEJS,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_NODEJS
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.RUBY,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_RUBY
                                            ),
                                            @ExampleObject(
                                                    name = ApiRequestExamples.GO,
                                                    value = ApiRequestExamples.IMPORT_BANK_STATEMENT_GO
                                            )
                                    }
                            )
                    }
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.CREATED_CODE_STRING,
                            description = ApiResponseConstants.CREATED,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_SUCCESS,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_SUCCESS
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.BAD_REQUEST_CODE_STRING,
                            description = ApiResponseConstants.BAD_REQUEST_MISSING_PARTNER_TOKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = {
                                            @ExampleObject(
                                                    name = ApiResponseConstants.RESPONSE_TYPE_MISSING_PARTNER_TOKEN_HEADER,
                                                    value = ApiResponseExamples.IMPORT_BANK_STATEMENT_MISSING_PARTNER_TOKEN
                                            ),
                                            @ExampleObject(
                                                    name = ApiResponseConstants.RESPONSE_TYPE_VALIDATION_ERROR,
                                                    value = ApiResponseExamples.IMPORT_BANK_STATEMENT_VALIDATION_ERROR
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.UNAUTHORIZED_CODE_STRING,
                            description = ApiResponseConstants.INVALID_PARTNER_TOKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_UNAUTHORIZED,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_UNAUTHORIZED
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.FORBIDDEN_CODE_STRING,
                            description = ApiResponseConstants.FORBIDDEN_PARTNER_TOKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_FORBIDDEN,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_FORBIDDEN
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.NOT_FOUND_CODE_STRING,
                            description = ApiResponseConstants.NO_FMIS_CONFIG_FOUND,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_NOT_FOUND,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_NOT_FOUND
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.BAD_GATEWAY_CODE_STRING,
                            description = ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_FMIS_FAILURE,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_FMIS_FAILURE
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE_STRING,
                            description = ApiResponseConstants.INTERNAL_SERVER_ERROR,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_SERVER_ERROR,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_SERVER_ERROR
                                    )
                            )
                    )
            }
    )
    @PostMapping("/import-bank-statement")
    public ResponseEntity<ApiResponse<?>> createBankStatement(
            @Validated
            @RequestHeader(value = HeaderConstants.X_PARTNER_TOKEN, required = false)
            @Parameter(required = true, description = HeaderConstants.X_PARTNER_TOKEN_DESC) String partnerCode,
            @RequestBody BankStatementDTO bankStatementDTO,
            String endpoint,
            BindingResult bindingResult) {

        // Check if endpoint is null or empty, and set a default value if necessary
        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = "api/import-bank-statement";
        }

        // Extract validation errors using the utility method
        Map<String, String> validationErrors = ValidationErrorUtils.extractValidationErrors(bindingResult);

        // If there are validation errors, return them in the response
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ApiResponseConstants.BAD_REQUEST_CODE, validationErrors));
        }

        // Get the authenticated user's username
        String username = userService.getAuthenticatedUsername();

        // Validate that the X-Partner-Token is not missing or empty
        ResponseEntity<ApiResponse<?>> partnerValidationResponse = HeaderValidationUtil.validatePartnerCode(partnerCode, username, partnerService, userService);
        if (partnerValidationResponse != null) {
            return partnerValidationResponse;
        }

        try {
            // Set the createdBy and partnerId values
            Long userId = userService.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException(ApiResponseConstants.USER_NOT_FOUND));

            bankStatementDTO.setCreatedBy(userId);

            Long partnerId = partnerService.findIdByPublicKey(partnerCode);
            bankStatementDTO.setPartnerId(partnerId);

            Optional<Partner> partner = partnerService.findById(partnerId);

            // Use ifPresent to safely access the value if it's present
            partner.ifPresent(p -> {
                String identifier = p.getIdentifier();

                // Add CMB_BANK_CODE to all arrays in the data
                if (bankStatementDTO.getData() != null) {
                    for (Map.Entry<String, Object> entry : bankStatementDTO.getData().entrySet()) {

                        // Check if the value is a list
                        if (entry.getValue() instanceof List<?>) {
                            List<Map<String, Object>> list = (List<Map<String, Object>>) entry.getValue();

                            // Add CMB_BANK_CODE to each object in the list
                            for (Map<String, Object> item : list) {
                                item.put("CMB_BANK_CODE", identifier);
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
            });

            // Convert the bank statement data to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(bankStatementDTO.getData());

            // Check if the data is valid
            if (!data.isEmpty()) {
                bankStatementDTO.setMethod("POST");
                bankStatementDTO.setEndpoint(endpoint);
                bankStatementDTO.setPayload(data);

                // Convert JSON data to XML for FMIS
                String xmlPayload = JsonToXmlUtil.convertBankStatementJsonToXml(data);
                bankStatementDTO.setXml(xmlPayload);

                // Get FMIS configuration
                Optional<FMIS> fmis = fmisService.getFmisUrlById(1L);
                if (fmis.isPresent()) {
                    FMIS fmisConfig = fmis.get();
                    String fmisURL = fmisConfig.getBaseURL() + "/BANKSTM_STG.v1/";
                    String fmisUsername = fmisConfig.getUsername();
                    String fmisPassword = fmisConfig.getPassword();
                    String fmisContentType = fmisConfig.getContentType();

                    // Send XML payload to FMIS and handle response
                    ResponseEntity<String> fmisResponse = fmisService.sendXmlToFmis(fmisURL, fmisUsername, fmisPassword, xmlPayload);
                    String fmisResponseBody = fmisResponse.getBody();

                    int responseCode;
                    String responseMessage;

                    if (fmisResponse.getStatusCode().is2xxSuccessful()) {

                        // Parse the FMIS XML response manually
                        Map<String, Object> fmisResponseData = new HashMap<>();

                        if (fmisResponseBody != null) {
                            try {
                                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder builder = factory.newDocumentBuilder();

                                Document document = builder.parse(
                                        new ByteArrayInputStream(fmisResponseBody.getBytes(StandardCharsets.UTF_8))
                                );
                                Element root = document.getDocumentElement();

                                // Extract status code from <status code="200"/>
                                Node statusNode = root.getElementsByTagName("status").item(0);
                                if (statusNode instanceof Element) {
                                    String code = ((Element) statusNode).getAttribute("code");
                                    fmisResponseData.put("status", Integer.parseInt(code));

                                    // Save the bank statement if FMIS response is successful
                                    if (code.equals("200") || code.equals("201")) {
                                        bankStatementService.createBankStatement(partnerId, bankStatementDTO);
                                    }
                                }

                                // Extract message from <message>...</message>
                                Node messageNode = root.getElementsByTagName("message").item(0);
                                if (messageNode != null) {
                                    responseMessage = messageNode.getTextContent();
                                    fmisResponseData.put("message", responseMessage);
                                } else {
                                    responseMessage = ApiResponseConstants.ERROR_FMIS_RESPONSE_EMPTY;
                                }
                                responseCode = ApiResponseConstants.CREATED_CODE;

                            } catch (Exception e) {
                                responseMessage = ApiResponseConstants.ERROR_FMIS_RESPONSE_PARSE;
                                fmisResponseData.put("message", responseMessage);
                                responseCode = ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE;
                            }
                        } else {
                            responseMessage = ApiResponseConstants.ERROR_FMIS_RESPONSE_EMPTY;
                            fmisResponseData.put("message", responseMessage);
                            responseCode = ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE;
                        }

                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(new ApiResponse<>(
                                        responseCode,
                                        responseMessage
                                ));
                    } else {
                        fmisResponseBody = Optional.ofNullable(fmisResponseBody).orElse("");

                        // Define the regex pattern to capture only the domain
                        Pattern pattern = Pattern.compile("(https?://[a-zA-Z0-9.-]+)");
                        Matcher matcher = pattern.matcher(fmisResponseBody);

                        // Extract the first match if found
                        String responseURL = matcher.find() ? matcher.group(1) : "";
                        String responseHost = !responseURL.isEmpty() ? " (" + responseURL + ")" : "";

                        // Handle failure in sending data to FMIS
                        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                .body(new ApiResponse<>(
                                        ApiResponseConstants.BAD_GATEWAY_CODE,
                                        ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT + responseHost
                                ));
                    }
                } else {
                    // Handle case when FMIS URL is not found
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(
                                    ApiResponseConstants.NOT_FOUND_CODE,
                                    ApiResponseConstants.BASE_URL_NOT_FOUND
                            ));
                }
            }

            // Return error if no valid bank statement data is provided
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.NO_VALID_BANK_STATEMENT
                    ));

        } catch (Exception e) {
            // Handle any server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Endpoint to upload a JSON file and process it as a bank statement.
     * This accepts a multipart file and parses it into a BankStatementDTO
     * before delegating to the main `createBankStatement` method.
     *
     * @param partnerCode The X-Partner-Token header used for authentication and partner identification.
     * @param file        The JSON file uploaded as multipart/form-data.
     * @return ResponseEntity containing the API response with success or error message.
     */
    @Operation(
            summary = "Upload Bank Statement",
            description = "Uploads a JSON file containing bank statement data. " +
                          "The file is validated, parsed into a DTO, and then processed as if submitted through the import API. " +
                          "The data is forwarded to FMIS after successful validation.",
            parameters = {
                @Parameter(
                        name = HeaderConstants.X_PARTNER_TOKEN,
                        in = ParameterIn.HEADER,
                        required = true,
                        description = HeaderConstants.X_PARTNER_TOKEN_DESC
                )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.CREATED_CODE_STRING,
                            description = ApiResponseConstants.CREATED,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_SUCCESS,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_SUCCESS
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.BAD_REQUEST_CODE_STRING,
                            description = ApiResponseConstants.BAD_REQUEST_MISSING_PARTNER_TOKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = {
                                            @ExampleObject(
                                                    name = ApiResponseConstants.RESPONSE_TYPE_MISSING_PARTNER_TOKEN_HEADER,
                                                    value = ApiResponseExamples.IMPORT_BANK_STATEMENT_MISSING_PARTNER_TOKEN
                                            ),
                                            @ExampleObject(
                                                    name = ApiResponseConstants.RESPONSE_TYPE_VALIDATION_ERROR,
                                                    value = ApiResponseExamples.IMPORT_BANK_STATEMENT_VALIDATION_ERROR
                                            ),
                                            @ExampleObject(
                                                    name = ApiResponseConstants.RESPONSE_TYPE_FILE_MISSING_OR_EMPTY,
                                                    value = ApiResponseExamples.UPLOAD_BANK_STATEMENT_FILE_MISSING_OR_EMPTY_ERROR
                                            ),
                                            @ExampleObject(
                                                    name = ApiResponseConstants.RESPONSE_TYPE_INVALID_FILE_TYPE,
                                                    value = ApiResponseExamples.UPLOAD_BANK_STATEMENT_INVALID_FILE_TYPE_ERROR
                                            ),
                                            @ExampleObject(
                                                    name = ApiResponseConstants.RESPONSE_TYPE_FAILED_TO_PARSE_JSON,
                                                    value = ApiResponseExamples.UPLOAD_BANK_STATEMENT_FAILED_TO_PARSE_JSON_ERROR
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.UNAUTHORIZED_CODE_STRING,
                            description = ApiResponseConstants.INVALID_PARTNER_TOKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_UNAUTHORIZED,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_UNAUTHORIZED
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.FORBIDDEN_CODE_STRING,
                            description = ApiResponseConstants.FORBIDDEN_PARTNER_TOKEN,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_FORBIDDEN,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_FORBIDDEN
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.NOT_FOUND_CODE_STRING,
                            description = ApiResponseConstants.NO_FMIS_CONFIG_FOUND,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_NOT_FOUND,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_NOT_FOUND
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.BAD_GATEWAY_CODE_STRING,
                            description = ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_FMIS_FAILURE,
                                            value = ApiResponseExamples.IMPORT_BANK_STATEMENT_FMIS_FAILURE
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE_STRING,
                            description = ApiResponseConstants.INTERNAL_SERVER_ERROR,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    examples = {
                                            @ExampleObject(
                                                    name = ApiResponseConstants.RESPONSE_TYPE_SERVER_ERROR,
                                                    value = ApiResponseExamples.IMPORT_BANK_STATEMENT_SERVER_ERROR
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/upload-bank-statement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> uploadBankStatement(
            @RequestHeader(value = HeaderConstants.X_PARTNER_TOKEN, required = false)
            @Parameter(required = true, description = HeaderConstants.X_PARTNER_TOKEN_DESC) String partnerCode,
            @RequestParam("file") MultipartFile file) {

        // Validate if the file is missing or empty
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.BAD_REQUEST_FILE_MISSING_OR_EMPTY
                    ));
        }

        // Check if the file content type is application/json
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.BAD_REQUEST_INVALID_FILE_TYPE
                    ));
        }

        try {
            // Parse the uploaded file into a JsonNode
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(file.getInputStream());

            // Convert JsonNode to BankStatementDTO
            BankStatementDTO dto = mapper.treeToValue(jsonNode, BankStatementDTO.class);

            // Create a BindingResult for validation
            BindingResult bindingResult = new BeanPropertyBindingResult(dto, "bankStatementDTO");

            // Call the original createBankStatement method with the parsed DTO and binding result
            return createBankStatement(partnerCode, dto, "api/upload-bank-statement", bindingResult);

        } catch (IOException e) {
            // Handle parsing error (e.g., malformed JSON)
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            ApiResponseConstants.BAD_REQUEST_CODE,
                            ApiResponseConstants.BAD_REQUEST_FAILED_TO_PARSE_JSON
                    ));
        } catch (Exception e) {
            // Handle unexpected internal errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
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
    public ResponseEntity<ApiResponse<?>> getBankStatements(
            @RequestHeader(value = HeaderConstants.X_PARTNER_TOKEN, required = false)
            @Parameter(required = true, description = HeaderConstants.X_PARTNER_TOKEN_DESC) String partnerCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Get the authenticated user's username
        String username = userService.getAuthenticatedUsername();

        // Validate that the X-Partner-Token is not missing or empty
        ResponseEntity<ApiResponse<?>> partnerValidationResponse = HeaderValidationUtil.validatePartnerCode(partnerCode, username, partnerService, userService);
        if (partnerValidationResponse != null) {
            return partnerValidationResponse;
        }

        try {
            // Fetch the paginated list of bank statements from the service
            Page<BankStatement> bankStatements = bankStatementService.getAllBankStatements(page, size);

            // If no data is found, return a 204 No Content response
            if (bankStatements.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.NO_CONTENT_CODE,
                                ApiResponseConstants.NO_BANK_STATEMENTS_FOUND
                        ));
            }

            // ObjectMapper for JSON conversion
            ObjectMapper objectMapper = new ObjectMapper();

            // Map each BankStatement entity to a BankStatementDTO
            Page<BankStatementDTO> bankStatementDTOPage = bankStatements.map(bankStatement -> {
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
                    payloadJson = objectMapper.createObjectNode(); // If invalid payload, set as empty JSON object
                }

                // Check if the payloadJson is not null and is a valid JSON object
                if (payloadJson != null && payloadJson.isObject()) {
                    Map<String, Object> dataMap = objectMapper.convertValue(payloadJson, Map.class);
                    dto.setData(dataMap);
                } else {
                    // If payload is invalid or not an object, set an empty map
                    Map<String, Object> emptyMap = objectMapper.convertValue(objectMapper.createObjectNode(), Map.class);
                    dto.setData(emptyMap);
                }

                return dto;
            });

            // Return the paginated list of BankStatementDTO wrapped in a successful API response
            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.BANK_STATEMENTS_FETCHED,
                    bankStatementDTOPage
            ));

        } catch (Exception e) {
            // Handle any exceptions and return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_FETCHING_BANK_STATEMENTS + e.getMessage()
                    ));
        }
    }
}