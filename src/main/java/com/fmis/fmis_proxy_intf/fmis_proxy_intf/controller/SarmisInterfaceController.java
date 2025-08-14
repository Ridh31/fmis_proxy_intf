package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterface;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.InternalCamDigiKeyService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SarmisInterfaceService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SecurityServerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling SARMIS interface interactions, including logging
 * and processing requests for FMIS purchase orders sent to SARMIS.
 */
@Tag(
        name = "SARMIS Interface",
        description = "Endpoints for importing and retrieving data related to SARMIS."
)
@Hidden
@RestController
@RequestMapping
public class SarmisInterfaceController {

    @Value("${application.api.prefix}")
    private String apiPrefix;

    private static final String SARMIS_APP_KEY = "sarmis_interface";
    private static final String FMIS_BATCH_PO_SARMIS = "FMIS_BATCH_PO_SARMIS";
    private static final String LONG_TERM_ASSET_REPORT_SARMIS = "LONG_TERM_ASSET_REPORT_SARMIS";
    private static final String DEPRECIATION_ASSET_REPORT_SARMIS = "DEPRECIATION_ASSET_REPORT_SARMIS";
    private static final String INSTITUTION_CLOSING_LIST_SARMIS = "INSTITUTION_CLOSING_LIST_SARMIS";
    private static final String ASSET_KIND_LIST_SARMIS = "ASSET_KIND_LIST_SARMIS";

    private final SarmisInterfaceService sarmisInterfaceService;
    private final SecurityServerService securityServerService;
    private final InternalCamDigiKeyService internalCamDigiKeyService;
    private final RestTemplate restTemplate;
    private final AuthorizationHelper authorizationHelper;

    /**
     * Constructs a new {@code SarmisController} with the required service dependencies.
     * Initializes services responsible for logging interface activity, retrieving security server configurations,
     * and sending HTTP requests to external APIs.
     *
     * @param sarmisInterfaceService    service for saving and managing SARMIS interface logs
     * @param securityServerService     service for retrieving security server configurations by config key
     * @param internalCamDigiKeyService Service for interacting with CamDigiKey and retrieving authorization tokens.
     * @param restTemplate              HTTP client for sending requests to external systems such as SARMIS
     * @param authorizationHelper       helper for authorization and authentication checks
     */
    @Autowired
    public SarmisInterfaceController(SarmisInterfaceService sarmisInterfaceService,
                                     SecurityServerService securityServerService,
                                     InternalCamDigiKeyService internalCamDigiKeyService,
                                     RestTemplate restTemplate,
                                     AuthorizationHelper authorizationHelper) {
        this.sarmisInterfaceService = sarmisInterfaceService;
        this.securityServerService = securityServerService;
        this.internalCamDigiKeyService = internalCamDigiKeyService;
        this.restTemplate = restTemplate;
        this.authorizationHelper = authorizationHelper;
    }

    /**
     * Accepts FMIS purchase orders in JSON or XML format, injects a generated interface code,
     * logs the request, and forwards the payload to the SARMIS external API.
     *
     * @param requestBody Raw request body content.
     * @param contentType Content-Type header value (e.g., application/json, application/xml).
     * @return Standard API response indicating success or failure.
     */
    @PostMapping("/sarmis/fmis-purchase-orders")
    public ResponseEntity<ApiResponse<?>> fmisPurchaseOrders(
            @RequestBody String requestBody,
            @RequestHeader("Content-Type") String contentType) {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        SarmisInterface sarmisInterface = new SarmisInterface();
        String payload = "";
        String organizationToken = "";

        try {
            // Generate a unique interface code
            String generatedCode = InterfaceCodeGenerator.generate();

            if (contentType.contains("application/json")) {
                JsonNode rootNode = objectMapper.readTree(requestBody);
                if (rootNode instanceof ObjectNode) {
                    ((ObjectNode) rootNode).put("interface_code", generatedCode);
                }
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterface.setPayload(payload);

            } else if (contentType.contains("application/xml")) {
                String json = XmlToJsonUtil.convertSarmisBatchPOXmlToJson(requestBody).toString(2);
                String xml = InterfaceCodeGenerator.injectInterfaceCodeIntoXml(requestBody, generatedCode);
                ObjectNode rootNode = (ObjectNode) objectMapper.readTree(json);

                // Unwrap the "data" node to make it the root
                JsonNode dataNode = rootNode.get("data");
                if (dataNode != null && dataNode.isObject()) {
                    rootNode = (ObjectNode) dataNode;
                }

                // Fix purchase_orders if empty string instead of array
                JsonNode purchaseOrdersNode = rootNode.path("purchase_orders");
                if (purchaseOrdersNode.isTextual() && purchaseOrdersNode.asText().isEmpty()) {
                    rootNode.putArray("purchase_orders");
                }

                // Inject interface_code
                rootNode.put("interface_code", generatedCode);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                payload = payload.replace("[ ]", "[]");

                sarmisInterface.setXml(xml);
                sarmisInterface.setPayload(payload);

            } else {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.UNSUPPORTED_CONTENT_TYPE + contentType
                        ));
            }

