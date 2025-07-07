package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.SecurityServerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.SecurityServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link SecurityServerService} for managing security server configurations.
 * Handles business logic for creating, retrieving, and deleting {@link SecurityServer} entities.
 */
@Service
public class SecurityServerServiceImpl implements SecurityServerService {

    private final SecurityServerRepository securityServerRepository;

    @Autowired
    public SecurityServerServiceImpl(SecurityServerRepository securityServerRepository) {
        this.securityServerRepository = securityServerRepository;
    }

    /**
     * Creates and saves a new {@link SecurityServer}.
     * Converts the config key to uppercase and ensures it is unique before persisting.
     *
     * @param server the SecurityServer entity to be created
     * @return the saved SecurityServer
     * @throws RuntimeException if a server with the same config key already exists
     */
    @Override
    public SecurityServer create(SecurityServer server) {
        String normalizedKey = server.getConfigKey().toUpperCase();
        server.setConfigKey(normalizedKey);

        if (securityServerRepository.existsByConfigKey(normalizedKey)) {
            throw new RuntimeException("Security server with config key already exists: " + normalizedKey);
        }

        return securityServerRepository.save(server);
    }

    /**
     * Retrieves a {@link SecurityServer} by its unique configuration key.
     *
     * @param configKey the unique config key
     * @return an {@link Optional} containing the server if found, or empty otherwise
     */
    @Override
    public Optional<SecurityServer> getByConfigKey(String configKey) {
        return securityServerRepository.findByConfigKey(configKey.toUpperCase());
    }

    /**
     * Retrieves all {@link SecurityServer} entities.
     *
     * @return a list of all configured security servers
     */
    @Override
    public List<SecurityServer> getAll() {
        return securityServerRepository.findAll();
    }

    /**
     * Deletes a {@link SecurityServer} by its ID.
     *
     * @param id the ID of the SecurityServer to delete
     */
    @Override
    public void delete(Long id) {
        securityServerRepository.deleteById(id);
    }
}
