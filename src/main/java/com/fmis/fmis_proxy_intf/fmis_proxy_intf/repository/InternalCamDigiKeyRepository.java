package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    /**
     * Retrieves an {@link InternalCamDigiKey} entity by its unique app key.
     *
     * @param appKey the unique application key to search for
     * @return an {@link Optional} containing the matching entity, if found
     */
    Optional<InternalCamDigiKey> findByAppKey(String appKey);

    /**
     * Retrieves a paginated list of all active and non-deleted {@link InternalCamDigiKey} records.
     *
     * @param pageable the pagination information
     * @return a page of {@link InternalCamDigiKey} entities
     */
    @Query(value = """
        SELECT
            *
        FROM
            internal_camdigikey ic
        WHERE
            ic.status = TRUE
            AND ic.is_deleted = FALSE
        ORDER BY ic.id DESC
        """, nativeQuery = true)
    Page<InternalCamDigiKey> getAllInternalCamDigiKey(Pageable pageable);
}