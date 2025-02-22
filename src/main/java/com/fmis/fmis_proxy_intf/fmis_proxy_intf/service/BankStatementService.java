package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing {@link BankStatement} entities.
 * Defines methods for creating and retrieving bank statements.
 */
public interface BankStatementService {

    /**
     * Creates and saves a new {@link BankStatement}.
     *
     * @param partnerId        The ID of the associated partner.
     * @param bankStatementDTO The bank statement data transfer object containing required information.
     * @return The created {@link BankStatement} entity.
     */
    BankStatement createBankStatement(Long partnerId, BankStatementDTO bankStatementDTO);

    /**
     * Retrieves a paginated list of all bank statements.
     *
     * @param page The page number to fetch.
     * @param size The number of items per page.
     * @return A {@link Page} of {@link BankStatement} entities.
     */
    Page<BankStatement> getAllBankStatements(int page, int size);
}
