package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterface;

/**
 * Service interface for handling operations related to SarmisInterface.
 * Provides methods to manage and store interface logs.
 */
public interface SarmisInterfaceService {

    /**
     * Saves a SarmisInterface to the database.
     * This method persists the log object and returns the saved entity.
     *
     * @param sarmisInterface The log object to be saved.
     * @return The saved SarmisInterface entity.
     */
    SarmisInterface save(SarmisInterface sarmisInterface);
}