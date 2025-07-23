package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterfaceLog;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.InternalCamDigiKeyService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SarmisInterfaceLogService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SecurityServerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ExceptionUtils;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.InterfaceCodeGenerator;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.XmlToJsonUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

/**
 * Controller for handling SARMIS interface interactions, including logging
 * and processing requests for FMIS purchase orders sent to SARMIS.
 */
@Tag(
        name = "SARMIS Interface",
        description = "Endpoints for importing and retrieving data related to SARMIS."
)
@RestController
@RequestMapping("/api/v1")
public class SarmisInterfaceLogController {

    private static final String SARMIS_APP_KEY = "sarmis_interface";
    private static final String FMIS_BATCH_PO_SARMIS = "FMIS_BATCH_PO_SARMIS";
    private static final String LONG_TERM_ASSET_REPORT_SARMIS = "LONG_TERM_ASSET_REPORT_SARMIS";
    private static final String DEPRECIATION_ASSET_REPORT_SARMIS = "DEPRECIATION_ASSET_REPORT_SARMIS";
    private static final String INSTITUTION_CLOSING_LIST_SARMIS = "INSTITUTION_CLOSING_LIST_SARMIS";
    private static final String ASSET_KIND_LIST_SARMIS = "ASSET_KIND_LIST_SARMIS";

    private final SarmisInterfaceLogService sarmisInterfaceLogService;
    private final SecurityServerService securityServerService;
    private final InternalCamDigiKeyService internalCamDigiKeyService;
    private final RestTemplate restTemplate;

