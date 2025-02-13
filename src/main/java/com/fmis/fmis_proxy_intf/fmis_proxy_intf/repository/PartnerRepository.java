package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on Partner entities.
 */
public interface PartnerRepository extends JpaRepository<Partner, Integer> {

    /**
     * Finds a Partner by its unique code.
     *
     * @param code the unique code associated with the partner
     * @return an Optional containing the Partner if found, or empty if not found
     */
    Optional<Partner> findByCode(String code);

    /**
     * Finds a Partner by its base64 representation.
     *
     * @param base64 the base64 representation of the partner
     * @return an Optional containing the Partner if found, or empty if not found
     */
    Optional<Partner> findByBase64(String base64);
}
