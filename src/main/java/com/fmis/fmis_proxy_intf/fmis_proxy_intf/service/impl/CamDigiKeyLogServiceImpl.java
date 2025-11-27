package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.CamDigiKeyLog;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.CamDigiKeyLogRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.CamDigiKeyLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service implementation for CamDigiKey log operations.
 * Handles saving new logs and retrieving all logs for monitoring purposes.
 */
@Service
public class CamDigiKeyLogServiceImpl implements CamDigiKeyLogService {

    private final CamDigiKeyLogRepository camDigiKeyLogRepository;

    /**
     * Constructor for dependency injection of the log repository.
     *
     * @param camDigiKeyLogRepository Repository for CamDigiKeyLog entities
     */
    public CamDigiKeyLogServiceImpl(CamDigiKeyLogRepository camDigiKeyLogRepository) {
        this.camDigiKeyLogRepository = camDigiKeyLogRepository;
    }

    /**
     * Save a CamDigiKey log entry to the database.
     *
     * @param log Log entity to save
     * @return The saved log entity
     */
    @Override
    public CamDigiKeyLog save(CamDigiKeyLog log) {
        return camDigiKeyLogRepository.save(log);
    }

    /**
     * Retrieve all CamDigiKey log entries from the database.
     *
     * @return List of all logs
     */
    @Override
    public List<CamDigiKeyLog> findAll() {
        return camDigiKeyLogRepository.findAll();
    }

    /**
     * Returns a paginated list of active, non-deleted {@link CamDigiKeyLog} logs,
     * optionally filtered by action, application key, IP address, access URL, and creation date.
     *
     * @param page        zero-based page index
     * @param size        number of records per page
     * @param action      optional filter by action
     * @param appKey      optional filter by application key
     * @param ipAddress   optional filter by IP address
     * @param requestURL  optional filter by request URL
     * @param createdDate optional filter by creation date in "dd-MM-yyyy" format
     * @return a {@link Page} of {@link CamDigiKeyLog} matching the given filters
     */
    @Override
    public Page<CamDigiKeyLog> getFilteredCamDigiKeyLogs(
            int page, int size,
            String action,
            String appKey,
            String ipAddress,
            String requestURL,
            String createdDate) {

        Pageable pageable = PageRequest.of(page, size);
        return camDigiKeyLogRepository.findFilteredCamDigiKeyLog(
                action, appKey, ipAddress, requestURL, createdDate, pageable);
    }
}