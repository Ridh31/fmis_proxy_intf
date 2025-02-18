package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TestService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling test-related endpoints.
 */
@RestController
@RequestMapping("/api")
public class TestController {

    private final TestService testService;

    /**
     * Constructor for {@link TestController}.
     *
     * @param testService The service handling test-related operations.
     */
    public TestController(TestService testService) {
        this.testService = testService;
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
    @GetMapping("/getname")
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
    @PostMapping("/add")
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
     * @GetMapping("/getall")
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