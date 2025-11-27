package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.CamDigiKeyLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CamDigiKeyLog.
 * Provides basic save and list operations.
 */
@Repository
public interface CamDigiKeyLogRepository extends JpaRepository<CamDigiKeyLog, Long> {

    /**
     * Retrieves a paginated list of active, non-deleted {@link CamDigiKeyLog} logs,
     * filtered by optional parameters: action, appKey, IP address, request URL, and creation date.
     * Results are ordered by ID descending.
     *
     * @param action      optional filter by action
     * @param appKey      optional filter by application key
     * @param ipAddress   optional filter by IP address
     * @param requestURL  optional filter by requested URL
     * @param createdDate optional filter by creation date ("dd-MM-yyyy")
     * @param pageable    pagination information
     * @return a page of filtered {@link CamDigiKeyLog} entities
     */
    @Query(value = """
        SELECT
            *
        FROM
            camdigikey_log cl
        WHERE
            cl.status = TRUE
            AND cl.is_deleted = FALSE
            AND (:action IS NULL OR cl.action = :action)
            AND (:appKey IS NULL OR cl.app_key = :appKey)
            AND (:ipAddress IS NULL OR cl.ip_address = :ipAddress)
            AND (:requestURL IS NULL OR cl.request_url = :requestURL)
            AND (:createdDate IS NULL OR DATE_FORMAT(cl.created_date, '%d-%m-%Y') = :createdDate)
        ORDER BY cl.id DESC
    """, nativeQuery = true)
    Page<CamDigiKeyLog> findFilteredCamDigiKeyLog(
            @Param("action") String action,
            @Param("appKey") String appKey,
            @Param("ipAddress") String ipAddress,
            @Param("requestURL") String requestURL,
            @Param("createdDate") String createdDate,
            Pageable pageable
    );
}