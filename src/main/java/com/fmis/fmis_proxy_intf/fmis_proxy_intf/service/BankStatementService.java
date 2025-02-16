package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;

/**
 * Service interface for managing {@link BankStatement} entities.
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
}
