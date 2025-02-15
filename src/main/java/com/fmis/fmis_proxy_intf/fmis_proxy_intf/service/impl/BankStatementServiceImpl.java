package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.BankStatementRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStatementService;
import org.springframework.stereotype.Service;

/**
 * Implementation of BankStatementService interface.
 */
@Service
public class BankStatementServiceImpl implements BankStatementService {

    /**
     * Constructs a new BankStatementRepository with the given repository.
     *
     * @param bankStatementRepository The repository for managing Test entities.
     */
    public BankStatementServiceImpl(BankStatementRepository bankStatementRepository) {
    }
}
