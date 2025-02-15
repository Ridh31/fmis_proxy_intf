package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;

import java.util.Optional;

/**
 * Service interface for managing Partner entities.
 * Provides methods to create and retrieve Partner entities based on different criteria.
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
     * Retrieves a Partner by its unique code.
     *
     * @param code the unique code associated with the partner
     * @return an Optional containing the Partner if found, or an empty Optional if not found
     */
    Optional<Partner> findByCode(String code);

    /**
     * Retrieves a Partner by its base64 encoded representation.
     *
     * @param base64 the base64 encoded string representing the partner
     * @return the ID of the Partner associated with the given base64 encoding
     */
    Long findIdByBase64(String base64);

    /**
     * Retrieves a Partner by its RSA public key.
     *
     * @param rsaPublicKey the RSA public key associated with the partner
     * @return the ID of the Partner associated with the given RSA public key
     */
    Long findIdByRsaPublicKey(String rsaPublicKey);
}