package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto.BankStatementDTO;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.BankStatement;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.BankStatementRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStatementService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Implementation of {@link BankStatementService} to manage bank statement operations.
 */
@Service
public class BankStatementServiceImpl implements BankStatementService {

    private final BankStatementRepository bankStatementRepository;
    private final PartnerRepository partnerRepository;

    /**
     * Constructor for {@code BankStatementServiceImpl}.
     *
     * @param bankStatementRepository Repository for {@code BankStatement} entities.
     * @param partnerRepository       Repository for {@code Partner} entities.
     */
    public BankStatementServiceImpl(BankStatementRepository bankStatementRepository,
                                    PartnerRepository partnerRepository) {
        this.bankStatementRepository = bankStatementRepository;
        this.partnerRepository = partnerRepository;
    }

    /**
     * Creates a new bank statement associated with a specific partner.
     *
     * @param partnerId       The ID of the partner.
     * @param bankStatementDTO The DTO containing the bank statement details.
     * @return The saved {@code BankStatement} entity.
     * @throws RuntimeException If the partner is not found.
     */
    @Transactional
    @Override
    public BankStatement createBankStatement(Long partnerId, BankStatementDTO bankStatementDTO) {
        // Retrieve the Partner entity or throw an exception if not found
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        // Convert the DTO to the BankStatement entity
        BankStatement bankStatement = new BankStatement();
        bankStatement.setPartner(partner);
        bankStatement.setMethod(bankStatementDTO.getMethod());
        bankStatement.setEndpoint(bankStatementDTO.getEndpoint());
        bankStatement.setFilename(bankStatementDTO.getFilename());
        bankStatement.setBankAccountNumber(bankStatementDTO.getBankAccountNumber());
        bankStatement.setStatementDate(bankStatementDTO.getStatementDate());
        bankStatement.setPayload(bankStatementDTO.getPayload());
        bankStatement.setXml(bankStatementDTO.getXml());
        bankStatement.setMessage(bankStatementDTO.getMessage());
        bankStatement.setCreatedBy(bankStatementDTO.getCreatedBy());
        bankStatement.setStatus(bankStatementDTO.getStatus());

        // Save and return the bank statement entity
        return bankStatementRepository.save(bankStatement);
    }

    /**
     * Retrieves a paginated list of all active, non-deleted bank statements.
     *
     * @param page The page number to fetch (starting from 0).
     * @param size The size of each page (items per page).
     * @return A {@link Page} of {@link BankStatement} entities.
     */
    @Override
    public Page<BankStatement> getAllBankStatements(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bankStatementRepository.getAllBankStatements(pageable);
    }

    /**
     * Retrieves a paginated list of filtered bank statements based on the provided filters.
     * Filters include partner ID, bank account number, statement date, imported date, and status.
     * Only non-deleted bank statements are returned.
     *
     * @param page              The page number to fetch (starting from 0).
     * @param size              The size of each page (items per page).
     * @param partnerId         The partner ID to filter by (optional).
     * @param bankAccountNumber The bank account number to filter by (optional).
     * @param statementDate     The statement date to filter by (optional).
     * @param importedDate      The imported date to filter by (optional).
     * @param status            The status (true/false) to filter by (optional).
     * @return A {@link Page} of {@link BankStatement} entities matching the filter criteria.
     */
    @Override
    public Page<BankStatement> getFilteredBankStatements(
            int page, int size,
            Long partnerId,
            String bankAccountNumber,
            LocalDate statementDate,
            LocalDate importedDate,
            Boolean status) {

        Pageable pageable = PageRequest.of(page, size);
        return bankStatementRepository.findFilteredBankStatements(
                partnerId,
                bankAccountNumber,
                statementDate,
                importedDate,
                status,
                pageable);
    }
}