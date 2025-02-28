package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TestService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.FmisService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStatementService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling test-related endpoints.
 */
@RestController
@RequestMapping("/api")
public class TestController {

    private final TestService testService;
    private final FmisService fmisService;
    private final BankStatementService bankStatementService;

    /**
     * Constructor for {@link TestController}.
     *
     * @param testService          The service handling test-related operations.
     * @param fmisService          The service handling FMIS-related operations.
     * @param bankStatementService The service handling bank statement operations.
     */
    public TestController(TestService testService,
                          FmisService fmisService,
                          BankStatementService bankStatementService) {
        this.testService = testService;
        this.fmisService = fmisService;
        this.bankStatementService = bankStatementService;
    }

    /**
     * Endpoint to test FMIS connectivity and retrieve data.
     *
     * @return ResponseEntity containing API response.
     */
    @GetMapping("/test/fmis")
    public ResponseEntity<ApiResponse> testFmis() {

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
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(
                                "201",
                                "Access Successful.",
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
        }

        // Return response if FMIS configuration is not found
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        "404",
                        "FMIS Configuration Not Found.",
                        null
                ));
    }

    /**
     * Basic test endpoint to verify the service is running.
     *
     * @return A success message wrapped in an {@link ApiResponse}.
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<?>> test() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        "200",
                        "FMIS Interface Web Service."
                ));
    }

    /**
     * Retrieves a test entity by name.
     *
     * @param name The name of the test entity.
     * @return The test entity if found.
     */
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
    @PostMapping("/add-test")
    public ResponseEntity<String> save(@RequestBody Test test) {
        testService.addTest(test);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Test entity added successfully.");
    }

    /*
     * Alternative test endpoint that returns a structured API response.
     *
     * @GetMapping("/test")
     * public ApiResponse<String> test() {
     *     return new ApiResponse<>("200", "Success", "Hello, World!");
     * }
     */

    /*
     * Retrieves all test entities.
     *
     * @GetMapping("/get-all-test")
     * public ResponseEntity<List<Test>> getAll() {
     *     List<Test> testList = testService.getAllTests();
     *     if (!testList.isEmpty()) {
     *         return ResponseEntity.ok(testList);
     *     } else {
     *         return ResponseEntity.status(HttpStatus.NOT_FOUND)
     *                 .body(Collections.emptyList());
     *     }
     * }
     */
}