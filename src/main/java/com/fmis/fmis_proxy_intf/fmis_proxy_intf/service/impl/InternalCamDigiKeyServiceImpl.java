package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.InternalCamDigiKeyRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.InternalCamDigiKeyService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for {@link InternalCamDigiKeyService}.
 * Handles business logic and persistence for {@link InternalCamDigiKey} entities.
 */
@Service
public class InternalCamDigiKeyServiceImpl implements InternalCamDigiKeyService {

    private final InternalCamDigiKeyRepository internalCamDigiKeyRepository;

    /**
     * Constructor for injecting the repository dependency.
     *
     * @param internalCamDigiKeyRepository Repository for managing CamDigiKey records.
     */
    public InternalCamDigiKeyServiceImpl(InternalCamDigiKeyRepository internalCamDigiKeyRepository) {
        this.internalCamDigiKeyRepository = internalCamDigiKeyRepository;
    }

    /**
     * Creates and persists a new {@link InternalCamDigiKey} entity.
     *
     * @param internalCamDigiKey The CamDigiKey entity to create.
     * @return The persisted entity.
     */
    @Transactional
    @Override
    public InternalCamDigiKey createInternalCamDigiKey(InternalCamDigiKey internalCamDigiKey) {
        return internalCamDigiKeyRepository.save(internalCamDigiKey);
    }

    /**
     * Checks if a CamDigiKey exists by its name.
     *
     * @param name The name to check.
     * @return {@code true} if a record exists; {@code false} otherwise.
     */
    @Override
    public boolean existsByName(String name) {
        return internalCamDigiKeyRepository.existsByName(name);
    }

    /**
     * Checks if a CamDigiKey exists by its app key.
     *
     * @param appKey The app key to check.
     * @return {@code true} if a record exists; {@code false} otherwise.
     */
    @Override
    public boolean existsByAppKey(String appKey) {
        return internalCamDigiKeyRepository.existsByAppKey(appKey);
    }

    /**
     * Checks if a CamDigiKey exists by its IP address.
     *
     * @param ipAddress The IP address to check.
     * @return {@code true} if a record exists; {@code false} otherwise.
     */
    @Override
    public boolean existsByIpAddress(String ipAddress) {
        return internalCamDigiKeyRepository.existsByIpAddress(ipAddress);
    }

    /**
     * Checks if a CamDigiKey exists by its access URL.
     *
     * @param accessURL The access URL to check.
     * @return {@code true} if a record exists; {@code false} otherwise.
     */
    @Override
    public boolean existsByAccessURL(String accessURL) {
        return internalCamDigiKeyRepository.existsByAccessURL(accessURL);
    }

    /**
     * Retrieves a CamDigiKey by its ID.
     *
     * @param id The unique identifier of the CamDigiKey.
     * @return An {@link Optional} containing the entity if found; otherwise empty.
     */
    @Override
    public Optional<InternalCamDigiKey> findById(Long id) {
        return internalCamDigiKeyRepository.findById(id);
    }

    /**
     * Finds an {@link InternalCamDigiKey} entity by its app key.
     *
     * @param appKey the application key used to identify the internal camera DigiKey
     * @return an {@link Optional} containing the found {@link InternalCamDigiKey}, or empty if not found
     */
    @Override
    public Optional<InternalCamDigiKey> findByAppKey(String appKey) {
        return internalCamDigiKeyRepository.findByAppKey(appKey);
    }

    /**
     * Retrieves a paginated list of active and non-deleted {@link InternalCamDigiKey} records,
     * filtered optionally by name, appKey, ipAddress, accessURL, and createdDate.
     *
     * @param page        the zero-based page index
     * @param size        the number of records per page
     * @param name        optional filter by name
     * @param appKey      optional filter by app key
     * @param ipAddress   optional filter by IP address
     * @param accessURL   optional filter by access URL
     * @param createdDate optional filter by creation date in "dd-MM-yyyy" format
     * @return a {@link Page} of {@link InternalCamDigiKey} entities matching the filters
     */
    @Override
    public Page<InternalCamDigiKey> getFilteredInternalCamDigiKeys(
            int page, int size,
            String name,
            String appKey,
            String ipAddress,
            String accessURL,
            String createdDate) {

        Pageable pageable = PageRequest.of(page, size);
        return internalCamDigiKeyRepository.findFilteredInternalCamDigiKeys(
                name, appKey, ipAddress, accessURL, createdDate, pageable);
    }
}