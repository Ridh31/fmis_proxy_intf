package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the PartnerService interface.
 * Provides methods to manage Partner entities.
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    /**
     * Constructor to inject PartnerRepository dependency.
     *
     * @param partnerRepository the repository to interact with Partner data.
     */
    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    /**
     * Creates and saves a new Partner entity.
     *
     * @param partner the Partner entity to be saved
     * @return the saved Partner entity
     */
    @Transactional
    @Override
    public Partner createPartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    /**
     * Finds a Partner by its unique code.
     *
     * @param code the unique code of the partner
     * @return an Optional containing the Partner, or empty if not found
     */
    @Override
    public Optional<Partner> findByCode(String code) {
        return partnerRepository.findByCode(code);
    }

    /**
     * Finds a Partner by its base64-encoded string.
     *
     * @param base64 the base64 string representing the partner
     * @return the ID of the Partner if found
     * @throws ResourceNotFoundException if the Partner is not found
     */
    @Override
    public Long findIdByBase64(String base64) {
        return partnerRepository.findByBase64(base64)
                .map(Partner::getId)  // Get the ID of the Partner
                .orElseThrow(() -> new ResourceNotFoundException("Partner code not found."));
    }

    /**
     * Finds a Partner by its RSA public key.
     *
     * @param rsaPublicKey the RSA public key associated with the partner
     * @return the ID of the Partner if found
     * @throws ResourceNotFoundException if the Partner is not found
     */
    @Override
    public Long findIdByRsaPublicKey(String rsaPublicKey) {
        return partnerRepository.findIdByRsaPublicKey(rsaPublicKey)
                .map(Partner::getId)  // Get the ID of the Partner
                .orElseThrow(() -> new ResourceNotFoundException("Partner code not found."));
    }
}
