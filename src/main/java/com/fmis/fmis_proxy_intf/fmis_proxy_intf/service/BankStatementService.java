package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

/**
 * Service interface for managing {@link BankStatement} entities.
 * Defines methods for creating and retrieving bank statements.
 */
public interface BankStatementService {

    /**
     * Creates and saves a new {@link BankStatement} associated with a partner.
     *
     * @param partnerId        The ID of the partner associated with the bank statement.
     * @param bankStatementDTO The {@link BankStatementDTO} containing the details to create the bank statement.
     * @return The created {@link BankStatement} entity.
     */
    BankStatement createBankStatement(Long partnerId, BankStatementDTO bankStatementDTO);

    /**
     * Retrieves a paginated list of all bank statements.
     *
     * @param page The page number to fetch (starting from 0).
     * @param size The number of items per page.
     * @return A {@link Page} containing the list of {@link BankStatement} entities.
     */
    Page<BankStatement> getAllBankStatements(int page, int size);

    /**
     * Retrieves a paginated list of bank statements based on the provided filters.
     *
     * @param page              The page number to fetch (starting from 0).
     * @param size              The number of items per page.
     * @param bankAccountNumber The bank account number to filter by (optional).
     * @param statementDate     The statement date to filter by (optional).
     * @param importedDate      The imported date to filter by (optional).
     * @return A {@link Page}   containing the filtered list of {@link BankStatement} entities.
     */
    Page<BankStatement> getFilteredBankStatements(int page, int size, String bankAccountNumber, LocalDate statementDate, LocalDate importedDate);
}