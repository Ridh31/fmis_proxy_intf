package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterfaceLog;

/**
 * Service interface for handling operations related to SarmisInterfaceLog.
 * Provides methods to manage and store interface logs.
 */
public interface SarmisInterfaceLogService {

    /**
     * Saves a SarmisInterfaceLog to the database.
     * This method persists the log object and returns the saved entity.
     *
     * @param log The log object to be saved.
     * @return The saved SarmisInterfaceLog entity.
     */
    SarmisInterfaceLog save(SarmisInterfaceLog log);
}