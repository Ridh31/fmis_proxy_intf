package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Repository interface for accessing and manipulating SarmisInterface data.
 * Extends JpaRepository to provide CRUD operations for the SarmisInterface entity.
 */
@Repository
public interface SarmisInterfaceRepository extends JpaRepository<SarmisInterface, Long> {

    /**
     * Retrieves a paginated list of SARMIS interface records filtered by optional parameters:
     * endpoint, interface code, purchase order ID (from payload), action date, and status.
     * Only non-deleted records are returned, sorted by descending ID.
     *
     * @param endpoint        The interface endpoint to filter by (optional).
     * @param interfaceCode   The interface code to filter by (optional, supports partial match).
     * @param purchaseOrderId The purchase order ID to filter by (optional, searched within payload).
     * @param actionDate      The action date to filter by (optional, filters records on that specific date).
     * @param status          The status to filter by (optional).
     * @param pageable        The {@link Pageable} object for pagination and sorting.
     * @return A {@link Page} of {@link SarmisInterface} entities matching the filter criteria.
     */
    @Query(value = """
        SELECT
            *
        FROM
            sarmis_interface si
        WHERE
            si.is_deleted = FALSE
            AND (
                :endpoint IS NULL
                OR si.endpoint = :endpoint
                OR si.endpoint LIKE CONCAT(:endpoint, '?%')
            )
            AND (:interfaceCode IS NULL OR si.interface_code LIKE CONCAT('%', :interfaceCode, '%'))
            AND (:purchaseOrderId IS NULL OR payload LIKE CONCAT('%', :purchaseOrderId, '%'))
            AND (:actionDate IS NULL OR (
                 si.created_date >= :actionDate AND si.created_date < DATE_ADD(:actionDate, INTERVAL 1 DAY)))
            AND (:status IS NULL OR si.status = :status)
        ORDER BY si.id DESC
        """, nativeQuery = true)
    Page<SarmisInterface> findFilteredSarmisInterface(
            @Param("endpoint") String endpoint,
            @Param("interfaceCode") String interfaceCode,
            @Param("purchaseOrderId") String purchaseOrderId,
            @Param("actionDate") LocalDate actionDate,
            @Param("status") Boolean status,
            Pageable pageable);
}