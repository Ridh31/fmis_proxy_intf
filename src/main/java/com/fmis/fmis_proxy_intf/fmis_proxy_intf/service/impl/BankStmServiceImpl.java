package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.BankStmRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.BankStmService;
import org.springframework.stereotype.Service;

/**
 * Implementation of BankStmService interface.
 */
@Service
public class BankStmServiceImpl implements BankStmService {

    private final BankStmRepository bankStmRepository;

    /**
     * Constructs a new BankStmRepository with the given repository.
     *
     * @param bankStmRepository The repository for managing Test entities.
     */
    public BankStmServiceImpl(BankStmRepository bankStmRepository) {
        this.bankStmRepository = bankStmRepository;
    }
}
