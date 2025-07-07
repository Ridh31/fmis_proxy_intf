package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing {@link SecurityServer} entities.
 * Defines operations for creating, retrieving, and deleting security server configurations.
 */
public interface SecurityServerService {

    /**
     * Creates and saves a new {@link SecurityServer}.
     *
     * @param server the SecurityServer to create
     * @return the saved SecurityServer
     */
    SecurityServer create(SecurityServer server);

    /**
     * Retrieves a {@link SecurityServer} by its unique configuration key.
     *
     * @param configKey the unique config key
     * @return an {@link Optional} containing the SecurityServer if found, or empty if not
     */
    Optional<SecurityServer> getByConfigKey(String configKey);

    /**
     * Retrieves all {@link SecurityServer} entities.
     *
     * @return a list of all SecurityServer records
     */
    List<SecurityServer> getAll();

    /**
     * Deletes a {@link SecurityServer} by its ID.
     *
     * @param id the ID of the SecurityServer to delete
     */
    void delete(Long id);
}
