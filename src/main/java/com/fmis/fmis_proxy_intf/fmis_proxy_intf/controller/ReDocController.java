package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Controller to handle the API documentation request.
 */
@Controller
@RequestMapping("/api/v1")
public class ReDocController {

    // Inject application details from the application.properties file
    @Value("${application.title}")
    private String applicationTitle;

    @Value("${application.description}")
    private String applicationDescription;

    @Value("${application.version}")
    private String applicationVersion;

    @Value("${application.base-url}")
    private String applicationBaseUrl;

    /**
     * Serve the redoc.html file as the API documentation.
     * This method replaces placeholders in the HTML template with actual application values
     * and returns the content as a response with the appropriate headers.
     *
     * @return ResponseEntity containing the redoc.html content with replaced placeholders.
     * @throws IOException if there is an error reading the redoc.html file.
     */
    @GetMapping("/docs")
    public ResponseEntity<byte[]> serveReDoc() {
        try {
            Resource resource = new ClassPathResource("templates/redoc.html");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
            content = content.replace("{{appTitle}}", applicationTitle)
                    .replace("{{appDescription}}", applicationDescription)
                    .replace("{{appVersion}}", applicationVersion)
                    .replace("{{appBaseUrl}}", applicationBaseUrl);

            // Set HTTP headers for content type (HTML with UTF-8 encoding)
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

            return new ResponseEntity<>(
                    content.getBytes(StandardCharsets.UTF_8),
                    headers,
                    HttpStatus.OK
            );
        } catch (IOException e) {
            String errorMessage = ApiResponseConstants.ERROR_READING_FILE;
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
            return new ResponseEntity<>(
                    errorMessage.getBytes(StandardCharsets.UTF_8),
                    headers,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
