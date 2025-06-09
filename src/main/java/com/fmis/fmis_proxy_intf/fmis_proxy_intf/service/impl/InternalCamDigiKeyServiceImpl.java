package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.InternalCamDigiKey;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.InternalCamDigiKeyRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.InternalCamDigiKeyService;
import jakarta.transaction.Transactional;
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
}