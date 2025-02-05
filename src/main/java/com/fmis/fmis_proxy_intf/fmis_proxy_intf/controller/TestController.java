package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TestService;
import org.springframework.web.bind.annotation.*;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;  // Import the correct Response class


@RestController
@RequestMapping("/api")

public class TestController {
    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }
//    @GetMapping("/test")
//    public String test() {
//        return "Hello World";
//    }
    // Endpoint that returns a success response with a message
     @GetMapping("/test")
     public ApiResponse<String> test() {
     return new ApiResponse<>("200", "Success", "Hello, World!");
    }
    @GetMapping("/getname")
    public Test getTestByTestName(@RequestParam String name) {
        Test test = testService.test(name);

        return test;
    }
//    @GetMapping("/getall")
//    public StandardResponse getAll() {
//        List<Test> testList = testService.getAllTests();
//        if (testList != null && !testList.isEmpty()) {
//            return StandardResponse.success("Tests found", testList);
//        } else {
//            return StandardResponse.notFound("No tests found");
//        }
//    }

    @PostMapping("/add")
    public void  save(@RequestBody Test test){
        testService.addTest(test);
    }
}
