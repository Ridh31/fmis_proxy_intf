package com.fmis.fmis_proxy_intf.fmis_proxy_intf.controller;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.BankStatementRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.SarmisInterfaceRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ApiResponse;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseCodeUtil;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller that handles dashboard-related endpoints.
 * Provides summary data for displaying on dashboard UI.
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private BankStatementRepository bankStatementRepository;

    @Autowired
    private SarmisInterfaceRepository sarmisInterfaceRepository;

    /**
     * GET /dashboard/summary
     *
     * Returns a summary count of key entities for dashboard display.
     * @return ResponseEntity containing the summary counts
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getSummary() {
        Map<String, Long> summary = new HashMap<>();
        summary.put("bank_statement", bankStatementRepository.count());
        summary.put("sarmis_interface", sarmisInterfaceRepository.count());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        ResponseCodeUtil.fetched(),
                        ResponseMessageUtil.fetched("Summary"),
                        summary
                )
        );
    }
}