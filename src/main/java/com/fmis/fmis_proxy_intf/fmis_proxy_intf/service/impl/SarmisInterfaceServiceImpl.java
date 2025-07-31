package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterface;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.SarmisInterfaceRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SarmisInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Implementation of the SarmisInterfaceService interface.
 * Handles the business logic for interacting with the SarmisInterface repository.
 */
@Service
public class SarmisInterfaceServiceImpl implements SarmisInterfaceService {

    private final SarmisInterfaceRepository logRepository;
    private final SarmisInterfaceRepository sarmisInterfaceRepository;

    /**
     * Constructor-based injection for the repository.
     *
     * @param logRepository The repository for saving interface logs.
     */
    @Autowired
    public SarmisInterfaceServiceImpl(SarmisInterfaceRepository logRepository, SarmisInterfaceRepository sarmisInterfaceRepository) {
        this.logRepository = logRepository;
        this.sarmisInterfaceRepository = sarmisInterfaceRepository;
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

    /**
     * Returns a paginated list of SARMIS interface records filtered by optional parameters.
     * Filters include endpoint, interface code, purchase order ID, action date, and status.
     *
     * @param page            The page number (0-based).
     * @param size            The number of records per page.
     * @param endpoint        The endpoint to filter by (optional).
     * @param interfaceCode   The interface code to filter by (optional).
     * @param purchaseOrderId The purchase order ID to filter by (optional).
     * @param actionDate      The action date to filter by (optional).
     * @param status          The status to filter by (optional).
     * @return A {@link Page} of {@link SarmisInterface} matching the filters.
     */
    @Override
    public Page<SarmisInterface> getFilteredSarmisInterface(
            int page, int size,
            String endpoint,
            String interfaceCode,
            String purchaseOrderId,
            LocalDate actionDate,
            Boolean status) {

        Pageable pageable = PageRequest.of(page, size);
        return sarmisInterfaceRepository.findFilteredSarmisInterface(
                endpoint,
                interfaceCode,
                purchaseOrderId,
                actionDate,
                status,
                pageable);
    }
}