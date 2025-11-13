package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterface;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.InternalCamDigiKeyService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SarmisInterfaceService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SecurityServerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TelegramNotificationService;
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
import java.util.stream.Collectors;

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
    private final TelegramNotificationService telegramNotificationService;

    /**
     * Constructs a new {@code SarmisController} with the required service dependencies.
     * Initializes services responsible for logging interface activity, retrieving security server configurations,
     * and sending HTTP requests to external APIs.
     *
     * @param sarmisInterfaceService      service for saving and managing SARMIS interface logs
     * @param securityServerService       service for retrieving security server configurations by config key
     * @param internalCamDigiKeyService   Service for interacting with CamDigiKey and retrieving authorization tokens.
     * @param restTemplate                HTTP client for sending requests to external systems such as SARMIS
     * @param authorizationHelper         helper for authorization and authentication checks
     * @param telegramNotificationService service for sending Telegram notifications
     */
    @Autowired
    public SarmisInterfaceController(SarmisInterfaceService sarmisInterfaceService,
                                     SecurityServerService securityServerService,
                                     InternalCamDigiKeyService internalCamDigiKeyService,
                                     RestTemplate restTemplate,
                                     AuthorizationHelper authorizationHelper,
                                     TelegramNotificationService telegramNotificationService) {
        this.sarmisInterfaceService = sarmisInterfaceService;
        this.securityServerService = securityServerService;
        this.internalCamDigiKeyService = internalCamDigiKeyService;
        this.restTemplate = restTemplate;
        this.authorizationHelper = authorizationHelper;
        this.telegramNotificationService = telegramNotificationService;
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
                sarmisInterface.setStatus(false);
                sarmisInterface.setResponse("Unsupported media type: " + contentType);
                sarmisInterfaceService.save(sarmisInterface);

                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.unsupportedMediaType(),
                                ResponseMessageUtil.unsupportedMediaType(contentType)
                        ));
            }

            // Set log metadata
            sarmisInterface.setMethod("POST");
            sarmisInterface.setEndpoint(apiPrefix + "/sarmis/fmis-purchase-orders");
            sarmisInterface.setInterfaceCode(generatedCode);

            // Retrieve SecurityServer configuration by config key
            Optional<SecurityServer> optionalConfig = securityServerService.getByConfigKey(FMIS_BATCH_PO_SARMIS);
            if (optionalConfig.isEmpty()) {
                sarmisInterface.setStatus(false);
                sarmisInterface.setResponse("Configuration not found: " + FMIS_BATCH_PO_SARMIS);
                sarmisInterfaceService.save(sarmisInterface);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound(FMIS_BATCH_PO_SARMIS)
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
                sarmisInterface.setStatus(false);
                sarmisInterface.setResponse("CamDigiKey configuration not found: " + SARMIS_APP_KEY);
                sarmisInterfaceService.save(sarmisInterface);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound(SARMIS_APP_KEY)
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
                                        ResponseCodeUtil.processed(),
                                        ResponseMessageUtil.processed("Batch Purchase Orders"),
                                        objectMapper.readTree(sarmisResponse.getBody())
                                ));
                            } else {
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ResponseCodeUtil.serviceUnavailable(),
                                                ResponseMessageUtil.serviceUnavailable() + " Detail: " + sarmisResponse
                                        ));
                            }
                        } catch (RestClientException e) {
                            JsonNode sarmisError = ExceptionUtils.extractJsonFromErrorMessage(e.getMessage(), objectMapper);
                            sarmisInterface.setResponse(sarmisError.toString());
                            sarmisInterface.setStatus(false);
                            sarmisInterfaceService.save(sarmisInterface);

                            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                    .body(new ApiResponse<>(
                                            ResponseCodeUtil.upstreamServiceError(),
                                            ResponseMessageUtil.upstreamServiceError(),
                                            sarmisError
                                    ));
                        }
                    } else {
                        String message = root.path("message").asText("Unknown CamDigiKey error");
                        sarmisInterface.setStatus(false);
                        sarmisInterface.setResponse(message);
                        sarmisInterfaceService.save(sarmisInterface);

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(
                                        ResponseCodeUtil.invalid(),
                                        message
                                ));
                    }

                } catch (JsonProcessingException e) {
                    sarmisInterface.setStatus(false);
                    sarmisInterface.setResponse("JSON processing error: " + e.getMessage());
                    sarmisInterfaceService.save(sarmisInterface);

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    ResponseCodeUtil.internalError(),
                                    ResponseMessageUtil.internalError("JSON response")
                            ));
                }
            } else {
                sarmisInterface.setStatus(false);
                sarmisInterface.setResponse("Failed to fetch organization token");
                sarmisInterfaceService.save(sarmisInterface);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.fetchError(),
                                ResponseMessageUtil.fetchError("Organization token")
                        ));
            }

        } catch (Exception e) {
            sarmisInterface.setStatus(false);
            sarmisInterface.setResponse("Unexpected error: " + e.getMessage());
            sarmisInterfaceService.save(sarmisInterface);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Batch purchase order")
                    ));
        }
    }

    /**
     * Handles FMIS purchase order callbacks by validating the request, logging results,
     * sending Telegram notifications, and returning a standardized {@link ApiResponse}.
     *
     * @param requestBody raw JSON payload from FMIS
     * @return ResponseEntity with success or error response
     */
    @PostMapping("/sarmis/fmis-purchase-orders-callback")
    public ResponseEntity<?> fmisPurchaseOrdersCallback(@RequestBody String requestBody) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        SarmisInterface sarmisInterface = new SarmisInterface();
        String endpoint = apiPrefix + "/sarmis/fmis-purchase-orders-callback";

        try {
            // Convert request into JsonNode for logging & validation
            JsonNode jsonBody = objectMapper.readTree(requestBody);

            try {
                // Perform request validation
                BodyValidationUtil.validateBatchPOCallback(objectMapper.convertValue(jsonBody, Map.class));
            } catch (IllegalArgumentException e) {
                // Validation failed — log + notify
                sarmisInterface.setMethod("POST");
                sarmisInterface.setEndpoint(endpoint);
                sarmisInterface.setPayload(jsonBody.toString());
                sarmisInterface.setResponse(objectMapper.writeValueAsString(
                        new ApiResponse<>(
                                ResponseCodeUtil.invalid(),
                                e.getMessage()
                        )
                ));
                sarmisInterface.setStatus(false);
                sarmisInterfaceService.save(sarmisInterface);

                String telegramMessage = TelegramUtil.buildBatchPOCallbackErrorNotification(e.getMessage());
                telegramNotificationService.sendSarmisInterfaceMessage(telegramMessage);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.invalid(),
                                e.getMessage()
                        ));
            }

            // Extract interface_code if present
            String interfaceCode = jsonBody.has("interface_code")
                    ? jsonBody.get("interface_code").asText()
                    : null;

            // Log successful validation
            sarmisInterface.setMethod("POST");
            sarmisInterface.setEndpoint(endpoint);
            sarmisInterface.setInterfaceCode(interfaceCode);
            sarmisInterface.setPayload(jsonBody.toString());
            sarmisInterface.setResponse(objectMapper.writeValueAsString(
                    new ApiResponse<>(
                            ResponseCodeUtil.processed(),
                            ResponseMessageUtil.processed("Purchases orders callback"),
                            jsonBody
                    )
            ));
            sarmisInterface.setStatus(true);
            sarmisInterfaceService.save(sarmisInterface);

            // Send Telegram notification
            String telegramMessage = TelegramUtil.buildBatchPOCallbackNotification(jsonBody);
            telegramNotificationService.sendSarmisInterfaceMessage(telegramMessage);

            return ResponseEntity.ok(new ApiResponse<>(
                    ResponseCodeUtil.processed(),
                    ResponseMessageUtil.processed("Purchases orders callback"),
                    jsonBody
            ));

        } catch (JsonProcessingException e) {
            // JSON parsing failed — log + notify
            sarmisInterface.setMethod("POST");
            sarmisInterface.setEndpoint(endpoint);
            sarmisInterface.setPayload(requestBody);
            sarmisInterface.setResponse(new ApiResponse<>(
                    ResponseCodeUtil.internalError(),
                    ResponseMessageUtil.internalError("Purchases orders callback")
            ).toString());
            sarmisInterface.setStatus(false);
            sarmisInterfaceService.save(sarmisInterface);

            String telegramMessage = TelegramUtil.buildBatchPOCallbackErrorNotification(e.getMessage());
            telegramNotificationService.sendSarmisInterfaceMessage(telegramMessage);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Purchases orders callback")
                    ));
        }
    }

    /**
     * Retrieves long-term asset report data from SARMIS using request parameters.
     * Logs the request for auditing, adds authentication via CamDigiKey,
     * and forwards it as query parameters to the SARMIS API.
     *
     * @param institutionCode institution identifier (required)
     * @param closingYear     closing year of the report (required)
     * @param assetKindCode   asset kind code (optional)
     * @return ApiResponse with SARMIS response or error details
     */
    @GetMapping("/sarmis/long-term-asset-report")
    public ResponseEntity<ApiResponse<?>> longTermAssetReport(
            @RequestParam("institution_code") String institutionCode,
            @RequestParam("closing_year") String closingYear,
            @RequestParam(name = "asset_kind_code", required = false, defaultValue = "") String assetKindCode
    ) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        SarmisInterface sarmisInterface = new SarmisInterface();

        try {
            Map<String, String> params = Map.of(
                    "institution_code", institutionCode,
                    "closing_year", closingYear,
                    "asset_kind_code", assetKindCode
            );

            String endpoint = apiPrefix + "/sarmis/long-term-asset-report?" +
                    params.entrySet().stream()
                            .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("&"));

            sarmisInterface.setMethod("GET");
            sarmisInterface.setEndpoint(endpoint);

            // Retrieve SecurityServer configuration
            SecurityServer securityServer = securityServerService.getByConfigKey(LONG_TERM_ASSET_REPORT_SARMIS)
                    .orElseThrow(() -> new RuntimeException(
                            ResponseMessageUtil.configurationNotFound(LONG_TERM_ASSET_REPORT_SARMIS)));

            // Build URI with query parameters
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(securityServer.getBaseURL() + securityServer.getEndpoint())
                    .queryParam("institution_code", institutionCode)
                    .queryParam("closing_year", closingYear);

            if (!assetKindCode.isEmpty()) {
                uriBuilder.queryParam("asset_kind_code", assetKindCode);
            }

            URI uri = uriBuilder.build().encode().toUri();

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(securityServer.getContentType()));
            headers.set(HeaderConstants.X_ROAD_CLIENT, securityServer.getSubsystem());

            // CamDigiKey Authorization
            InternalCamDigiKey camDigiKey = internalCamDigiKeyService.findByAppKey(SARMIS_APP_KEY)
                    .orElseThrow(() -> new RuntimeException(
                            ResponseMessageUtil.configurationNotFound(SARMIS_APP_KEY)));

            String camDigiKeyURL = camDigiKey.getAccessURL() + apiPrefix + "/portal/camdigikey/organization-token";
            ResponseEntity<String> camDigiKeyResponse = restTemplate.getForEntity(camDigiKeyURL, String.class);

            if (camDigiKeyResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.fetchError(),
                                ResponseMessageUtil.fetchError("Organization token")
                        ));
            }

            JsonNode camDigiKeyJson = objectMapper.readTree(camDigiKeyResponse.getBody());
            if (camDigiKeyJson.path("error").asInt() != 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.invalid(),
                                camDigiKeyJson.path("message").asText("Unknown error")
                        ));
            }

            String organizationToken = camDigiKeyJson.path("data").path("accessToken").asText();
            headers.set(HttpHeaders.AUTHORIZATION, organizationToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward GET request to SARMIS using URI
            ResponseEntity<String> sarmisResponse = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            // Save audit log
            sarmisInterface.setResponse(sarmisResponse.getBody());
            sarmisInterface.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
            sarmisInterfaceService.save(sarmisInterface);

            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(new ApiResponse<>(
                        ResponseCodeUtil.processed(),
                        ResponseMessageUtil.processed("Long term asset report"),
                        objectMapper.readTree(sarmisResponse.getBody())
                ));
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.serviceUnavailable(),
                                ResponseMessageUtil.serviceUnavailable() + " Detail: " + sarmisResponse
                        ));
            }

        } catch (Exception e) {
            JsonNode sarmisError = ExceptionUtils.extractJsonFromErrorMessage(e.getMessage(), objectMapper);
            sarmisInterface.setResponse(sarmisError.toString());
            sarmisInterface.setStatus(false);
            sarmisInterfaceService.save(sarmisInterface);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Long term asset report"),
                            sarmisError
                    ));
        }
    }

    /**
     * Handles POST requests for the Long-Term Asset Report in XML format.
     *
     * Wraps the JSON-based `longTermAssetReport` endpoint and converts its response into XML.
     *
     * @param institutionCode institution identifier (required)
     * @param closingYear     closing year of the report (required)
     * @param assetKindCode   asset kind code (optional)
     * @return ResponseEntity with XML content and appropriate status.
     */
    @GetMapping("/sarmis/long-term-asset-report/xml")
    public ResponseEntity<String> longTermAssetReportXml(
            @RequestParam("institution_code") String institutionCode,
            @RequestParam("closing_year") String closingYear,
            @RequestParam(name = "asset_kind_code", required = false, defaultValue = "") String assetKindCode) {

        try {
            // Call the JSON endpoint directly
            ResponseEntity<ApiResponse<?>> jsonResponse = longTermAssetReport(
                    institutionCode, closingYear, assetKindCode);

            ApiResponse<?> apiResponse = jsonResponse.getBody();
            if (apiResponse == null) {
                return ResponseEntity.noContent().build();
            }

            // Convert ApiResponse<?> to Map
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.convertValue(apiResponse, Map.class);

            // Check if the "message" field is a JSON string, then parse it
            Object messageObj = responseMap.get("message");
            if (messageObj instanceof String) {
                String messageStr = ((String) messageObj).trim();

                // Look for embedded JSON after the last colon and space ": "
                int jsonStart = messageStr.indexOf("{");
                int jsonEnd = messageStr.lastIndexOf("}");
                if (jsonStart >= 0 && jsonEnd > jsonStart) {
                    String prefix = messageStr.substring(0, jsonStart).trim();
                    String jsonPart = messageStr.substring(jsonStart, jsonEnd + 1);

                    try {
                        Map<String, Object> embeddedJsonMap = mapper.readValue(jsonPart, new TypeReference<Map<String,Object>>() {});

                        // Create a new map to replace "message"
                        Map<String, Object> newMessageMap = new HashMap<>();
                        newMessageMap.put("text", prefix);
                        newMessageMap.putAll(embeddedJsonMap);

                        responseMap.put("message", newMessageMap);

                    } catch (Exception ex) {
                        System.err.println(ResponseMessageUtil.internalError("Long term asset report (XML)"));
                    }
                } else if (messageStr.startsWith("{") && messageStr.endsWith("}")) {
                    try {
                        Map<String, Object> innerMessageMap = mapper.readValue(
                                messageStr, new TypeReference<Map<String, Object>>() {});
                        responseMap.put("message", innerMessageMap);
                    } catch (Exception ex) {
                        System.err.println(ResponseMessageUtil.internalError("Long term asset report (XML)"));
                    }
                }
            }

            // Recursively replace nulls with empty strings
            JsonToXmlUtil.replaceNullsWithEmptyString(responseMap);

            // Convert Map to XML
            JSONObject jsonObject = new JSONObject(responseMap);
            String xml = XML.toString(jsonObject, "response");

            return ResponseEntity
                    .status(jsonResponse.getStatusCode())
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml);

        } catch (Exception e) {
            String errorXml = "<error><message>" + e.getMessage() + "</message></error>";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(errorXml);
        }
    }

    /**
     * Retrieves depreciation asset report data from SARMIS using request parameters.
     * Logs the request for auditing, adds authentication via CamDigiKey,
     * and forwards it as query parameters to the SARMIS API.
     *
     * @param institutionCode institution identifier (required)
     * @param closingYear     closing year of the report (required)
     * @param financialCode   financial code (optional)
     * @return ApiResponse with SARMIS response or error details
     */
    @GetMapping("/sarmis/depreciation-asset-report")
    public ResponseEntity<ApiResponse<?>> depreciationAssetReport(
            @RequestParam("institution_code") String institutionCode,
            @RequestParam("closing_year") String closingYear,
            @RequestParam(name = "financial_code", required = false, defaultValue = "") String financialCode
    ) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        SarmisInterface sarmisInterface = new SarmisInterface();

        try {
            Map<String, String> params = Map.of(
                    "institution_code", institutionCode,
                    "closing_year", closingYear,
                    "financial_code", financialCode
            );

            String endpoint = apiPrefix + "/sarmis/depreciation-asset-report?" +
                    params.entrySet().stream()
                            .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("&"));

            sarmisInterface.setMethod("GET");
            sarmisInterface.setEndpoint(endpoint);

            // Retrieve SecurityServer configuration
            SecurityServer securityServer = securityServerService.getByConfigKey(DEPRECIATION_ASSET_REPORT_SARMIS)
                    .orElseThrow(() -> new RuntimeException(
                            ResponseMessageUtil.configurationNotFound(DEPRECIATION_ASSET_REPORT_SARMIS)));

            // Build URI with query parameters
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(securityServer.getBaseURL() + securityServer.getEndpoint())
                    .queryParam("institution_code", institutionCode)
                    .queryParam("closing_year", closingYear);

            if (!financialCode.isEmpty()) {
                uriBuilder.queryParam("financial_code", financialCode);
            }

            URI uri = uriBuilder.build().encode().toUri();

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(securityServer.getContentType()));
            headers.set(HeaderConstants.X_ROAD_CLIENT, securityServer.getSubsystem());

            // CamDigiKey Authorization
            InternalCamDigiKey camDigiKey = internalCamDigiKeyService.findByAppKey(SARMIS_APP_KEY)
                    .orElseThrow(() -> new RuntimeException(
                            ResponseMessageUtil.configurationNotFound(SARMIS_APP_KEY)));

            String camDigiKeyURL = camDigiKey.getAccessURL() + apiPrefix + "/portal/camdigikey/organization-token";
            ResponseEntity<String> camDigiKeyResponse = restTemplate.getForEntity(camDigiKeyURL, String.class);

            if (camDigiKeyResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.fetchError(),
                                ResponseMessageUtil.fetchError("Organization token")
                        ));
            }

            JsonNode camDigiKeyJson = objectMapper.readTree(camDigiKeyResponse.getBody());
            if (camDigiKeyJson.path("error").asInt() != 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.invalid(),
                                camDigiKeyJson.path("message").asText("Unknown error")
                        ));
            }

            String organizationToken = camDigiKeyJson.path("data").path("accessToken").asText();
            headers.set(HttpHeaders.AUTHORIZATION, organizationToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward GET request to SARMIS using URI
            ResponseEntity<String> sarmisResponse = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            // Save audit log
            sarmisInterface.setResponse(sarmisResponse.getBody());
            sarmisInterface.setStatus(sarmisResponse.getStatusCode().is2xxSuccessful());
            sarmisInterfaceService.save(sarmisInterface);

            if (sarmisResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(new ApiResponse<>(
                        ResponseCodeUtil.processed(),
                        ResponseMessageUtil.processed("Depreciation asset report"),
                        objectMapper.readTree(sarmisResponse.getBody())
                ));
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.serviceUnavailable(),
                                ResponseMessageUtil.serviceUnavailable() + " Detail: " + sarmisResponse
                        ));
            }

        } catch (Exception e) {
            JsonNode sarmisError = ExceptionUtils.extractJsonFromErrorMessage(e.getMessage(), objectMapper);
            sarmisInterface.setResponse(sarmisError.toString());
            sarmisInterface.setStatus(false);
            sarmisInterfaceService.save(sarmisInterface);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Depreciation asset report"),
                            sarmisError
                    ));
        }
    }

    /**
     * Handles POST requests for the Depreciation Asset Report in XML format.
     *
     * Wraps the JSON-based `longTermAssetReport` endpoint and converts its response into XML.
     *
     * @param institutionCode institution identifier (required)
     * @param closingYear     closing year of the report (required)
     * @param financialCode   financial code (optional)
     * @return ResponseEntity with XML content and appropriate status.
     */
    @GetMapping("/sarmis/depreciation-asset-report/xml")
    public ResponseEntity<String> depreciationAssetReportXml(
            @RequestParam("institution_code") String institutionCode,
            @RequestParam("closing_year") String closingYear,
            @RequestParam(name = "financial_code", required = false, defaultValue = "") String financialCode) {

        try {
            // Call the JSON endpoint directly
            ResponseEntity<ApiResponse<?>> jsonResponse = depreciationAssetReport(
                    institutionCode, closingYear, financialCode);

            ApiResponse<?> apiResponse = jsonResponse.getBody();
            if (apiResponse == null) {
                return ResponseEntity.noContent().build();
            }

            // Convert ApiResponse<?> to Map
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.convertValue(apiResponse, Map.class);

            // Check if the "message" field is a JSON string, then parse it
            Object messageObj = responseMap.get("message");
            if (messageObj instanceof String) {
                String messageStr = ((String) messageObj).trim();

                // Look for embedded JSON after the last colon and space ": "
                int jsonStart = messageStr.indexOf("{");
                int jsonEnd = messageStr.lastIndexOf("}");
                if (jsonStart >= 0 && jsonEnd > jsonStart) {
                    String prefix = messageStr.substring(0, jsonStart).trim();
                    String jsonPart = messageStr.substring(jsonStart, jsonEnd + 1);

                    try {
                        Map<String, Object> embeddedJsonMap = mapper.readValue(jsonPart, new TypeReference<Map<String,Object>>() {});

                        // Create a new map to replace "message"
                        Map<String, Object> newMessageMap = new HashMap<>();
                        newMessageMap.put("text", prefix);
                        newMessageMap.putAll(embeddedJsonMap);

                        responseMap.put("message", newMessageMap);

                    } catch (Exception ex) {
                        System.err.println(ResponseMessageUtil.internalError("Depreciation asset report (XML)"));
                    }
                } else if (messageStr.startsWith("{") && messageStr.endsWith("}")) {
                    try {
                        Map<String, Object> innerMessageMap = mapper.readValue(
                                messageStr, new TypeReference<Map<String, Object>>() {});
                        responseMap.put("message", innerMessageMap);
                    } catch (Exception ex) {
                        System.err.println(ResponseMessageUtil.internalError("Depreciation asset report (XML)"));
                    }
                }
            }

            // Recursively replace nulls with empty strings
            JsonToXmlUtil.replaceNullsWithEmptyString(responseMap);

            // Convert Map to XML
            JSONObject jsonObject = new JSONObject(responseMap);
            String xml = XML.toString(jsonObject, "response");

            return ResponseEntity
                    .status(jsonResponse.getStatusCode())
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml);

        } catch (Exception e) {
            String errorXml = "<error><message>" + e.getMessage() + "</message></error>";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(errorXml);
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
            Map<String, String> params = Map.of(
                    "page", String.valueOf(page),
                    "size", String.valueOf(size),
                    "search", search
            );

            String endpoint = apiPrefix + "/sarmis/institution-closing-list?" +
                    params.entrySet().stream()
                            .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("&"));

            // Initialize the SARMIS interface log
            SarmisInterface sarmisInterface = new SarmisInterface();
            sarmisInterface.setMethod("GET");
            sarmisInterface.setEndpoint(endpoint);

            // Retrieve SARMIS configuration from database
            Optional<SecurityServer> optionalConfig = securityServerService.getByConfigKey(INSTITUTION_CLOSING_LIST_SARMIS);
            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound(INSTITUTION_CLOSING_LIST_SARMIS)
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
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound(SARMIS_APP_KEY)
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
                                        ResponseCodeUtil.processed(),
                                        ResponseMessageUtil.processed("Institution closing list"),
                                        sarmisResponseJSON
                                ));
                            } else {
                                // SARMIS responded but not with 2xx
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ResponseCodeUtil.serviceUnavailable(),
                                                ResponseMessageUtil.serviceUnavailable() + " Detail: " + sarmisResponse
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
                                            ResponseCodeUtil.upstreamServiceError(),
                                            ResponseMessageUtil.upstreamServiceError(),
                                            sarmisError
                                    ));
                        }
                    } else {
                        // CamDigiKey returned an error code
                        String message = root.path("message").asText("Unknown error");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(
                                        ResponseCodeUtil.invalid(),
                                        message
                                ));
                    }

                } catch (JsonProcessingException e) {
                    // JSON parsing error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    ResponseCodeUtil.internalError(),
                                    ResponseMessageUtil.internalError("JSON response")
                            ));
                }
            } else {
                // CamDigiKey failed to provide a token
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.fetchError(),
                                ResponseMessageUtil.fetchError("Organization token")
                        ));
            }

        } catch (Exception e) {
            // Catch-all for unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Institution closing list")
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
            Map<String, String> params = Map.of(
                    "page", String.valueOf(page),
                    "size", String.valueOf(size),
                    "search", search
            );

            String endpoint = apiPrefix + "/sarmis/asset-kind-list?" +
                    params.entrySet().stream()
                            .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("&"));

            // Initialize the SARMIS interface log
            SarmisInterface sarmisInterface = new SarmisInterface();
            sarmisInterface.setMethod("GET");
            sarmisInterface.setEndpoint(endpoint);

            // Retrieve SARMIS configuration from database
            Optional<SecurityServer> optionalConfig = securityServerService.getByConfigKey(ASSET_KIND_LIST_SARMIS);
            if (optionalConfig.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound(ASSET_KIND_LIST_SARMIS)
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
                                ResponseCodeUtil.configurationNotFound(),
                                ResponseMessageUtil.configurationNotFound(SARMIS_APP_KEY)
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
                                        ResponseCodeUtil.processed(),
                                        ResponseMessageUtil.processed("Asset kind list"),
                                        assetKindList
                                ));
                            } else {
                                // SARMIS responded but not with 2xx
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new ApiResponse<>(
                                                ResponseCodeUtil.serviceUnavailable(),
                                                ResponseMessageUtil.serviceUnavailable() + " Detail: " + sarmisResponse
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
                                            ResponseCodeUtil.upstreamServiceError(),
                                            ResponseMessageUtil.upstreamServiceError(),
                                            sarmisError
                                    ));
                        }
                    } else {
                        // CamDigiKey returned an error code
                        String message = root.path("message").asText("Unknown error");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(
                                        ResponseCodeUtil.invalid(),
                                        message
                                ));
                    }

                } catch (JsonProcessingException e) {
                    // JSON parsing error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(
                                    ResponseCodeUtil.internalError(),
                                    ResponseMessageUtil.internalError("JSON response")
                            ));
                }
            } else {
                // CamDigiKey failed to provide a token
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.fetchError(),
                                ResponseMessageUtil.fetchError("Organization token")
                        ));
            }

        } catch (Exception e) {
            // Catch-all for unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.internalError(),
                            ResponseMessageUtil.internalError("Asset kind list")
                    ));
        }
    }

    /**
     * Returns the asset kind list in XML format.
     *
     * Converts the JSON response from {@code assetKindList()} to XML
     * using the org.json library, without affecting global message converters.
     *
     * @param page   Page number (default: 1)
     * @param size   Page size (default: 10)
     * @param search Optional search term (default: "")
     * @return XML response
     */
    @GetMapping(value = "/sarmis/asset-kind-list/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> assetKindListXml(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        try {
            // Call existing JSON endpoint
            ResponseEntity<ApiResponse<?>> jsonResponse = assetKindList(page, size, search);
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
        Object authorization = authorizationHelper.authenticateAndAuthorizeModerator();
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
                                ResponseCodeUtil.invalidField(),
                                ResponseMessageUtil.invalidField("Status", "boolean")
                        ));
            }
        }

        try {
            // Fetch paginated SARMIS interface list
            Page<SarmisInterface> sarmisInterface = sarmisInterfaceService.getFilteredSarmisInterface(
                    page, size, endpoint, interfaceCode, purchaseOrderId, actionDate, statusValue);

            return ResponseEntity.ok(new ApiResponse<>(
                    ResponseCodeUtil.processed(),
                    ResponseMessageUtil.processed("SARMIS interface log"),
                    sarmisInterface
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            ResponseCodeUtil.fetchError(),
                            ResponseMessageUtil.fetchError("SARMIS interface log")
                    ));
        }
    }
}