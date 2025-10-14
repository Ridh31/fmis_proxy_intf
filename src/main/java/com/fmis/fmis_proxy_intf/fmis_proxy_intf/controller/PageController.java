package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.CookieUtils;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseMessageUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @Value("${application.version}")
    private String appVersion;

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
            Resource resource = new ClassPathResource("templates/pages/login.html");
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
                    .secure(true)
                    .sameSite("Strict")
                    .build();

            ResponseCookie adminUsernameCookie = ResponseCookie.from("adminUsername", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .build();

            ResponseCookie adminPasswordCookie = ResponseCookie.from("adminPassword", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
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
            String errorMessage = ResponseMessageUtil.internalError("document");

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
     * Adds admin session-related attributes globally if admin is authenticated.
     */
    @ModelAttribute
    public void addAdminAttributesToModel(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            Model model
    ) {
        if (!"true".equals(isAdmin) || !StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
            return;
        }

        Optional<User> optionalUser = userService.findByUsername(adminUsername);
        if (optionalUser.isEmpty()) return;

        User user = optionalUser.get();
        String partnerToken = user.getPartner().getPublicKey();
        int adminLevel = user.getRole().getLevel();

        model.addAttribute("username", adminUsername);
        model.addAttribute("password", adminPassword);
        model.addAttribute("partnerToken", partnerToken);
        model.addAttribute("adminLevel", adminLevel);
        model.addAttribute("appVersion", appVersion);
        model.addAttribute("apiPrefix", apiPrefix);
    }

    /**
     * Adds commonly used admin page URLs to the model for use in views.
     * Each attribute corresponds to a specific menu or page link in the admin interface.
     */
    @ModelAttribute
    public void addMenuUrls(Model model) {
        model.addAttribute("loginUrl", apiPrefix + "/admin/login");
        model.addAttribute("logoutUrl", apiPrefix + "/admin/logout");
        model.addAttribute("homeUrl", apiPrefix + "/admin/home");
        model.addAttribute("dashboardUrl", apiPrefix + "/admin/dashboard");
        model.addAttribute("bankStatementUrl", apiPrefix + "/admin/bank-statement-log");
        model.addAttribute("sarmisInterfaceUrl", apiPrefix + "/admin/sarmis-interface-log");
        model.addAttribute("internalCamDigiKeyUrl", apiPrefix + "/admin/internal-camdigikey");
        model.addAttribute("securityServerUrl", apiPrefix + "/admin/security-server");
        model.addAttribute("partnerManagementUrl", apiPrefix + "/admin/partner-management");
        model.addAttribute("userProfilesUrl", apiPrefix + "/admin/user-profiles");
        model.addAttribute("fmisConfigUrl", apiPrefix + "/admin/fmis-config");
    }

    /**
     * Validates admin authentication using cookie values.
     *
     * @return Redirect to log in if not authenticated; otherwise, null.
     */
    private String checkAdminAuth(String isAdmin, String adminUsername, String adminPassword) {
        if (!"true".equals(isAdmin) || !StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
            return "redirect:" + apiPrefix + "/admin/login";
        }
        return null;
    }

    /**
     * Handles GET requests to the home page.
     *
     * @return ResponseEntity containing the home HTML page or an error message if the file is not found.
     */
    @GetMapping("/home")
    public String home(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            HttpServletResponse response,
            Model model
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Re-set cookies to extend session
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add only page-specific attributes
        model.addAttribute("title", "Home | FMIS Proxy Interface");
        model.addAttribute("heading", "Home");
        model.addAttribute("currentPage", "home");

        return "pages/home";
    }

    /**
     * Handles GET requests to the dashboard log page.
     *
     * @return ResponseEntity containing the dashboard HTML page or an error message if the file is not found.
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            HttpServletResponse response,
            Model model
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Re-set cookies to extend session
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add only page-specific attributes
        model.addAttribute("title", "Dashboard | FMIS Proxy Interface");
        model.addAttribute("heading", "Dashboard");
        model.addAttribute("currentPage", "dashboard");

        return "pages/dashboard";
    }

    /**
     * Handles GET request to the Bank Statement Log page.
     *
     * @return ResponseEntity with the config HTML page or a redirect if not authenticated.
     */
    @GetMapping("/bank-statement-log")
    public String bankStatementLog(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            Model model,
            HttpServletResponse response
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Set cookies
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add attributes to model for Thymeleaf
        model.addAttribute("title", "Bank Statement | FMIS Proxy Interface");
        model.addAttribute("heading", "Bank Statement");
        model.addAttribute("currentPage", "bank-statement-log");

        return "pages/bank-statement-log";
    }

    /**
     * Handles GET request to the SARMIS Interface Log page.
     *
     * @return ResponseEntity with the config HTML page or a redirect if not authenticated.
     */
    @GetMapping("/sarmis-interface-log")
    public String sarmisInterfaceLog(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            Model model,
            HttpServletResponse response
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Set cookies
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add attributes to model for Thymeleaf
        model.addAttribute("title", "SARMIS Interface | FMIS Proxy Interface");
        model.addAttribute("heading", "SARMIS Interface");
        model.addAttribute("currentPage", "sarmis-interface-log");

        return "pages/sarmis-interface-log";
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
            Model model,
            HttpServletResponse response
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Set cookies
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add attributes to model for Thymeleaf
        model.addAttribute("title", "Internal CamDigiKey | FMIS Proxy Interface");
        model.addAttribute("heading", "Internal CamDigiKey");
        model.addAttribute("currentPage", "internal-camdigikey");

        return "pages/internal-camdigikey";
    }

    /**
     * Handles GET request to the Security Server page.
     *
     * @return ResponseEntity with the config HTML page or a redirect if not authenticated.
     */
    @GetMapping("/security-server")
    public String securityServer(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            Model model,
            HttpServletResponse response
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Set cookies
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add attributes to model for Thymeleaf
        model.addAttribute("title", "Security Server | FMIS Proxy Interface");
        model.addAttribute("heading", "Security Server");
        model.addAttribute("currentPage", "security-server");

        return "pages/security-server";
    }

    /**
     * Handles GET request to the Partner Management page.
     *
     * @return ResponseEntity with the config HTML page or a redirect if not authenticated.
     */
    @GetMapping("/partner-management")
    public Object partnerManagement(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            Model model,
            HttpServletResponse response
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Set cookies
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add attributes to model for Thymeleaf
        model.addAttribute("title", "Partner | FMIS Proxy Interface");
        model.addAttribute("heading", "Partner");
        model.addAttribute("currentPage", "partner-management");

        return "pages/partner-management";
    }

    /**
     * Handles GET request to the Partner Management page.
     *
     * @return ResponseEntity with the config HTML page or a redirect if not authenticated.
     */
    @GetMapping("/user-profiles")
    public Object userProfiles(
            @CookieValue(name = "isAdmin", required = false) String isAdmin,
            @CookieValue(name = "adminUsername", required = false) String adminUsername,
            @CookieValue(name = "adminPassword", required = false) String adminPassword,
            Model model,
            HttpServletResponse response
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Set cookies
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add attributes to model for Thymeleaf
        model.addAttribute("title", "User | FMIS Proxy Interface");
        model.addAttribute("heading", "User");
        model.addAttribute("currentPage", "user");

        return "pages/user";
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
            Model model,
            HttpServletResponse response
    ) {
        // Check authentication
        String redirect = checkAdminAuth(isAdmin, adminUsername, adminPassword);
        if (redirect != null) return redirect;

        // Set cookies
        CookieUtils.setCookie(response, "isAdmin", "true", cookieLifetime);
        CookieUtils.setCookie(response, "adminUsername", adminUsername, cookieLifetime);
        CookieUtils.setCookie(response, "adminPassword", adminPassword, cookieLifetime);

        // Add attributes to model for Thymeleaf
        model.addAttribute("title", "Integration Gateway | FMIS Proxy Interface");
        model.addAttribute("heading", "Integration Gateway");
        model.addAttribute("currentPage", "fmis-config");

        return "pages/fmis-config";
    }
}