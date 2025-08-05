package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.SecurityServerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SecurityServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the SecurityServerService interface.
 * Provides business logic for managing SecurityServer entities.
 */
@Service
public class SecurityServerServiceImpl implements SecurityServerService {

    private final SecurityServerRepository securityServerRepository;

    /**
     * Constructor for initializing the SecurityServerServiceImpl with the SecurityServerRepository.
     *
     * @param securityServerRepository The repository for accessing SecurityServer data.
     */
    @Autowired
    public SecurityServerServiceImpl(SecurityServerRepository securityServerRepository) {
        this.securityServerRepository = securityServerRepository;
    }

    /**
     * Creates and saves a new SecurityServer entity.
     *
     * @param server The SecurityServer entity to create.
     * @return The saved SecurityServer entity.
     */
    @Override
    public SecurityServer create(SecurityServer server) {
        return securityServerRepository.save(server);
    }

    /**
     * Retrieves a SecurityServer by its unique configuration key.
     *
     * @param configKey The unique configuration key of the SecurityServer.
     * @return An Optional containing the SecurityServer if found, otherwise empty.
     */
    @Override
    public Optional<SecurityServer> getByConfigKey(String configKey) {
        return securityServerRepository.findByConfigKey(configKey);
    }

    /**
     * Retrieves all SecurityServer entities.
     *
     * @return A list of all SecurityServer entities.
     */
    @Override
    public List<SecurityServer> getAll() {
        return securityServerRepository.findAll();
    }

    /**
     * Deletes a SecurityServer by its ID.
     *
     * @param id The ID of the SecurityServer to delete.
     */
    @Override
    public void delete(Long id) {
        securityServerRepository.deleteById(id);
    }

    /**
     * Checks if a SecurityServer with the given name already exists.
     *
     * @param name The name of the SecurityServer.
     * @return true if a SecurityServer with the same name exists, otherwise false.
     */
    @Override
    public boolean existsByName(String name) {
        return securityServerRepository.existsByName(name);
    }

    /**
     * Checks if a SecurityServer with the given configKey already exists.
     *
     * @param configKey The configKey of the SecurityServer.
     * @return true if a SecurityServer with the same configKey exists, otherwise false.
     */
    @Override
    public boolean existsByConfigKey(String configKey) {
        return securityServerRepository.existsByConfigKey(configKey);
    }

    /**
     * Retrieves a paginated list of active and non-deleted {@link SecurityServer} entities,
     * filtered optionally by the provided parameters.
     * The filtering is applied based on parameters such as name, config key, description, and creation date.
     *
     * @param page        the zero-based page index
     * @param size        the number of records per page
     * @param name        optional filter by name
     * @param configKey   optional filter by config key
     * @param description optional filter by description
     * @param createdDate optional filter by creation date in "dd-MM-yyyy" format
     * @return a {@link Page} containing the filtered {@link SecurityServer} results
     */
    @Override
    public Page<SecurityServer> getFilteredSecurityServers(
            int page, int size,
            String name,
            String configKey,
            String description,
            LocalDate createdDate) {

        Pageable pageable = PageRequest.of(page, size);

        return securityServerRepository.findFilteredSecurityServers(
                name, configKey, description, createdDate, pageable);
    }
}