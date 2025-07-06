package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterfaceLog;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.SarmisInterfaceLogRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SarmisInterfaceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the SarmisInterfaceLogService interface.
 * Handles the business logic for interacting with the SarmisInterfaceLog repository.
 */
@Service
public class SarmisInterfaceLogServiceImpl implements SarmisInterfaceLogService {

    private final SarmisInterfaceLogRepository logRepository;

    /**
     * Constructor-based injection for the repository.
     *
     * @param logRepository The repository for saving interface logs.
     */
    @Autowired
    public SarmisInterfaceLogServiceImpl(SarmisInterfaceLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Saves a SarmisInterfaceLog entity to the database.
     *
     * @param log The SarmisInterfaceLog entity to be saved.
     * @return The saved SarmisInterfaceLog entity.
     */
    @Override
    public SarmisInterfaceLog save(SarmisInterfaceLog log) {
        return logRepository.save(log);
    }
}