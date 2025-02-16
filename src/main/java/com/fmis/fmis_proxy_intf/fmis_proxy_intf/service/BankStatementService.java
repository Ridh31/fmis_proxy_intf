package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing {@link BankStatement} entities.
 * This interface defines methods for creating and retrieving bank statements.
 */
public interface BankStatementService {

    /**
     * Creates and saves a new {@link BankStatement}.
     *
     * @param userId    The ID of the user creating the statement.
     * @param partnerId The ID of the partner associated with the statement.
     * @param statement The {@link BankStatementDTO.BankStatement} containing statement details.
     * @return The created {@link BankStatement} entity.
     */
    BankStatement createBankStatement(Long userId, Long partnerId, BankStatementDTO.BankStatement statement);

    /**
     * Retrieves a paginated list of all bank statements.
     *
     * @param page The page number to fetch.
     * @param size The number of items per page.
     * @return A {@link Page} of {@link BankStatement} entities.
     */
    Page<BankStatement> getAll(int page, int size);
}
