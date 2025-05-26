package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * Service interface for managing {@link Partner} entities.
 * Provides methods for creation, retrieval, and existence checks based on various criteria.
 */
public interface PartnerService {

    /**
     * Creates and saves a new {@link Partner}.
     *
     * @param partner the partner entity to be created
     * @return the saved {@link Partner} entity
     */
    Partner createPartner(Partner partner);

    /**
     * Retrieves a partner by its unique ID.
     *
     * @param id the ID of the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findById(Long id);

    /**
     * Retrieves a partner by its name.
     *
     * @param name the name of the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findByName(String name);

    /**
     * Retrieves a partner by its unique identifier.
     *
     * @param identifier the unique identifier of the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findByIdentifier(String identifier);

    /**
     * Retrieves a partner by its unique code.
     *
     * @param code the unique code associated with the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findByCode(String code);

    /**
     * Retrieves the ID of a partner by its RSA public key.
     *
     * @param publicKey the RSA public key associated with the partner
     * @return the ID of the partner, or {@code null} if not found
     */
    Long findIdByPublicKey(String publicKey);

    /**
     * Checks if a partner exists by its ID.
     *
     * @param id the ID of the partner
     * @return {@code true} if the partner exists, otherwise {@code false}
     */
    boolean existsById(Long id);

    /**
     * Retrieves a paginated list of all partners.
     *
     * @param page the page number (zero-based)
     * @param size the number of records per page
     * @return a {@link Page} of {@link Partner} entities
     */
    Page<Partner> getAllPartners(int page, int size);

    /**
     * Updates an existing {@link Partner} entity.
     *
     * @param partner the partner entity with updated fields
     * @return the updated {@link Partner} entity
     */
    Partner updatePartner(Partner partner);

    /**
     * Retrieves a paginated list of {@link Partner} entities that match the following criteria:
     * - isBank is true
     * - isOwn is false
     * The results are sorted by the identifier field in ascending order.
     *
     * @param page the zero-based page index to retrieve
     * @param size the number of records per page
     * @return a {@link Page} of filtered {@link Partner} entities
     */
    Page<Partner> getFilteredBankPartners(int page, int size);
}