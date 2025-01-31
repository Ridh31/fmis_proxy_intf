package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")

public class TestController {
    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    @GetMapping("/getname")
    public Test getTestByTestName(@RequestParam String name) {
        Test test = testService.test(name);

        return test;
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Test>> getAll() {
       List <Test> test = testService.getAllTests();

       return new ResponseEntity<>(test, HttpStatus.OK);
    }

    @PostMapping("/add")
    public void  save(@RequestBody Test test){
        testService.addTest(test);
    }
}
