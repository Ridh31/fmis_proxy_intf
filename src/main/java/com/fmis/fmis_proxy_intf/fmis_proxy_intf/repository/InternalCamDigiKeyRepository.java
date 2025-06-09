package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link InternalCamDigiKey} entities.
 * Extends {@link JpaRepository} to provide standard CRUD and query operations.
 */
public interface InternalCamDigiKeyRepository extends JpaRepository<InternalCamDigiKey, Long> {

    /**
     * Checks if an {@link InternalCamDigiKey} exists with the given name.
     *
     * @param name The name to check.
     * @return {@code true} if a record exists with the given name, otherwise {@code false}.
     */
    boolean existsByName(String name);

    /**
     * Checks if an {@link InternalCamDigiKey} exists with the given app key.
     *
     * @param appKey The app key to check.
     * @return {@code true} if a record exists with the given app key, otherwise {@code false}.
     */
    boolean existsByAppKey(String appKey);

    /**
     * Checks if an {@link InternalCamDigiKey} exists with the given IP address.
     *
     * @param ipAddress The IP address to check.
     * @return {@code true} if a record exists with the given IP address, otherwise {@code false}.
     */
    boolean existsByIpAddress(String ipAddress);

    /**
     * Checks if an {@link InternalCamDigiKey} exists with the given access URL.
     *
     * @param accessURL The access URL to check.
     * @return {@code true} if a record exists with the given access URL, otherwise {@code false}.
     */
    boolean existsByAccessURL(String accessURL);

    /**
     * Retrieves an {@link InternalCamDigiKey} by its ID.
     *
     * @param id The unique identifier of the entity.
     * @return An {@link Optional} containing the entity if found, otherwise empty.
     */
    Optional<InternalCamDigiKey> findById(Long id);
}