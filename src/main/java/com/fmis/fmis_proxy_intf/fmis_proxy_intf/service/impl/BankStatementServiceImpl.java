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

/**
 * Implementation of {@link BankStatementService} for handling bank statement operations.
 */
@Service
public class BankStatementServiceImpl implements BankStatementService {

    private final BankStatementRepository bankStatementRepository;
    private final PartnerRepository partnerRepository;

    /**
     * Constructs a new {@code BankStatementServiceImpl} with the given repositories.
     *
     * @param bankStatementRepository The repository for managing {@code BankStatement} entities.
     * @param partnerRepository       The repository for managing {@code Partner} entities.
     */
    public BankStatementServiceImpl(BankStatementRepository bankStatementRepository,
                                    PartnerRepository partnerRepository) {
        this.bankStatementRepository = bankStatementRepository;
        this.partnerRepository = partnerRepository;
    }

    /**
     * Creates a new bank statement associated with a specific user and partner.
     *
     * @param userId    The ID of the user creating the bank statement.
     * @param partnerId The ID of the partner associated with the bank statement.
     * @param statement The DTO containing bank statement details.
     * @return The saved {@code BankStatement} entity.
     */
    @Transactional
    @Override
    public BankStatement createBankStatement(Long userId, Long partnerId, BankStatementDTO.BankStatement statement) {

        // Retrieve the Partner entity or throw an exception if not found
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        // Map DTO to BankStatement entity
        BankStatement bankStatement = new BankStatement();
        bankStatement.setCmbBspStmtDt(statement.getCmbBspStmtDt());
        bankStatement.setCmbBankAccountN(statement.getCmbBankAccountN());
        bankStatement.setCmbCurrencyCd(statement.getCmbCurrencyCd());
        bankStatement.setCmbValueDt(statement.getCmbValueDt());
        bankStatement.setCmbBankStmtType(statement.getCmbBankStmtType());
        bankStatement.setCmbBspTranAmt(statement.getCmbBspTranAmt());
        bankStatement.setCmbOpenBalance(statement.getCmbOpenBalance());
        bankStatement.setCmbEndBalance(statement.getCmbEndBalance());
        bankStatement.setCmbImmediateBal(statement.getCmbImmediateBalance());
        bankStatement.setCmbReconRefId(statement.getCmbReconRefId());
        bankStatement.setCmbCheckNumber(statement.getCmbCheckNumber());
        bankStatement.setCmbDescrLong(statement.getCmbDescrlong());
        bankStatement.setCmbLetterNumber(statement.getCmbLetterNumber());
        bankStatement.setCreatedBy(userId);
        bankStatement.setPartner(partner); // Associate the partner

        // Save and return the bank statement entity
        return bankStatementRepository.save(bankStatement);
    }

    /**
     * Fetches a page of active and non-deleted bank statements.
     *
     * @param page The page number to fetch.
     * @param size The size of each page.
     * @return A Page of BankStatement entities.
     */
    @Override
    public Page<BankStatement> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bankStatementRepository.getAll(pageable);
    }
}
