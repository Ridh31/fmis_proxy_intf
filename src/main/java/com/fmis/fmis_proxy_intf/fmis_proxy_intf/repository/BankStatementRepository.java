package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

/**
 * Repository interface for performing CRUD operations on {@link BankStatement} entities.
 * This repository extends {@link JpaRepository}, providing built-in methods for common database operations.
 * Custom queries are defined using the {@link Query} annotation.
 */
public interface BankStatementRepository extends JpaRepository<BankStatement, Long> {

    /**
     * Retrieves all active bank statements (status = TRUE) that are not deleted (is_deleted = FALSE).
     * The results are ordered by the bank statement ID in descending order.
     *
     * @param pageable The {@link Pageable} object for pagination and sorting.
     * @return A {@link Page} of {@link BankStatement} entities.
     */
    @Query(
            value = """
                    SELECT
                        bs.*, pi2.name
                    FROM
                        bank_statement bs
                        LEFT JOIN partner_intf pi2
                        ON pi2.id = bs.partner_intf_id
                    WHERE
                        bs.is_deleted = FALSE
                    ORDER BY bs.id DESC
                    """,
            nativeQuery = true
    )
    Page<BankStatement> getAllBankStatements(Pageable pageable);

    /**
     * Retrieves bank statements filtered by optional parameters: bank account number,
     * statement date, imported date, partner ID, and status. Returns only non-deleted bank statements.
     *
     * @param partnerId          The partner ID to filter by (optional).
     * @param bankAccountNumber  The bank account number to filter by (optional).
     * @param statementId        The statement ID to filter by (optional).
     * @param statementDate      The statement date to filter by (optional).
     * @param importedDate       The imported date to filter by (optional).
     * @param status             The status (true/false) to filter by (optional).
     * @param pageable           The {@link Pageable} object for pagination and sorting.
     * @return A {@link Page} of {@link BankStatement} entities matching the filter criteria.
     */
    @Query(value = """
        SELECT
            bs.*,
            pi2.name AS branch,
            pi2.name AS partner_identifier,
            u.username AS imported_by
        FROM
            bank_statement bs
            LEFT JOIN partner_intf pi2 ON pi2.id = bs.partner_intf_id
            LEFT JOIN user u ON u.id = bs.created_by
        WHERE bs.is_deleted = FALSE
            AND (:bankAccountNumber IS NULL OR bs.bank_account_number = :bankAccountNumber)
            AND (:statementId IS NULL OR bs.statement_id LIKE CONCAT('%', :statementId, '%'))
            AND (:statementDate IS NULL OR bs.statement_date >= :statementDate 
                AND bs.statement_date < DATE_ADD(:statementDate, INTERVAL 1 DAY))
            AND (:importedDate IS NULL OR bs.created_date >= :importedDate 
                AND bs.created_date < DATE_ADD(:importedDate, INTERVAL 1 DAY))
            AND (:partnerId IS NULL OR bs.partner_intf_id = :partnerId)
            AND (:status IS NULL OR bs.status = :status)
        ORDER BY bs.id DESC
        """, nativeQuery = true)
    Page<BankStatement> findFilteredBankStatements(
            @Param("partnerId") Long partnerId,
            @Param("bankAccountNumber") String bankAccountNumber,
            @Param("statementId") String statementId,
            @Param("statementDate") LocalDate statementDate,
            @Param("importedDate") LocalDate importedDate,
            @Param("status") Boolean status,
            Pageable pageable);
}