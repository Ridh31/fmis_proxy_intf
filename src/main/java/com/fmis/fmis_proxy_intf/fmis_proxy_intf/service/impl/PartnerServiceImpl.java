package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementation of PartnerService interface.
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

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

    public Long findByBase64(String base64) {
        return partnerRepository.findByBase64(base64)
                .map(Partner::getId) // Get the id of the Hint Code
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with hint code: " + base64));
    }
}
