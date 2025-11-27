package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.CamDigiKeyLog;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for CamDigiKeyLog operations.
 */
public interface CamDigiKeyLogService {

    /**
     * Save a new CamDigiKey log entry.
     *
     * @param log the log entity to save
     * @return the saved log
     */
    CamDigiKeyLog save(CamDigiKeyLog log);

    /**
     * Retrieve all CamDigiKey log entries.
     *
     * @return list of logs
     */
    List<CamDigiKeyLog> findAll();

    /**
     * Retrieves a paginated list of active and non-deleted {@link CamDigiKeyLog} entities,
     * optionally filtered by the provided parameters.
     *
     * @param page        the zero-based page index
     * @param size        the number of records per page
     * @param action      optional filter by action
     * @param appKey      optional filter by app key
     * @param ipAddress   optional filter by IP address
     * @param requestURL  optional filter by request URL
     * @param createdDate optional filter by creation date (formatted as "dd-MM-yyyy")
     * @return a {@link Page} containing filtered {@link CamDigiKeyLog} results
     */
    Page<CamDigiKeyLog> getFilteredCamDigiKeyLogs(
            int page, int size,
            String action,
            String appKey,
            String ipAddress,
            String requestURL,
            String createdDate
    );
}