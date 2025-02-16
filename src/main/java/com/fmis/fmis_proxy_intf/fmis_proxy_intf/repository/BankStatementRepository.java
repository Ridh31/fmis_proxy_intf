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
     * Retrieves all bank statements that are active (status = TRUE) and not deleted (is_deleted = FALSE).
     * Results are ordered by ID in descending order.
     *
     * @param pageable The Pageable object for pagination and sorting.
     * @return A Page of BankStatement entities.
     */
    @Query(
            value = "SELECT * FROM cmb_bankstm_stg cbs WHERE status = TRUE AND is_deleted = FALSE ORDER BY id DESC",
            nativeQuery = true
    )
    Page<BankStatement> getAll(Pageable pageable);
}
