package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for managing CRUD operations on {@link Partner} entities.
 * Extends {@link JpaRepository} to provide basic data access functionality.
 */
public interface PartnerRepository extends JpaRepository<Partner, Integer> {

    /**
     * Finds a partner by its unique ID.
     *
     * @param id the unique identifier of the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findById(Long id);

    /**
     * Finds a partner by its name.
     *
     * @param name the name of the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findByName(String name);

    /**
     * Finds a partner by its unique identifier.
     *
     * @param identifier the identifier of the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findByIdentifier(String identifier);

    /**
     * Finds a partner by its system code.
     *
     * @param systemCode the system code associated with the partner
     * @return an Optional containing the partner if found, otherwise empty
     */
    Optional<Partner> findBySystemCode(String systemCode);

    /**
     * Finds a partner by its unique code.
     *
     * @param code the code associated with the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findByCode(String code);

    /**
     * Finds a partner by its RSA public key.
     *
     * @param publicKey the RSA public key associated with the partner
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    Optional<Partner> findIdByPublicKey(String publicKey);

    /**
     * Checks if a partner with the given ID exists.
     *
     * @param id the ID of the partner
     * @return {@code true} if the partner exists, otherwise {@code false}
     */
    boolean existsById(Long id);

    /**
     * Retrieves all active and non-deleted partners, sorted by ID in descending order.
     *
     * @param pageable the pagination and sorting information
     * @return a {@link Page} of active, non-deleted partners
     */
    @Query(value = """
        SELECT
            *
        FROM
            partner_intf pi2
        WHERE
            pi2.status = TRUE
            AND pi2.is_deleted = FALSE
        ORDER BY pi2.id DESC
        """, nativeQuery = true)
    Page<Partner> getAllPartners(Pageable pageable);

    /**
     * Retrieves the latest identifier among active, non-deleted partners.
     *
     * @return the most recent partner identifier, or {@code null} if none exist
     */
    @Query(value = """
        SELECT
            pi2.identifier
        FROM
            partner_intf pi2
        WHERE
            pi2.status = TRUE
            AND pi2.is_deleted = FALSE
        ORDER BY pi2.identifier DESC
        LIMIT 1
        """, nativeQuery = true)
    String findTopByOrderByIdentifierDesc();

    /**
     * Finds all {@link Partner} entities where:
     * - isBank is true
     * - isOwn is false
     *
     * Results are returned in a paginated format based on the provided {@link Pageable} object.
     * @param pageable the pagination and sorting information
     * @return a {@link Page} of {@link Partner} entities matching the criteria
     */
    Page<Partner> findByIsBankTrueAndIsOwnFalse(Pageable pageable);

    /**
     * Retrieves a paginated list of active and non-deleted {@link Partner} records,
     * filtered by optional parameters.
     *
     * @param name        optional filter by name
     * @param identifier  optional filter by identifier
     * @param systemCode  optional filter by system code
     * @param description optional filter by description
     * @param createdDate optional filter by creation date in "dd-MM-yyyy" format
     * @return a page of filtered {@link Partner} entities
     */
    @Query(value = """
        SELECT
            *
        FROM
            partner_intf pi
        WHERE
            pi.status = TRUE
            AND pi.is_deleted = FALSE
            AND pi.is_own = FALSE
            AND (:name IS NULL OR LOWER(pi.name) LIKE CONCAT('%', LOWER(:name), '%'))
            AND (:identifier IS NULL OR LOWER(pi.identifier) LIKE CONCAT('%', LOWER(:identifier), '%'))
            AND (:systemCode IS NULL OR LOWER(pi.system_code) LIKE CONCAT('%', LOWER(:systemCode), '%'))
            AND (:description IS NULL OR LOWER(pi.description) LIKE CONCAT('%', LOWER(:description), '%'))
            AND (:createdDate IS NULL OR pi.created_date >= :createdDate
                AND pi.created_date < DATE_ADD(:createdDate, INTERVAL 1 DAY))
        ORDER BY pi.id DESC
    """, nativeQuery = true)
    Page<Partner> findFilteredInternalCamDigiKeys(
            @Param("name") String name,
            @Param("identifier") String identifier,
            @Param("systemCode") String systemCode,
            @Param("description") String description,
            @Param("createdDate") LocalDate createdDate,
            Pageable pageable
    );
}