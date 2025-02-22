package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository interface for managing CRUD operations on Partner entities.
 * Extends {@link JpaRepository} to provide built-in methods for database interaction.
 */
public interface PartnerRepository extends JpaRepository<Partner, Integer> {

    /**
     * Finds a Partner by its unique identifier.
     *
     * @param id The unique identifier of the partner.
     * @return An {@link Optional} containing the Partner if found, or an empty {@link Optional} if not found.
     */
    Optional<Partner> findById(Long id);

    /**
     * Finds a Partner by its unique code.
     *
     * @param code The unique code associated with the partner.
     * @return An {@link Optional} containing the Partner if found, or an empty {@link Optional} if not found.
     */
    Optional<Partner> findByCode(String code);

    /**
     * Finds a Partner by its RSA public key.
     *
     * @param publicKey The RSA public key associated with the partner.
     * @return An {@link Optional} containing the Partner if found, or an empty {@link Optional} if not found.
     */
    Optional<Partner> findIdByPublicKey(String publicKey);

    /**
     * Checks if a Partner with the specified ID exists.
     *
     * @param id The unique identifier of the partner.
     * @return {@code true} if the Partner exists, {@code false} otherwise.
     */
    boolean existsById(Long id);

    /**
     * Retrieves all active and non-deleted Partners, sorted by ID in descending order.
     *
     * @param pageable The {@link Pageable} object containing pagination and sorting details.
     * @return A {@link Page} of active and non-deleted Partners.
     */
    @Query(
            value = "SELECT * FROM partner_intf pi2 WHERE pi2.status = TRUE AND pi2.is_deleted = FALSE ORDER BY pi2.id DESC",
            nativeQuery = true
    )
    Page<Partner> getAllPartners(Pageable pageable);
}
