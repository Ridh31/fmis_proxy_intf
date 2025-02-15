package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations on BankStatement entities.
 */
public interface BankStatementRepository extends JpaRepository<BankStatement, Long> {
}
