package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import java.util.Optional;

/**
 * Service interface for managing {@link InternalCamDigiKey} entities.
 * Provides method contracts for creating, checking, and retrieving internal camera DigiKey records.
 */
public interface InternalCamDigiKeyService {

    /**
     * Creates and saves a new {@link InternalCamDigiKey} entity.
     *
     * @param internalCamDigiKey The entity to be created and persisted.
     * @return The saved {@link InternalCamDigiKey} entity.
     */
    InternalCamDigiKey createInternalCamDigiKey(InternalCamDigiKey internalCamDigiKey);

    /**
     * Checks if an entity exists by name.
     *
     * @param name The name to check for uniqueness.
     * @return {@code true} if a record with the name exists, otherwise {@code false}.
     */
    boolean existsByName(String name);

    /**
     * Checks if an entity exists by app key.
     *
     * @param appKey The application key to check for uniqueness.
     * @return {@code true} if a record with the app key exists, otherwise {@code false}.
     */
    boolean existsByAppKey(String appKey);

    /**
     * Checks if an entity exists by IP address.
     *
     * @param ipAddress The IP address to check for uniqueness.
     * @return {@code true} if a record with the IP address exists, otherwise {@code false}.
     */
    boolean existsByIpAddress(String ipAddress);

    /**
     * Checks if an entity exists by access URL.
     *
     * @param accessURL The access URL to check for uniqueness.
     * @return {@code true} if a record with the access URL exists, otherwise {@code false}.
     */
    boolean existsByAccessURL(String accessURL);

    /**
     * Retrieves an {@link InternalCamDigiKey} by its unique ID.
     *
     * @param id The unique identifier of the entity.
     * @return An {@link Optional} containing the entity if found, otherwise empty.
     */
    Optional<InternalCamDigiKey> findById(Long id);
}