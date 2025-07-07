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
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.InterfaceCodeGenerator;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.XmlToJsonUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    private static final String FMIS_BATCH_PO_SARMIS = "FMIS_BATCH_PO_SARMIS";
    private static final String SARMIS_APP_KEY = "sarmis_interface";

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
                            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                    .body(new ApiResponse<>(
                                            ApiResponseConstants.BAD_GATEWAY_CODE,
                                            ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT + " (" + securityServerURL + ")"
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
}