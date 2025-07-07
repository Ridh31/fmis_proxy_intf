package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link SecurityServer} entities.
 * Provides CRUD operations and custom query methods.
 */
public interface SecurityServerRepository extends JpaRepository<SecurityServer, Long> {

    /**
     * Retrieves a SecurityServer by its unique configuration key.
     *
     * @param configKey the unique configuration key
     * @return an {@link Optional} containing the found SecurityServer, or empty if not found
     */
    Optional<SecurityServer> findByConfigKey(String configKey);

    /**
     * Checks whether a SecurityServer with the given configuration key exists.
     *
     * @param configKey the unique configuration key
     * @return true if a matching SecurityServer exists, false otherwise
     */
    boolean existsByConfigKey(String configKey);
}