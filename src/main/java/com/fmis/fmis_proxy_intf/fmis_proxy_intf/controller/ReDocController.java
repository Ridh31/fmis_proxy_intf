package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;

@Controller
@RequestMapping("/api/v1")
public class ReDocController {

    /**
     * Serve the redoc.html file as the API documentation.
     * @return ResponseEntity containing the redoc.html content
     * @throws IOException if there is an error reading the file
     */
    @GetMapping("/docs")
    public ResponseEntity<byte[]> serveReDoc() throws IOException {

        // Load the redoc.html file from the static directory
        Resource resource = new ClassPathResource("static/redoc.html");
        byte[] content = Files.readAllBytes(resource.getFile().toPath());

        // Set HTTP headers for content type
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

        // Return the content with the appropriate headers and OK status
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
