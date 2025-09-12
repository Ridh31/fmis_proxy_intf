package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TestService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.FmisService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling test-related endpoints.
 */
@Tag(
        name = "Test Operations",
        description = "Endpoints related to testing and FMIS connectivity."
)
@RestController
@RequestMapping
public class TestController {

    private final TestService testService;
    private final FmisService fmisService;

    /**
     * Constructor for {@link TestController}.
     *
     * @param testService          The service handling test-related operations.
     * @param fmisService          The service handling FMIS-related operations.
     */
    public TestController(TestService testService,
                          FmisService fmisService) {
        this.testService = testService;
        this.fmisService = fmisService;
    }

    /**
     * Endpoint to test FMIS connectivity and retrieve data.
     *
     * @return ResponseEntity containing API response.
     */
    @Operation(
            summary = "Test FMIS Connectivity",
            description = "Tests the connectivity to FMIS and retrieves test data.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.SUCCESS_CODE_STRING,
                            description = ApiResponseConstants.SUCCESS,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_SUCCESS,
                                            value = ApiResponseExamples.FMIS_TEST_SUCCESS
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.BAD_GATEWAY_CODE_STRING,
                            description = ApiResponseConstants.BAD_GATEWAY_NOT_CONNECT,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_FMIS_FAILURE,
                                            value = ApiResponseExamples.FMIS_TEST_BAD_GATEWAY
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.NOT_FOUND_CODE_STRING,
                            description = ApiResponseConstants.NOT_FOUND,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.NOT_FOUND_FMIS_CONFIG,
                                            value = ApiResponseExamples.FMIS_TEST_NO_CONFIG_FOUND
                                    )
                            )
                    )
            }
    )
    @GetMapping("/test/fmis")
    public ResponseEntity<ApiResponse<?>> testFmis() {

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
                // Return success response
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.processed(),
                                ResponseMessageUtil.processed("Resource"),
                                fmisResponseBody
                        ));
            } else {
                String responseHost = ExceptionUtils.formatHostFromContent(fmisResponseBody);

                // Handle failure in sending data to FMIS
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse<>(
                                ResponseCodeUtil.badGatewayNotConnect(),
                                ResponseMessageUtil.badGatewayNotConnect(responseHost)
                        ));
            }
        }

        // Return response if FMIS configuration is not found
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.configurationNotFound(),
                        ResponseMessageUtil.configurationNotFound("FMIS"),
                        null
                ));
    }

    /**
     * Basic test endpoint to verify the service is running.
     *
     * @return A success message wrapped in an {@link ApiResponse}.
     */
    @Operation(
            summary = "Basic Test",
            description = "Verifies that the service is running by returning a simple success message.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.SUCCESS_CODE_STRING,
                            description = ApiResponseConstants.SUCCESS,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_SUCCESS,
                                            value = ApiResponseExamples.BASIC_TEST
                                    )
                            )
                    )
            }
    )
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<?>> test() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        ResponseCodeUtil.processed(),
                        "FMIS Interface Web Service."
                ));
    }

    /**
     * Endpoint to verify connectivity and provide a basic response from the SARMIS system.
     * This endpoint is used to confirm that the SARMIS system is up, running, and accessible.
     *
     * @return A successful {@link ApiResponse} containing a status code, message, and confirmation data.
     */
    @Operation(
            summary = "Verify SARMIS Connectivity",
            description = "Verifies that SARMIS can successfully access and interact with the endpoint. " +
                    "Returns a success message confirming connectivity and readiness for integration.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = ApiResponseConstants.SUCCESS_CODE_STRING,
                            description = ApiResponseConstants.SUCCESS,
                            content = @Content(
                                    mediaType = HeaderConstants.CONTENT_TYPE_JSON,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants.RESPONSE_TYPE_SUCCESS,
                                            value = ApiResponseExamples.SARMIS_TEST
                                    )
                            )
                    )
            }
    )
    @GetMapping("/test/sarmis")
    public ResponseEntity<Map<String, Object>> testSarmis() {

        // Prepare the response body with the desired fields
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successful");
        response.put("error", "0");
        response.put("data", "Welcome!");

        // Return the response with HTTP status 200 (OK)
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves a test entity by name.
     *
     * @param name The name of the test entity.
     * @return The test entity if found.
     */
    @Operation(
            summary = "Get Test by Name",
            description = "Retrieves a test entity by its name."
    )
    @Hidden
    @GetMapping("/get-name-test")
    public ResponseEntity<Test> getTestByTestName(@RequestParam String name) {
        Test test = testService.test(name);
        return ResponseEntity.ok(test);
    }

    /**
     * Saves a new test entity.
     *
     * @param test The test entity to be saved.
     * @return ResponseEntity indicating the operation status.
     */
    @Operation(
            summary = "Add New Test",
            description = "Adds a new test entity to the system."
    )
    @Hidden
    @PostMapping("/add-test")
    public ResponseEntity<String> save(@RequestBody Test test) {
        testService.addTest(test);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseMessageUtil.created("Resource"));
    }
}