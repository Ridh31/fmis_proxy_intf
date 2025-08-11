package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
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

    /**
     * Checks if a SecurityServer with the given name exists.
     *
     * @param name the name of the SecurityServer
     * @return true if a SecurityServer with the same name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if a SecurityServer with the given configKey exists.
     *
     * @param configKey the configKey of the SecurityServer
     * @return true if a SecurityServer with the same configKey exists, false otherwise
     */
    boolean existsByConfigKey(String configKey);

    /**
     * Retrieves a {@link SecurityServer} by its unique ID.
     *
     * @param id the ID of the SecurityServer to retrieve
     * @return an {@link Optional} containing the SecurityServer if found, or empty if not
     */
    Optional<SecurityServer> findById(Long id);

    /**
     * Retrieves a {@link SecurityServer} by its unique name.
     *
     * @param name the name of the SecurityServer to find
     * @return an {@link Optional} containing the SecurityServer if found, or empty otherwise
     */
    Optional<SecurityServer> findByName(String name);

    /**
     * Updates an existing {@link SecurityServer} entity.
     * If the entity does not exist, it will be created.
     *
     * @param server the {@link SecurityServer} entity with updated information
     * @return the updated {@link SecurityServer} entity
     */
    SecurityServer update(SecurityServer server);

    /**
     * Retrieves a paginated list of active and non-deleted {@link SecurityServer} entities,
     * optionally filtered by the provided parameters.
     * The method applies filters for name, config key, description, and creation date.
     * If any parameter is null, it is ignored in the query.
     *
     * @param page        the zero-based page index (starting from 0)
     * @param size        the number of records to return per page
     * @param name        optional filter by the name of the security server
     * @param configKey   optional filter by the configuration key
     * @param description optional filter by the description of the security server
     * @param createdDate optional filter by the creation date in "dd-MM-yyyy" format
     * @return a {@link Page} of filtered {@link SecurityServer} results, which may be empty if no records match the criteria
     */
    Page<SecurityServer> getFilteredSecurityServers(
            int page, int size,
            String name,
            String configKey,
            String description,
            LocalDate createdDate
    );
}
