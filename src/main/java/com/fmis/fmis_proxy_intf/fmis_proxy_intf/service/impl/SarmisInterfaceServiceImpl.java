package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterface;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.SarmisInterfaceRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SarmisInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the SarmisInterfaceService interface.
 * Handles the business logic for interacting with the SarmisInterface repository.
 */
@Service
public class SarmisInterfaceServiceImpl implements SarmisInterfaceService {

    private final SarmisInterfaceRepository logRepository;

    /**
     * Constructor-based injection for the repository.
     *
     * @param logRepository The repository for saving interface logs.
     */
    @Autowired
    public SarmisInterfaceServiceImpl(SarmisInterfaceRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Saves a SarmisInterface entity to the database.
     *
     * @param sarmisInterface The SarmisInterface entity to be saved.
     * @return The saved SarmisInterface entity.
     */
    @Override
    public SarmisInterface save(SarmisInterface sarmisInterface) {
        return logRepository.save(sarmisInterface);
    }
}