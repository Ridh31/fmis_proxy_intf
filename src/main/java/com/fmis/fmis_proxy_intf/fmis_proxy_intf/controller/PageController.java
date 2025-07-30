package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Controller for serving static HTML pages like login and bank statement log.
 */
@Controller
@RequestMapping("/admin")
public class PageController {

    @Value("${application.api.prefix}")
    private String apiPrefix;

    private final int cookieLifetime = 300;
    private final UserService userService;

    /**
     * Constructor for {@code PageController} that injects the {@code UserService}.
     *
     * @param userService service used to manage user-related operations
     */
    public PageController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles GET requests to the login page.
     *
     * @return ResponseEntity containing the login HTML page or an error message if the file is not found.
     */
    @GetMapping("/login")
    public ResponseEntity<byte[]> login() {
        try {
            Resource resource = new ClassPathResource("templates/login.html");
            String title = "Login | FMIS Proxy Interface";
            String heading = "Login";
            String content = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
            content = content.replace("{{title}}", title)
                    .replace("{{heading}}", heading)
                    .replace("{{apiPrefix}}", apiPrefix);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

            // Clear cookies by setting them with an expired max-age
            ResponseCookie isAdminCookie = ResponseCookie.from("isAdmin", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            ResponseCookie adminUsernameCookie = ResponseCookie.from("adminUsername", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            ResponseCookie adminPasswordCookie = ResponseCookie.from("adminPassword", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            headers.add(HttpHeaders.SET_COOKIE, isAdminCookie.toString());
            headers.add(HttpHeaders.SET_COOKIE, adminUsernameCookie.toString());
            headers.add(HttpHeaders.SET_COOKIE, adminPasswordCookie.toString());

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

    /**
     * Handles GET requests to the bank statement log page.
     *
     * @return ResponseEntity containing the bank statement log HTML page or an error message if the file is not found.
     */
    @GetMapping("/bank-statement-log")
    public Object bankStatementLog(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            HttpServletResponse response
    ) {
        if (!"true".equals(isAdmin) || !StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
            return new RedirectView(apiPrefix + "/admin/login");
        }

        try {
            Optional<User> userOptional = userService.findByUsername(adminUsername);
            String partnerToken = userOptional.get().getPartner().getPublicKey();

            Resource resource = new ClassPathResource("templates/bank-statement-log.html");
            String title = "Bank Statement History | FMIS Proxy Interface";
            String heading = "Bank Statement History";
            String content = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
            content = content.replace("{{title}}", title)
                    .replace("{{heading}}", heading)
                    .replace("{{username}}", adminUsername)
                    .replace("{{password}}", adminPassword)
                    .replace("{{partnerToken}}", partnerToken)
                    .replace("{{apiPrefix}}", apiPrefix);

            // Set cookie expire duration
            CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
            CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
            CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

            return new ResponseEntity<>(
                    content.getBytes(StandardCharsets.UTF_8),
                    headers,
                    HttpStatus.OK
            );
        } catch (IOException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
            return new ResponseEntity<>(
                    "Error loading file.".getBytes(),
                    headers,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Handles GET request to the FMIS configuration page.
     *
     * @return ResponseEntity with the config HTML page or a redirect if not authenticated.
     */
    @GetMapping("/fmis-config")
    public Object fmisConfig(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            HttpServletResponse response
    ) {
        if (!"true".equals(isAdmin) || !StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
            return new RedirectView(apiPrefix + "/admin/login");
        }

        try {
            Optional<User> userOptional = userService.findByUsername(adminUsername);
            String partnerToken = userOptional.get().getPartner().getPublicKey();

            Resource resource = new ClassPathResource("templates/fmis-config.html");
            String title = "Configuration | FMIS Proxy Interface";
            String heading = "FMIS Configuration";
            String content = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
            content = content.replace("{{title}}", title)
                    .replace("{{heading}}", heading)
                    .replace("{{username}}", adminUsername)
                    .replace("{{password}}", adminPassword)
                    .replace("{{partnerToken}}", partnerToken)
                    .replace("{{apiPrefix}}", apiPrefix);

            // Set cookie expire duration
            CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
            CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
            CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

            return new ResponseEntity<>(
                    content.getBytes(StandardCharsets.UTF_8),
                    headers,
                    HttpStatus.OK
            );
        } catch (IOException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
            return new ResponseEntity<>(
                    "Error loading file.".getBytes(),
                    headers,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Handles GET request to the Internal CamDigiKey page.
     *
     * @return ResponseEntity with the config HTML page or a redirect if not authenticated.
     */
    @GetMapping("/internal-camdigikey")
    public Object internalCamDigiKey(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            HttpServletResponse response
    ) {
        if (!"true".equals(isAdmin) || !StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
            return new RedirectView(apiPrefix + "/admin/login");
        }

        try {
            Optional<User> userOptional = userService.findByUsername(adminUsername);
            String partnerToken = userOptional.get().getPartner().getPublicKey();

            Resource resource = new ClassPathResource("templates/internal-camdigikey.html");
            String title = "Internal CamDigiKey | FMIS Proxy Interface";
            String heading = "Internal CamDigiKey";
            String content = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
            content = content.replace("{{title}}", title)
                    .replace("{{heading}}", heading)
                    .replace("{{username}}", adminUsername)
                    .replace("{{password}}", adminPassword)
                    .replace("{{partnerToken}}", partnerToken)
                    .replace("{{apiPrefix}}", apiPrefix);

            // Set cookie expire duration
            CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
            CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
            CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

            return new ResponseEntity<>(
                    content.getBytes(StandardCharsets.UTF_8),
                    headers,
                    HttpStatus.OK
            );
        } catch (IOException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
            return new ResponseEntity<>(
                    "Error loading file.".getBytes(),
                    headers,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}