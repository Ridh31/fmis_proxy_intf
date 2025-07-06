package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for SecurityServer entities.
 * Extends JpaRepository to provide CRUD operations and
 * custom query methods for SecurityServer.
 */
public interface SecurityServerRepository extends JpaRepository<SecurityServer, Long> {

    /**
     * Finds a SecurityServer entity by its unique key.
     *
     * @param key the unique key of the SecurityServer
     * @return an Optional containing the SecurityServer if found, otherwise empty
     */
    Optional<SecurityServer> findByKey(String key);

    /**
     * Checks if a SecurityServer entity exists with the given key.
     *
     * @param key the unique key to check
     * @return true if a SecurityServer with the key exists, false otherwise
     */
    boolean existsByKey(String key);
}
