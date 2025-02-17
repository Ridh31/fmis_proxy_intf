package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link PartnerService} interface.
 * Provides methods to manage {@link Partner} entities.
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    /**
     * Constructs a new {@code PartnerServiceImpl} with the given repository.
     *
     * @param partnerRepository The repository for managing {@code Partner} entities.
     */
    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    /**
     * Creates and saves a new {@code Partner} entity.
     *
     * @param partner The {@code Partner} entity to be saved.
     * @return The saved {@code Partner} entity.
     */
    @Transactional
    @Override
    public Partner createPartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    /**
     * Finds a {@code Partner} by its ID.
     *
     * @param id The ID of the {@code Partner}.
     * @return An {@link Optional} containing the {@code Partner} if found, or empty if not found.
     */
    @Override
    public Optional<Partner> findById(Long id) {
        return partnerRepository.findById(id);
    }

    /**
     * Finds a {@code Partner} by its unique code.
     *
     * @param code The unique code associated with the {@code Partner}.
     * @return An {@link Optional} containing the {@code Partner}, or empty if not found.
     */
    @Override
    public Optional<Partner> findByCode(String code) {
        return partnerRepository.findByCode(code);
    }

    /**
     * Finds a {@code Partner} by its base64-encoded string.
     *
     * @param base64 The base64-encoded representation of the {@code Partner}.
     * @return The ID of the {@code Partner} if found.
     * @throws ResourceNotFoundException If the {@code Partner} is not found.
     */
    @Override
    public Long findIdByBase64(String base64) {
        return partnerRepository.findByBase64(base64)
                .map(Partner::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner with the provided base64 not found."));
    }

    /**
     * Finds a {@code Partner} by its RSA public key.
     *
     * @param rsaPublicKey The RSA public key associated with the {@code Partner}.
     * @return The ID of the {@code Partner} if found.
     * @throws ResourceNotFoundException If the {@code Partner} is not found.
     */
    @Override
    public Long findIdByRsaPublicKey(String rsaPublicKey) {
        return partnerRepository.findIdByRsaPublicKey(rsaPublicKey)
                .map(Partner::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner with the provided code not found."));
    }
}
