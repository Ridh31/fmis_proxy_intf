package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.SecurityServerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SecurityServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing SecurityServer entities.
 * Provides business logic and data access delegation for creating,
 * retrieving, and deleting security server records.
 */
@Service
public class SecurityServerServiceImpl implements SecurityServerService {

    @Autowired
    private SecurityServerRepository securityServerRepository;

    /**
     * Creates and saves a new SecurityServer entity.
     * Converts the provided key to uppercase and ensures uniqueness before saving.
     *
     * @param server the SecurityServer entity to create
     * @return the saved SecurityServer entity
     * @throws RuntimeException if a server with the same key already exists
     */
    @Override
    public SecurityServer create(SecurityServer server) {
        // Enforce uppercase and prevent duplication
        server.setKey(server.getKey().toUpperCase());

        if (securityServerRepository.existsByKey(server.getKey())) {
            throw new RuntimeException("Security server with key already exists: " + server.getKey());
        }

        return securityServerRepository.save(server);
    }

    /**
     * Retrieves a SecurityServer entity by its unique key.
     *
     * @param key the unique key to search for
     * @return an Optional containing the SecurityServer if found, or empty if not
     */
    @Override
    public Optional<SecurityServer> getByKey(String key) {
        return securityServerRepository.findByKey(key.toUpperCase());
    }

    /**
     * Retrieves all SecurityServer entities from the database.
     *
     * @return a list of all SecurityServer records
     */
    @Override
    public List<SecurityServer> getAll() {
        return securityServerRepository.findAll();
    }

    /**
     * Deletes a SecurityServer entity by its ID.
     *
     * @param id the ID of the SecurityServer to delete
     */
    @Override
    public void delete(Long id) {
        securityServerRepository.deleteById(id);
    }
}