    /**
     * Constructs a new {@code SarmisController} with the required service dependencies.
     * Initializes services responsible for logging interface activity, retrieving security server configurations,
     * and sending HTTP requests to external APIs.
     *
     * @param sarmisInterfaceLogService service for saving and managing SARMIS interface logs
     * @param securityServerService     service for retrieving security server configurations by config key
     * @param internalCamDigiKeyService Service for interacting with CamDigiKey and retrieving authorization tokens.
     * @param restTemplate              HTTP client for sending requests to external systems such as SARMIS
     */
    @Autowired
    public SarmisInterfaceLogController(SarmisInterfaceLogService sarmisInterfaceLogService,
                                        SecurityServerService securityServerService,
                                        InternalCamDigiKeyService internalCamDigiKeyService,
                                        RestTemplate restTemplate) {
        this.sarmisInterfaceLogService = sarmisInterfaceLogService;
        this.securityServerService = securityServerService;
        this.internalCamDigiKeyService = internalCamDigiKeyService;
        this.restTemplate = restTemplate;
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
        SarmisInterfaceLog sarmisInterfaceLog = new SarmisInterfaceLog();
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
                sarmisInterfaceLog.setPayload(payload);

            } else if (contentType.contains("application/xml")) {
                String json = XmlToJsonUtil.convertXmlToJson(requestBody);
                String updatedXml = InterfaceCodeGenerator.injectInterfaceCodeIntoXml(requestBody, generatedCode);
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode instanceof ObjectNode) {
                    ((ObjectNode) rootNode).put("interface_code", generatedCode);
                }
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterfaceLog.setXml(updatedXml);
                sarmisInterfaceLog.setPayload(payload);

            } else {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.UNSUPPORTED_CONTENT_TYPE + contentType
                        ));
            }

            // Set log metadata
            sarmisInterfaceLog.setMethod("POST");
            sarmisInterfaceLog.setEndpoint("/api/v1/sarmis/fmis-purchase-orders");
            sarmisInterfaceLog.setInterfaceCode(generatedCode);

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
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + "/api/v1/portal/camdigikey/organization-token";
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
                        headers.set(HeaderConstants.AUTHORIZATION_HEADER, organizationToken);

                        // Send request to external SARMIS API
                        try {
                            ResponseEntity<String> sarmisResponse = restTemplate.postForEntity(securityServerURL, entity, String.class);

                            // Log the response from SARMIS
                            sarmisInterfaceLog.setResponse(sarmisResponse.getBody());
                            sarmisInterfaceLog.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

                            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                                return ResponseEntity.ok(new ApiResponse<>(
                                        ApiResponseConstants.SUCCESS_CODE,
                                        ApiResponseConstants.SUCCESS
                                ));
                            } else {
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                                ApiResponseConstants.SERVICE_UNAVAILABLE + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromMessage(e.getMessage(), objectMapper);
                            sarmisInterfaceLog.setResponse(sarmisError.toString());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

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
        SarmisInterfaceLog sarmisInterfaceLog = new SarmisInterfaceLog();
        String payload = "";
        String organizationToken = "";

        try {
            if (contentType.contains("application/json")) {
                JsonNode rootNode = objectMapper.readTree(requestBody);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterfaceLog.setPayload(payload);

            } else if (contentType.contains("application/xml")) {
                String json = XmlToJsonUtil.convertXmlToJson(requestBody);
                JsonNode rootNode = objectMapper.readTree(json);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterfaceLog.setXml(requestBody);
                sarmisInterfaceLog.setPayload(payload);

            } else {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.UNSUPPORTED_CONTENT_TYPE + contentType
                        ));
            }

            // Set log metadata
            sarmisInterfaceLog.setMethod("POST");
            sarmisInterfaceLog.setEndpoint("/api/v1/sarmis/long-term-asset-report");

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
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + "/api/v1/portal/camdigikey/organization-token";
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
                        headers.set(HeaderConstants.AUTHORIZATION_HEADER, organizationToken);

                        // Send request to external SARMIS API
                        try {
                            ResponseEntity<String> sarmisResponse = restTemplate.postForEntity(securityServerURL, entity, String.class);

                            // Log the response from SARMIS
                            sarmisInterfaceLog.setResponse(sarmisResponse.getBody());
                            sarmisInterfaceLog.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

                            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                                return ResponseEntity.ok(new ApiResponse<>(
                                        ApiResponseConstants.SUCCESS_CODE,
                                        ApiResponseConstants.SUCCESS
                                ));
                            } else {
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                                ApiResponseConstants.SERVICE_UNAVAILABLE + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromMessage(e.getMessage(), objectMapper);
                            sarmisInterfaceLog.setResponse(sarmisError.toString());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

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
        SarmisInterfaceLog sarmisInterfaceLog = new SarmisInterfaceLog();
        String payload = "";
        String organizationToken = "";

        try {
            if (contentType.contains("application/json")) {
                JsonNode rootNode = objectMapper.readTree(requestBody);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterfaceLog.setPayload(payload);

            } else if (contentType.contains("application/xml")) {
                String json = XmlToJsonUtil.convertXmlToJson(requestBody);
                JsonNode rootNode = objectMapper.readTree(json);
                payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                sarmisInterfaceLog.setXml(requestBody);
                sarmisInterfaceLog.setPayload(payload);

            } else {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(new ApiResponse<>(
                                ApiResponseConstants.INTERNAL_SERVER_ERROR_CODE,
                                ApiResponseConstants.UNSUPPORTED_CONTENT_TYPE + contentType
                        ));
            }

            // Set log metadata
            sarmisInterfaceLog.setMethod("POST");
            sarmisInterfaceLog.setEndpoint("/api/v1/sarmis/depreciation-asset-report");

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
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + "/api/v1/portal/camdigikey/organization-token";
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
                        headers.set(HeaderConstants.AUTHORIZATION_HEADER, organizationToken);

                        // Send request to external SARMIS API
                        try {
                            ResponseEntity<String> sarmisResponse = restTemplate.postForEntity(securityServerURL, entity, String.class);

                            // Log the response from SARMIS
                            sarmisInterfaceLog.setResponse(sarmisResponse.getBody());
                            sarmisInterfaceLog.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

                            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                                return ResponseEntity.ok(new ApiResponse<>(
                                        ApiResponseConstants.SUCCESS_CODE,
                                        ApiResponseConstants.SUCCESS
                                ));
                            } else {
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ApiResponseConstants.SERVICE_UNAVAILABLE_CODE,
                                                ApiResponseConstants.SERVICE_UNAVAILABLE + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromMessage(e.getMessage(), objectMapper);
                            sarmisInterfaceLog.setResponse(sarmisError.toString());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

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
            SarmisInterfaceLog sarmisInterfaceLog = new SarmisInterfaceLog();
            sarmisInterfaceLog.setMethod("GET");
            sarmisInterfaceLog.setEndpoint("/api/v1/sarmis/institution-closing-list");

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
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + "/api/v1/portal/camdigikey/organization-token";
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
                        headers.set(HeaderConstants.AUTHORIZATION_HEADER, organizationToken);

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
                            sarmisInterfaceLog.setResponse((sarmisResponse.getBody()).toString());
                            sarmisInterfaceLog.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

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
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromMessage(e.getMessage(), objectMapper);
                            sarmisInterfaceLog.setResponse(sarmisError.toString());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

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
            SarmisInterfaceLog sarmisInterfaceLog = new SarmisInterfaceLog();
            sarmisInterfaceLog.setMethod("GET");
            sarmisInterfaceLog.setEndpoint("/api/v1/sarmis/asset-kind-list");

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
            String camDigiKeyURL = camDigiKey.get().getAccessURL() + "/api/v1/portal/camdigikey/organization-token";
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
                        headers.set(HeaderConstants.AUTHORIZATION_HEADER, organizationToken);

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
                            sarmisInterfaceLog.setResponse((sarmisResponse.getBody()).toString());
                            sarmisInterfaceLog.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

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
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromMessage(e.getMessage(), objectMapper);
                            sarmisInterfaceLog.setResponse(sarmisError.toString());
                            sarmisInterfaceLogService.save(sarmisInterfaceLog);

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
}