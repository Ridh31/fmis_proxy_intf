package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for performing CRUD operations on BankStatement entities.
 * This repository extends JpaRepository, providing built-in methods for common database operations.
 * Custom queries are defined using the @Query annotation.
 */
public interface BankStatementRepository extends JpaRepository<BankStatement, Long> {

    /**
     * Retrieves all active bank statements (status = TRUE) that are not deleted (is_deleted = FALSE).
     * The results are ordered by the bank statement ID in descending order.
     *
     * @param pageable The Pageable object for pagination and sorting.
     * @return A Page of BankStatement entities.
     */
    @Query(
            value = "SELECT bs.*, pi2.name FROM bank_statement bs " +
                    "LEFT JOIN partner_intf pi2 ON pi2.id = bs.partner_intf_id " +
                    "WHERE bs.status = TRUE AND bs.is_deleted = FALSE " +
                    "ORDER BY bs.id DESC",
            nativeQuery = true
    )
    Page<BankStatement> getAll(Pageable pageable);
}
