package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;

import java.util.List;
import java.util.Optional;

/**
 * Service interface defining operations for managing SecurityServer entities.
 */
public interface SecurityServerService {

    /**
     * Creates a new SecurityServer entity.
     *
     * @param server the SecurityServer entity to create
     * @return the created SecurityServer entity
     */
    SecurityServer create(SecurityServer server);

    /**
     * Retrieves a SecurityServer entity by its unique key.
     *
     * @param key the unique key of the SecurityServer
     * @return an Optional containing the SecurityServer if found, otherwise empty
     */
    Optional<SecurityServer> getByKey(String key);

    /**
     * Retrieves all SecurityServer entities.
     *
     * @return a list of all SecurityServer entities
     */
    List<SecurityServer> getAll();

    /**
     * Deletes the SecurityServer entity with the specified ID.
     *
     * @param id the ID of the SecurityServer to delete
     */
    void delete(Long id);
}
