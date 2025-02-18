package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository interface for managing CRUD operations on Partner entities.
 * Extends JpaRepository to provide built-in methods for database interaction.
 */
public interface PartnerRepository extends JpaRepository<Partner, Integer> {

    /**
     * Finds a Partner by its unique identifier.
     *
     * @param id the unique identifier of the partner
     * @return an Optional containing the Partner if found, or an empty Optional if not found
     */
    Optional<Partner> findById(Long id);

    /**
     * Finds a Partner by its unique code.
     *
     * @param code the unique code associated with the partner
     * @return an Optional containing the Partner if found, or an empty Optional if not found
     */
    Optional<Partner> findByCode(String code);

    /**
     * Finds a Partner by its base64 encoded representation.
     *
     * @param base64 the base64 representation of the partner
     * @return an Optional containing the Partner if found, or an empty Optional if not found
     */
    Optional<Partner> findByBase64(String base64);

    /**
     * Finds a Partner by its RSA public key.
     *
     * @param rsaPublicKey the RSA public key associated with the partner
     * @return an Optional containing the Partner if found, or an empty Optional if not found
     */
    Optional<Partner> findIdByRsaPublicKey(String rsaPublicKey);

    /**
     * Checks if a Partner with the specified ID exists.
     *
     * @param id the unique identifier of the partner
     * @return true if the Partner exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Retrieves all active and non-deleted Partners, sorted by ID in descending order.
     *
     * @param pageable pagination details
     * @return a paginated list of active and non-deleted Partners
     */
    @Query(
            value = "SELECT * FROM partner_intf pi2 WHERE pi2.status = TRUE AND pi2.is_deleted = FALSE ORDER BY pi2.id DESC",
            nativeQuery = true
    )
    Page<Partner> getAll(Pageable pageable);
}
