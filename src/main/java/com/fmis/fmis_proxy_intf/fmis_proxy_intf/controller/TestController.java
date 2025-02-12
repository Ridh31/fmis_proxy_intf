package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TestService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling test-related endpoints.
 */
@RestController
@RequestMapping("/api")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    /**
     * Basic test endpoint to verify the service is running.
     *
     * @return A simple success message.
     */
    @GetMapping("/test")
    public String test() {
        return "FMIS Interface Web Service";
    }

    /**
     * Retrieves a test entity by name.
     *
     * @param name The name of the test entity.
     * @return The test entity.
     */
    @GetMapping("/getname")
    public Test getTestByTestName(@RequestParam String name) {
        return testService.test(name);
    }

    /**
     * Saves a new test entity.
     *
     * @param test The test entity to be saved.
     */
    @PostMapping("/add")
    public void save(@RequestBody Test test) {
        testService.addTest(test);
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
     * public StandardResponse getAll() {
     *     List<Test> testList = testService.getAllTests();
     *     if (testList != null && !testList.isEmpty()) {
     *         return StandardResponse.success("Tests found", testList);
     *     } else {
     *         return StandardResponse.notFound("No tests found");
     *     }
     * }
     */
}
