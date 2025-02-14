package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of PartnerService interface.
 * Provides methods to manage Partner entities.
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    // Constructor to inject PartnerRepository dependency
    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    /**
     * Creates and saves a new Partner entity.
     *
     * @param partner The partner entity to be saved.
     */
    @Transactional
    @Override
    public Partner createPartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    /**
     * Finds a Partner by its unique code.
     *
     * @param code The unique code of the partner.
     * @return An Optional containing the Partner, or empty if not found.
     */
    public Optional<Partner> findByCode(String code) {
        return partnerRepository.findByCode(code);
    }

    /**
     * Finds a Partner by its base64-encoded string.
     *
     * @param base64 The base64 string representing the partner.
     * @return The ID of the Partner if found.
     * @throws ResourceNotFoundException if the Partner is not found.
     */
    public Long findByBase64(String base64) {
        return partnerRepository.findByBase64(base64)
                .map(Partner::getId) // Get the id of the Hint Code
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with code: " + base64));
    }
}