            // Set log metadata
            sarmisInterface.setMethod("POST");
            sarmisInterface.setEndpoint(apiPrefix + "/sarmis/fmis-purchase-orders");
            sarmisInterface.setInterfaceCode(generatedCode);

            // Retrieve SecurityServer configuration by config key
            Optional<SecurityServer> optionalConfig = securityServerService.getByConfigKey(FMIS_BATCH_PO_SARMIS);
            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.CONFIG_NOT_FOUND_FOR_KEY + FMIS_BATCH_PO_SARMIS
                        ));
            }

            SecurityServer securityServer = optionalConfig.get();
            String securityServerURL = securityServer.getBaseURL() + securityServer.getEndpoint();

            // Prepare HTTP request headers and entity
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(securityServer.getContentType()));
            headers.set(HeaderConstants.X_ROAD_CLIENT, securityServer.getSubsystem());
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            // CamDigiKey Authorization
            Optional<InternalCamDigiKey> camDigiKey = internalCamDigiKeyService.findByAppKey(SARMIS_APP_KEY);
            if (camDigiKey.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ERROR_NO_CONFIGURATION_FOUND + "(" + SARMIS_APP_KEY + ")"
                        ));
            }

            // Call the external CamDigiKey service
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + apiPrefix + "/portal/camdigikey/organization-token";
            ResponseEntity<String> camDigiKeyResponse = restTemplate.getForEntity(camDigiKeyURL, String.class);

            if (camDigiKeyResponse.getStatusCode() == HttpStatus.OK) {
                try {
                    String body = camDigiKeyResponse.getBody();
                    JsonNode root = objectMapper.readTree(body);

                    int errorCode = root.path("error").asInt();

                    if (errorCode == 0) {
                        JsonNode data = root.path("data");
                        organizationToken = data.path("accessToken").asText();

                        // Set the Authorization header
                        headers.set(HttpHeaders.AUTHORIZATION, organizationToken);

                        // Send request to external SARMIS API
                        try {
                            ResponseEntity<String> sarmisResponse = restTemplate.postForEntity(securityServerURL, entity, String.class);

                            // Log the response from SARMIS
                            sarmisInterface.setResponse(sarmisResponse.getBody());
                            sarmisInterface.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceService.save(sarmisInterface);

                            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                                return ResponseEntity.ok(new ApiResponse<>(
                                        ApiResponseConstants.SUCCESS_CODE,
                                        ApiResponseConstants.SUCCESS,
                                        objectMapper.readTree(sarmisResponse.getBody())
                                ));
                            } else {
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                                ApiResponseConstants.SERVICE_UNAVAILABLE + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromErrorMessage(e.getMessage(), objectMapper);
                            sarmisInterface.setResponse(sarmisError.toString());
                            sarmisInterface.setStatus(false);
                            sarmisInterfaceService.save(sarmisInterface);

                            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                    .body(new ApiResponse<>(
                                            ApiResponseConstants.BAD_GATEWAY_CODE,
                                            ApiResponseConstants.UPSTREAM_SERVICE_ERROR_MESSAGE,
                                            sarmisError
                                    ));
                        }
                    } else {
                        // If the error code isn't 0, it indicates something went wrong on CamDigiKey's side.
                        String message = root.path("message").asText("Unknown error");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(
                                        ApiResponseConstants.BAD_REQUEST_CODE,
                                        message
                                ));
                    }

                } catch (JsonProcessingException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                    ApiResponseConstants.CAMDIGIKEY_JSON_PARSE_ERROR
                            ));
                }
            } else {
                // Handle failed CamDigiKey token retrieval
                return ResponseEntity.status(camDigiKeyResponse.getStatusCode())
                        .body(new ApiResponse<>(
                                camDigiKeyResponse.getStatusCodeValue(),
                                ApiResponseConstants.CAMDIGIKEY_ORG_TOKEN_RETRIEVAL_FAILED
                        ));
            }

        } catch (Exception e) {
            // Catch any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Handles the FMIS purchase order callback, validates the request, logs data,
     * and returns a custom response.
     *
     * Success response: {"error": "0000", "message": "Success", "data": <data>}
     * Error response: {"error": "<status_code>", "message": "<error_message>"}
     *
     * @param requestBody Raw JSON payload from FMIS.
     * @return ResponseEntity with the success or error response.
     */
    @PostMapping("/sarmis/fmis-purchase-orders-callback")
    public ResponseEntity<?> fmisPurchaseOrdersCallback(@RequestBody String requestBody) {
        // Initialize Jackson's ObjectMapper with JavaTime support for date/time deserialization
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Prepare the logging entity
        SarmisInterface sarmisInterface = new SarmisInterface();
        String endpoint = apiPrefix + "/sarmis/fmis-purchase-orders-callback";

        try {
            // Convert the incoming JSON string into a JsonNode for easy parsing and logging
            JsonNode jsonBody = objectMapper.readTree(requestBody);

            // Perform request validation
            try {
                BodyValidationUtil.validateBatchPOCallback(objectMapper.convertValue(jsonBody, Map.class));
            } catch (IllegalArgumentException e) {
                // Validation failed — log the request and error message
                sarmisInterface.setMethod("POST");
                sarmisInterface.setEndpoint(endpoint);
                sarmisInterface.setPayload(jsonBody.toString());
                sarmisInterface.setResponse(String.format(
                        "{\n  \"error\": \"%d\",\n  \"message\": \"%s\"\n}",
                        ApiResponseConstants.BAD_REQUEST_CODE,
                        e.getMessage()
                ));
                sarmisInterface.setStatus(false);

                // Save the log entry to the database
                sarmisInterfaceService.save(sarmisInterface);

                // Return custom error response with HTTP 400
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", String.valueOf(ApiResponseConstants.BAD_REQUEST_CODE));
                errorResponse.put("message", e.getMessage());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Extract the 'interface_code' if it's available in the request
            String interfaceCode = jsonBody.has("interface_code") ? jsonBody.get("interface_code").asText() : null;

            // Log successful validation and request content
            sarmisInterface.setMethod("POST");
            sarmisInterface.setEndpoint(endpoint);
            sarmisInterface.setInterfaceCode(interfaceCode);
            sarmisInterface.setPayload(jsonBody.toString());
            sarmisInterface.setResponse(String.format(
                    "{\n  \"error\": \"%s\",\n  \"message\": \"%s\",\n  \"data\": %s\n}",
                    "0000",
                    ApiResponseConstants.SUCCESS,
                    jsonBody.toString()
            ));
            sarmisInterface.setStatus(true);

            // Persist the success log
            sarmisInterfaceService.save(sarmisInterface);

            // Return custom success response with HTTP 200 and include the data
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("error", "0000");
            successResponse.put("message", ApiResponseConstants.SUCCESS);
            successResponse.put("data", jsonBody);

            return ResponseEntity.ok(successResponse);

        } catch (JsonProcessingException e) {
            // JSON parsing failed — log the raw request and error details
            sarmisInterface.setMethod("POST");
            sarmisInterface.setEndpoint(endpoint);
            sarmisInterface.setPayload(requestBody);
            sarmisInterface.setResponse(String.format(
                    "{\n  \"error\": \"%d\",\n  \"message\": \"%s\"\n}",
                    ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                    e.getMessage()
            ));
            sarmisInterface.setStatus(false);

            // Save the failure log entry
            sarmisInterfaceService.save(sarmisInterface);

            // Return custom error response with HTTP 500
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", String.valueOf(ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE));
            errorResponse.put("message", ApiResponseConstants.ERROR_OCCURRED + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Handles submission of long-term asset report data to SARMIS.
     * Accepts JSON or XML, logs the request, enriches it with authentication, and forwards to SARMIS.
     *
     * @param requestBody the raw JSON or XML input
     * @param contentType the Content-Type of the request
     * @return API response with success or failure info
     */
    @PostMapping("/sarmis/long-term-asset-report")
    public ResponseEntity<ApiResponse<?>> longTermAssetReport(
            @RequestBody String requestBody,
            @RequestHeader("Content-Type") String contentType) {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        SarmisInterface sarmisInterface = new SarmisInterface();
        String payload = "";
        String organizationToken = "";

        try {
            if (contentType.contains("application/json")) {
                JsonNode rootNode = objectMapper.readTree(requestBody);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterface.setPayload(payload);

            } else if (contentType.contains("application/xml")) {
                String json = XmlToJsonUtil.convertXmlToJson(requestBody);
                JsonNode rootNode = objectMapper.readTree(json);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterface.setXml(requestBody);
                sarmisInterface.setPayload(payload);

            } else {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.UNSUPPORTED_CONTENT_TYPE + contentType
                        ));
            }

            // Set log metadata
            sarmisInterface.setMethod("POST");
            sarmisInterface.setEndpoint(apiPrefix + "/sarmis/long-term-asset-report");

            // Retrieve SecurityServer configuration by config key
            Optional<SecurityServer> optionalConfig = securityServerService.getByConfigKey(LONG_TERM_ASSET_REPORT_SARMIS);
            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.CONFIG_NOT_FOUND_FOR_KEY + LONG_TERM_ASSET_REPORT_SARMIS
                        ));
            }

            SecurityServer securityServer = optionalConfig.get();
            String securityServerURL = securityServer.getBaseURL() + securityServer.getEndpoint();

            // Prepare HTTP request headers and entity
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(securityServer.getContentType()));
            headers.set(HeaderConstants.X_ROAD_CLIENT, securityServer.getSubsystem());
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            // CamDigiKey Authorization
            Optional<InternalCamDigiKey> camDigiKey = internalCamDigiKeyService.findByAppKey(SARMIS_APP_KEY);
            if (camDigiKey.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ERROR_NO_CONFIGURATION_FOUND + "(" + SARMIS_APP_KEY + ")"
                        ));
            }

            // Call the external CamDigiKey service
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + apiPrefix + "/portal/camdigikey/organization-token";
            ResponseEntity<String> camDigiKeyResponse = restTemplate.getForEntity(camDigiKeyURL, String.class);

            if (camDigiKeyResponse.getStatusCode() == HttpStatus.OK) {
                try {
                    String body = camDigiKeyResponse.getBody();
                    JsonNode root = objectMapper.readTree(body);

                    int errorCode = root.path("error").asInt();

                    if (errorCode == 0) {
                        JsonNode data = root.path("data");
                        organizationToken = data.path("accessToken").asText();

                        // Set the Authorization header
                        headers.set(HttpHeaders.AUTHORIZATION, organizationToken);

                        // Send request to external SARMIS API
                        try {
                            ResponseEntity<String> sarmisResponse = restTemplate.postForEntity(securityServerURL, entity, String.class);

                            // Log the response from SARMIS
                            sarmisInterface.setResponse(sarmisResponse.getBody());
                            sarmisInterface.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceService.save(sarmisInterface);

                            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                                return ResponseEntity.ok(new ApiResponse<>(
                                        ApiResponseConstants.SUCCESS_CODE,
                                        ApiResponseConstants.SUCCESS,
                                        objectMapper.readTree(sarmisResponse.getBody())
                                ));
                            } else {
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                                ApiResponseConstants.SERVICE_UNAVAILABLE + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromErrorMessage(e.getMessage(), objectMapper);
                            sarmisInterface.setResponse(sarmisError.toString());
                            sarmisInterface.setStatus(false);
                            sarmisInterfaceService.save(sarmisInterface);

                            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                    .body(new ApiResponse<>(
                                            ApiResponseConstants.BAD_GATEWAY_CODE,
                                            ApiResponseConstants.UPSTREAM_SERVICE_ERROR_MESSAGE,
                                            sarmisError
                                    ));
                        }
                    } else {
                        // If the error code isn't 0, it indicates something went wrong on CamDigiKey's side.
                        String message = root.path("message").asText("Unknown error");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(
                                        ApiResponseConstants.BAD_REQUEST_CODE,
                                        message
                                ));
                    }

                } catch (JsonProcessingException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                    ApiResponseConstants.CAMDIGIKEY_JSON_PARSE_ERROR
                            ));
                }
            } else {
                // Handle failed CamDigiKey token retrieval
                return ResponseEntity.status(camDigiKeyResponse.getStatusCode())
                        .body(new ApiResponse<>(
                                camDigiKeyResponse.getStatusCodeValue(),
                                ApiResponseConstants.CAMDIGIKEY_ORG_TOKEN_RETRIEVAL_FAILED
                        ));
            }

        } catch (Exception e) {
            // Catch any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Submits depreciation asset report data to the SARMIS system.
     * <p>
     * Accepts JSON or XML input, converts and logs the payload, adds authentication,
     * and forwards the request to the configured SARMIS endpoint.
     *
     * @param requestBody  the raw JSON or XML payload
     * @param contentType  the content type of the incoming request (application/json or application/xml)
     * @return a standard API response indicating success or failure
     */
    @PostMapping("/sarmis/depreciation-asset-report")
    public ResponseEntity<ApiResponse<?>> depreciationAssetReport(
            @RequestBody String requestBody,
            @RequestHeader("Content-Type") String contentType) {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        SarmisInterface sarmisInterface = new SarmisInterface();
        String payload = "";
        String organizationToken = "";

        try {
            if (contentType.contains("application/json")) {
                JsonNode rootNode = objectMapper.readTree(requestBody);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterface.setPayload(payload);

            } else if (contentType.contains("application/xml")) {
                String json = XmlToJsonUtil.convertXmlToJson(requestBody);
                JsonNode rootNode = objectMapper.readTree(json);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterface.setXml(requestBody);
                sarmisInterface.setPayload(payload);

            } else {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.UNSUPPORTED_CONTENT_TYPE + contentType
                        ));
            }

            // Set log metadata
            sarmisInterface.setMethod("POST");
            sarmisInterface.setEndpoint(apiPrefix + "/sarmis/depreciation-asset-report");

            // Retrieve SecurityServer configuration by config key
            Optional<SecurityServer> optionalConfig = securityServerService.getByConfigKey(DEPRECIATION_ASSET_REPORT_SARMIS);
            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.CONFIG_NOT_FOUND_FOR_KEY + DEPRECIATION_ASSET_REPORT_SARMIS
                        ));
            }

            SecurityServer securityServer = optionalConfig.get();
            String securityServerURL = securityServer.getBaseURL() + securityServer.getEndpoint();

            // Prepare HTTP request headers and entity
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(securityServer.getContentType()));
            headers.set(HeaderConstants.X_ROAD_CLIENT, securityServer.getSubsystem());
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            // CamDigiKey Authorization
            Optional<InternalCamDigiKey> camDigiKey = internalCamDigiKeyService.findByAppKey(SARMIS_APP_KEY);
            if (camDigiKey.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ERROR_NO_CONFIGURATION_FOUND + "(" + SARMIS_APP_KEY + ")"
                        ));
            }

            // Call the external CamDigiKey service
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + apiPrefix + "/portal/camdigikey/organization-token";
            ResponseEntity<String> camDigiKeyResponse = restTemplate.getForEntity(camDigiKeyURL, String.class);

            if (camDigiKeyResponse.getStatusCode() == HttpStatus.OK) {
                try {
                    String body = camDigiKeyResponse.getBody();
                    JsonNode root = objectMapper.readTree(body);

                    int errorCode = root.path("error").asInt();

                    if (errorCode == 0) {
                        JsonNode data = root.path("data");
                        organizationToken = data.path("accessToken").asText();

                        // Set the Authorization header
                        headers.set(HttpHeaders.AUTHORIZATION, organizationToken);

                        // Send request to external SARMIS API
                        try {
                            ResponseEntity<String> sarmisResponse = restTemplate.postForEntity(securityServerURL, entity, String.class);

                            // Log the response from SARMIS
                            sarmisInterface.setResponse(sarmisResponse.getBody());
                            sarmisInterface.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceService.save(sarmisInterface);

                            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                                return ResponseEntity.ok(new ApiResponse<>(
                                        ApiResponseConstants.SUCCESS_CODE,
                                        ApiResponseConstants.SUCCESS,
                                        objectMapper.readTree(sarmisResponse.getBody())
                                ));
                            } else {
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                                ApiResponseConstants.SERVICE_UNAVAILABLE + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromErrorMessage(e.getMessage(), objectMapper);
                            sarmisInterface.setResponse(sarmisError.toString());
                            sarmisInterface.setStatus(false);
                            sarmisInterfaceService.save(sarmisInterface);

                            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                    .body(new ApiResponse<>(
                                            ApiResponseConstants.BAD_GATEWAY_CODE,
                                            ApiResponseConstants.UPSTREAM_SERVICE_ERROR_MESSAGE,
                                            sarmisError
                                    ));
                        }
                    } else {
                        // If the error code isn't 0, it indicates something went wrong on CamDigiKey's side.
                        String message = root.path("message").asText("Unknown error");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(
                                        ApiResponseConstants.BAD_REQUEST_CODE,
                                        message
                                ));
                    }

                } catch (JsonProcessingException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                    ApiResponseConstants.CAMDIGIKEY_JSON_PARSE_ERROR
                            ));
                }
            } else {
                // Handle failed CamDigiKey token retrieval
                return ResponseEntity.status(camDigiKeyResponse.getStatusCode())
                        .body(new ApiResponse<>(
                                camDigiKeyResponse.getStatusCodeValue(),
                                ApiResponseConstants.CAMDIGIKEY_ORG_TOKEN_RETRIEVAL_FAILED
                        ));
            }

        } catch (Exception e) {
            // Catch any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Fetches asset kind data from the SARMIS API using an organization token from CamDigiKey.
     * Supports pagination and search parameters.
     *
     * @param page   page number (default is 1)
     * @param size   number of items per page (default is 10)
     * @param search optional search keyword
     * @return JSON response from SARMIS or error details
     */
    @GetMapping("/sarmis/institution-closing-list")
    public ResponseEntity<ApiResponse<?>> institutionClosingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String organizationToken = "";

        try {
            // Initialize the SARMIS interface log
            SarmisInterface sarmisInterface = new SarmisInterface();
            sarmisInterface.setMethod("GET");
            sarmisInterface.setEndpoint(apiPrefix + "/sarmis/institution-closing-list");

            // Retrieve SARMIS configuration from database
            Optional<SecurityServer> optionalConfig = securityServerService.getByConfigKey(INSTITUTION_CLOSING_LIST_SARMIS);
            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.CONFIG_NOT_FOUND_FOR_KEY + INSTITUTION_CLOSING_LIST_SARMIS
                        ));
            }

            // Extract configuration values
            SecurityServer securityServer = optionalConfig.get();
            String securityServerURL = securityServer.getBaseURL() + securityServer.getEndpoint();

            // Prepare headers for SARMIS API call
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(securityServer.getContentType()));
            headers.set(HeaderConstants.X_ROAD_CLIENT, securityServer.getSubsystem());

            // Fetch CamDigiKey config by app key
            Optional<InternalCamDigiKey> camDigiKey = internalCamDigiKeyService.findByAppKey(SARMIS_APP_KEY);
            if (camDigiKey.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ERROR_NO_CONFIGURATION_FOUND + "(" + SARMIS_APP_KEY + ")"
                        ));
            }

            // Retrieve organization token from CamDigiKey
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + apiPrefix + "/portal/camdigikey/organization-token";
            ResponseEntity<String> camDigiKeyResponse = restTemplate.getForEntity(camDigiKeyURL, String.class);

            if (camDigiKeyResponse.getStatusCode() == HttpStatus.OK) {
                try {
                    // Parse CamDigiKey JSON response
                    String body = camDigiKeyResponse.getBody();
                    JsonNode root = objectMapper.readTree(body);

                    int errorCode = root.path("error").asInt();

                    if (errorCode == 0) {
                        // Extract access token from CamDigiKey response
                        JsonNode data = root.path("data");
                        organizationToken = data.path("accessToken").asText();

                        // Set the Authorization header
                        headers.set(HttpHeaders.AUTHORIZATION, organizationToken);

                        try {
                            // Build full SARMIS URL with query params
                            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                    .fromHttpUrl(securityServer.getBaseURL() + securityServer.getEndpoint())
                                    .queryParam("page", page)
                                    .queryParam("size", size)
                                    .queryParam("search", search);

                            URI uri = uriBuilder.build().encode().toUri();

                            // Send request to SARMIS API
                            HttpEntity<String> sarmisRequest = new HttpEntity<>(headers);
                            ResponseEntity<JsonNode> sarmisResponse = restTemplate.exchange(
                                    uri,
                                    HttpMethod.GET,
                                    sarmisRequest,
                                    JsonNode.class
                            );

                            // Log and persist the response
                            sarmisInterface.setResponse((sarmisResponse.getBody()).toString());
                            sarmisInterface.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceService.save(sarmisInterface);

                            JsonNode sarmisResponseJSON = sarmisResponse.getBody();

                            // Return success response
                            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                                return ResponseEntity.ok(new ApiResponse<>(
                                        ApiResponseConstants.SUCCESS_CODE,
                                        ApiResponseConstants.SUCCESS,
                                        sarmisResponseJSON
                                ));
                            } else {
                                // SARMIS responded but not with 2xx
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                                ApiResponseConstants.SERVICE_UNAVAILABLE + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromErrorMessage(e.getMessage(), objectMapper);
                            sarmisInterface.setResponse(sarmisError.toString());
                            sarmisInterface.setStatus(false);
                            sarmisInterfaceService.save(sarmisInterface);

                            // Failed to connect to SARMIS
                            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                    .body(new ApiResponse<>(
                                            ApiResponseConstants.BAD_GATEWAY_CODE,
                                            ApiResponseConstants.UPSTREAM_SERVICE_ERROR_MESSAGE,
                                            sarmisError
                                    ));
                        }
                    } else {
                        // CamDigiKey returned an error code
                        String message = root.path("message").asText("Unknown error");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(
                                        ApiResponseConstants.BAD_REQUEST_CODE,
                                        message
                                ));
                    }

                } catch (JsonProcessingException e) {
                    // JSON parsing error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                    ApiResponseConstants.CAMDIGIKEY_JSON_PARSE_ERROR
                            ));
                }
            } else {
                // CamDigiKey failed to provide a token
                return ResponseEntity.status(camDigiKeyResponse.getStatusCode())
                        .body(new ApiResponse<>(
                                camDigiKeyResponse.getStatusCodeValue(),
                                ApiResponseConstants.CAMDIGIKEY_ORG_TOKEN_RETRIEVAL_FAILED
                        ));
            }

        } catch (Exception e) {
            // Catch-all for unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Returns the institution closing list in XML format.
     *
     * Converts the JSON response from {@code institutionClosingList()} to XML
     * using the org.json library, without affecting global message converters.
     *
     * @param page   Page number (default: 1)
     * @param size   Page size (default: 10)
     * @param search Optional search term (default: "")
     * @return XML response
     */
    @GetMapping(value = "/sarmis/institution-closing-list/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> institutionClosingListXml(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        try {
            // Call existing JSON endpoint
            ResponseEntity<ApiResponse<?>> jsonResponse = institutionClosingList(page, size, search);
            ApiResponse<?> body = jsonResponse.getBody();

            if (body == null) {
                // Return 204 No Content if no data found
                return ResponseEntity.noContent().build();
            }

            // Convert the response body to a JSON string using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(body);

            // Convert JSON string to JSONObject (org.json)
            JSONObject json = new JSONObject(jsonString);

            // Convert JSONObject to XML string with root element <response>
            String xml = XML.toString(json, "response");

            // Return the XML string with Content-Type: application/xml
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml);

        } catch (Exception e) {
            // Handle any unexpected errors by returning error as XML
            String errorXml = "<error><message>" + e.getMessage() + "</message></error>";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(errorXml);
        }
    }

    /**
     * Calls the SARMIS Institution Closing List API.
     * Retrieves a token from CamDigiKey and uses it to fetch data from SARMIS.
     *
     * @param page   pagination page (default 0)
     * @param size   page size (default 10)
     * @param search optional search string
     * @return response containing SARMIS data or error info
     */
    @GetMapping("/sarmis/asset-kind-list")
    public ResponseEntity<ApiResponse<?>> assetKindList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String organizationToken = "";

        try {
            // Initialize the SARMIS interface log
            SarmisInterface sarmisInterface = new SarmisInterface();
            sarmisInterface.setMethod("GET");
            sarmisInterface.setEndpoint(apiPrefix + "/sarmis/asset-kind-list");

            // Retrieve SARMIS configuration from database
            Optional<SecurityServer> optionalConfig = securityServerService.getByConfigKey(ASSET_KIND_LIST_SARMIS);
            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.CONFIG_NOT_FOUND_FOR_KEY + ASSET_KIND_LIST_SARMIS
                        ));
            }

            // Extract configuration values
            SecurityServer securityServer = optionalConfig.get();
            String securityServerURL = securityServer.getBaseURL() + securityServer.getEndpoint();

            // Prepare headers for SARMIS API call
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(securityServer.getContentType()));
            headers.set(HeaderConstants.X_ROAD_CLIENT, securityServer.getSubsystem());

            // Fetch CamDigiKey config by app key
            Optional<InternalCamDigiKey> camDigiKey = internalCamDigiKeyService.findByAppKey(SARMIS_APP_KEY);
            if (camDigiKey.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.ERROR_NO_CONFIGURATION_FOUND + "(" + SARMIS_APP_KEY + ")"
                        ));
            }

            // Retrieve organization token from CamDigiKey
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + apiPrefix + "/portal/camdigikey/organization-token";
            ResponseEntity<String> camDigiKeyResponse = restTemplate.getForEntity(camDigiKeyURL, String.class);

            if (camDigiKeyResponse.getStatusCode() == HttpStatus.OK) {
                try {
                    // Parse CamDigiKey JSON response
                    String body = camDigiKeyResponse.getBody();
                    JsonNode root = objectMapper.readTree(body);

                    int errorCode = root.path("error").asInt();

                    if (errorCode == 0) {
                        // Extract access token from CamDigiKey response
                        JsonNode data = root.path("data");
                        organizationToken = data.path("accessToken").asText();

                        // Set the Authorization header
                        headers.set(HttpHeaders.AUTHORIZATION, organizationToken);

                        try {
                            // Build full SARMIS URL with query params
                            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                    .fromHttpUrl(securityServer.getBaseURL() + securityServer.getEndpoint())
                                    .queryParam("page", page)
                                    .queryParam("size", size)
                                    .queryParam("search", search);

                            URI uri = uriBuilder.build().encode().toUri();

                            // Send request to SARMIS API
                            HttpEntity<String> sarmisRequest = new HttpEntity<>(headers);
                            ResponseEntity<JsonNode> sarmisResponse = restTemplate.exchange(
                                    uri,
                                    HttpMethod.GET,
                                    sarmisRequest,
                                    JsonNode.class
                            );

                            // Log and persist the response
                            sarmisInterface.setResponse((sarmisResponse.getBody()).toString());
                            sarmisInterface.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceService.save(sarmisInterface);

                            JsonNode assetKindList = sarmisResponse.getBody();

                            // Return success response
                            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                                return ResponseEntity.ok(new ApiResponse<>(
                                        ApiResponseConstants.SUCCESS_CODE,
                                        ApiResponseConstants.SUCCESS,
                                        assetKindList
                                ));
                            } else {
                                // SARMIS responded but not with 2xx
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                                ApiResponseConstants.SERVICE_UNAVAILABLE + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromErrorMessage(e.getMessage(), objectMapper);
                            sarmisInterface.setResponse(sarmisError.toString());
                            sarmisInterface.setStatus(false);
                            sarmisInterfaceService.save(sarmisInterface);

                            // Failed to connect to SARMIS
                            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                    .body(new ApiResponse<>(
                                            ApiResponseConstants.BAD_GATEWAY_CODE,
                                            ApiResponseConstants.UPSTREAM_SERVICE_ERROR_MESSAGE,
                                            sarmisError
                                    ));
                        }
                    } else {
                        // CamDigiKey returned an error code
                        String message = root.path("message").asText("Unknown error");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(
                                        ApiResponseConstants.BAD_REQUEST_CODE,
                                        message
                                ));
                    }

                } catch (JsonProcessingException e) {
                    // JSON parsing error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                    ApiResponseConstants.CAMDIGIKEY_JSON_PARSE_ERROR
                            ));
                }
            } else {
                // CamDigiKey failed to provide a token
                return ResponseEntity.status(camDigiKeyResponse.getStatusCode())
                        .body(new ApiResponse<>(
                                camDigiKeyResponse.getStatusCodeValue(),
                                ApiResponseConstants.CAMDIGIKEY_ORG_TOKEN_RETRIEVAL_FAILED
                        ));
            }

        } catch (Exception e) {
            // Catch-all for unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_OCCURRED + e.getMessage()
                    ));
        }
    }

    /**
     * Retrieves a paginated list of SARMIS Interface records with optional filters.
     * Accessible to Admins only. Validates status input and handles authorization.
     *
     * @param endpoint         optional filter by endpoint
     * @param interfaceCode    optional filter by interface code
     * @param purchaseOrderId  optional filter by purchase order ID
     * @param actionDate       optional filter by action date (dd-MM-yyyy)
     * @param status           optional filter by status ("true" or "false")
     * @param page             pagination page (default 0)
     * @param size             page size (default 10)
     * @return response with filtered SARMIS data or error details
     */
    @Operation(
            summary = "Get SARMIS Interface",
            description = "Retrieves a paginated list of SARMIS Interface. Accessible only to Admins."
    )
    @Hidden
    @GetMapping("/list-sarmis-interface")
    public ResponseEntity<ApiResponse<?>> listSarmisInterface(
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) String interfaceCode,
            @RequestParam(required = false) String purchaseOrderId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate actionDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Authenticate user and verify required role permissions
        Object authorization = authorizationHelper.authenticateAndAuthorizeAdmin();
        if (authorization instanceof ResponseEntity) {
            return AuthorizationHelper.castToApiResponse(authorization);
        }

        // Check status value if exist
        Boolean statusValue = null;
        if (status != null && !status.trim().isEmpty()) {
            if ("true".equalsIgnoreCase(status.trim())) {
                statusValue = true;
            } else if ("false".equalsIgnoreCase(status.trim())) {
                statusValue = false;
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.BAD_REQUEST_CODE,
                                ApiResponseConstants.BAD_REQUEST_INVALID_STATUS_VALUE
                        ));
            }
        }

        try {
            // Fetch paginated SARMIS interface list
            Page<SarmisInterface> sarmisInterface = sarmisInterfaceService.getFilteredSarmisInterface(
                    page, size, endpoint, interfaceCode, purchaseOrderId, actionDate, statusValue);

            return ResponseEntity.ok(new ApiResponse<>(
                    ApiResponseConstants.SUCCESS_CODE,
                    ApiResponseConstants.SUCCESS,
                    sarmisInterface
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                            ApiResponseConstants.ERROR_FETCHING_DATA + e.getMessage()
                    ));
        }
    }
}