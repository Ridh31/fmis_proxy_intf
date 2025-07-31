package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterface;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

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

    /**
     * Retrieves a paginated list of SARMIS interface records using optional filters.
     * Filters include endpoint, interface code, purchase order ID, action date, and status.
     *
     * @param page            The page number (0-based).
     * @param size            The number of records per page.
     * @param endpoint        The endpoint to filter by (optional).
     * @param interfaceCode   The interface code to filter by (optional).
     * @param purchaseOrderId The purchase order ID to filter by (optional).
     * @param actionDate      The action date to filter by (optional).
     * @param status          The status to filter by (optional).
     * @return A {@link Page} of {@link SarmisInterface} entities matching the filters.
     */
    Page<SarmisInterface> getFilteredSarmisInterface(
            int page, int size,
            String endpoint,
            String interfaceCode,
            String purchaseOrderId,
            LocalDate actionDate,
            Boolean status
    );
}