package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;

import java.util.Optional;

/**
 * Service interface for managing Partner entities.
 * Provides methods to create a Partner and retrieve it by its unique code or base64 encoding.
 */
public interface PartnerService {

    /**
     * Creates a new Partner entity.
     *
     * @param partner the Partner object to be created
     * @return the created Partner entity
     */
    Partner createPartner(Partner partner);

    /**
     * Finds a Partner by its unique code.
     *
     * @param code The unique code associated with the partner.
     * @return An Optional containing the Partner if found, or empty if not found.
     */
    Optional<Partner> findByCode(String code);

    /**
     * Finds a Partner by its base64 representation.
     *
     * @param base64 the base64 encoded string representing the partner
     * @return the ID of the Partner associated with the given base64 encoding
     */
    Long findByBase64(String base64);
}
